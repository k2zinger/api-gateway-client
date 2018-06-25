package tk.k2zinger.aws;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.http.HttpHeaders.AUTHORIZATION;

class ApiGatewayRequestHeaders {

    static List<Header> calculateHeaders(URI uri, String body, String access_key, String secret_key,
                                         String security_token, String api_key, String region, String service) {
        Date t = now();
        String amzdate = formatDate(t, "yyyyMMdd'T'HHmmss'Z'");
        String datestamp = formatDate(t, "yyyyMMdd");

        String canonical_headers = "host:" + uri.getHost() + "\n" + "x-amz-date:" + amzdate + "\n" +
                (!security_token.isEmpty() ? "x-amz-security-token:" + security_token + "\n" : "");

        String signed_headers = "host;x-amz-date" +
                (!security_token.isEmpty() ? ";x-amz-security-token" : "");

        String body_hash = sha256_hash(body);

        String query = uri.getRawQuery() != null ? uri.getRawQuery() : "";

        String canonical_request = "GET" + "\n" +
                uri.getPath() + "\n" +
                query + "\n" +
                canonical_headers + "\n" +
                signed_headers + "\n" +
                body_hash;

        String algorithm = "AWS4-HMAC-SHA256";

        String signature_scope = datestamp + "/" +
                region + "/" +
                service + "/" +
                "aws4_request";

        String string_to_sign = algorithm + "\n" +
                amzdate + "\n" +
                signature_scope + "\n" +
                sha256_hash(canonical_request);

        byte[] signing_key = getSignatureKey(secret_key, datestamp, region, service);

        String signature = bytesToHexString(HmacSHA256(string_to_sign, signing_key));

        String authorization_header = algorithm + " " +
                "Credential=" + access_key + "/" + signature_scope + ", " +
                "SignedHeaders=" + signed_headers + ", " +
                "Signature=" + signature;

        return new ArrayList(){{
            add(new BasicHeader(AUTHORIZATION, authorization_header));
            add(new BasicHeader("x-amz-date", amzdate));
            add(new BasicHeader("x-amz-content-sha256", body_hash));
            if (!security_token.isEmpty())
                add(new BasicHeader("x-amz-security-token", security_token));
            if (!api_key.isEmpty())
                add(new BasicHeader("x-api-key", api_key));
        }};
    }

    static Date now() {
        return new Date();
    }

    private static String formatDate(Date d, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(d);
    }

    private static String sha256_hash(String val) {
        try {
            return bytesToHexString(MessageDigest.getInstance("SHA-256").digest(val.getBytes(UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new MissingResourceException(e.toString(), "MessageDigest", "SHA-256");
        }
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            sb.append(hex.length() == 1 ? "0" + hex : hex);
        }
        return sb.toString();
    }

    private static byte[] HmacSHA256(String data, byte[] key) {
        String algorithm = "HmacSHA256";
        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key, algorithm));
            return mac.doFinal(data.getBytes(UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new MissingResourceException(e.toString(), "Mac", algorithm);
        } catch (InvalidKeyException e) {
            throw new MissingResourceException(e.toString(), "Key", algorithm);
        }
    }

    private static byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) {
        byte[] secret = ("AWS4" + key).getBytes(UTF_8);
        byte[] date = HmacSHA256(dateStamp, secret);
        byte[] region = HmacSHA256(regionName, date);
        byte[] service = HmacSHA256(serviceName, region);
        return HmacSHA256("aws4_request", service);
    }

}
