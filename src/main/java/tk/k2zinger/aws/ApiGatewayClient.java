package tk.k2zinger.aws;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;

import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.entity.ContentType.APPLICATION_XML;

public class ApiGatewayClient {

    private URI uri;
    private String method, body, access_key, secret_key, security_token, api_key, service;
    private List<Header> headers = new ArrayList(){{
            add(new BasicHeader(ACCEPT,APPLICATION_XML.getMimeType()));
            add(new BasicHeader(CONTENT_TYPE,APPLICATION_JSON.getMimeType()));
    }};
    private Region region;

    public ApiGatewayClient(Map<ApiGatewayParameters, String> params) {

        parseUri(getArg(params.get(ApiGatewayParameters.URI)));

        method = getArg(params.get(ApiGatewayParameters.METHOD), HttpGet.METHOD_NAME);

        parseHeaders(getArg(params.get(ApiGatewayParameters.HEADERS)));

        body = getArg(params.get(ApiGatewayParameters.BODY));

        parseCredentials(getArg(params.get(ApiGatewayParameters.ACCESS_KEY)),
                getArg(params.get(ApiGatewayParameters.SECRET_KEY)),
                getArg(params.get(ApiGatewayParameters.PROFILE), getEnvVar("AWS_PROFILE")));

        security_token = getArg(params.get(ApiGatewayParameters.SECURITY_TOKEN),
                getArg(params.get(ApiGatewayParameters.SESSION_TOKEN),
                        getEnvVar("AWS_SECURITY_TOKEN",
                                getEnvVar("AWS_SESSION_TOKEN"))));

        api_key = getArg(params.get(ApiGatewayParameters.API_KEY));

        parseRegion(getArg(params.get(ApiGatewayParameters.REGION), getEnvVar("AWS_DEFAULT_REGION", "us-east-1")));

        service = getArg(params.get(ApiGatewayParameters.SERVICE), "execute-api");

        headers.addAll(ApiGatewayRequestHeaders.calculateHeaders(uri, body, access_key, secret_key, security_token,
                api_key, region.getName(), service));
    }

    private void parseUri(String uri) {
        try {
            if(uri.isEmpty())
                throw new URISyntaxException("","No URI provided");
            this.uri = new URI(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid uri: " + e);
        }
    }

    private String getArg(String arg, String def) {
        return arg != null ? arg : def;
    }

    private String getArg(String arg) {
        return getArg(arg, "");
    }

    private void parseHeaders(String headers) {
        if(!headers.isEmpty())
            try {
                for (String header : headers.replaceAll("\"", "").replaceAll("'", "").split(",")) {
                    String[] t = header.split(":");
                    this.headers.add(new BasicHeader(t[0].trim(), t[1].trim()));
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid headers:" + e);
            }
    }

    private void parseCredentials(String access_key, String secret_key, String profile) {
        if(!access_key.isEmpty() && !secret_key.isEmpty()) {
            this.access_key = access_key;
            this.secret_key = secret_key;
        } else {
            AWSCredentialsProviderChain credentials = new AWSCredentialsProviderChain(
                    new ProfileCredentialsProvider(profile), new DefaultAWSCredentialsProviderChain());
            this.access_key = credentials.getCredentials().getAWSAccessKeyId();
            this.secret_key = credentials.getCredentials().getAWSSecretKey();
        }

    }

    private String getEnvVar(String key, String def) {
        return System.getenv(key) != null ? System.getenv(key) : def;
    }

    private String getEnvVar(String key) {
        return getEnvVar(key, "");
    }

    private void parseRegion(String region) {
        this.region = RegionUtils.getRegion(region);
    }

    public ApiGatewayResponse sendRequest() {
        HttpClient client = HttpClientBuilder.create().build();
        HttpRequestBase request;
        if(method.equalsIgnoreCase(HttpGet.METHOD_NAME)) {
            request = new HttpGet(uri);
        } else if(method.equalsIgnoreCase(HttpPost.METHOD_NAME)) {
            request = new HttpPost(uri);
            try {
                ((HttpPost)request).setEntity(new StringEntity(body));
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        } else {
            throw new IllegalArgumentException("Method: " + method + " not implemented");
        }

        request.setHeaders(headers.toArray(new Header[headers.size()]));

        try {
            return new ApiGatewayResponse(client.execute(request));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
