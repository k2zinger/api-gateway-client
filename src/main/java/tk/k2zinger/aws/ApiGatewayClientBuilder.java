package tk.k2zinger.aws;

import java.util.Map;
import java.util.TreeMap;

public class ApiGatewayClientBuilder {

    private Map<ApiGatewayParameters, String> params = new TreeMap();

    public static ApiGatewayClientBuilder standard() {
        return new ApiGatewayClientBuilder();
    }

    public ApiGatewayClientBuilder withUri(String uri) {
        params.put(ApiGatewayParameters.URI, uri);
        return this;
    }

    public ApiGatewayClientBuilder withMethod(String method) {
        params.put(ApiGatewayParameters.METHOD, method);
        return this;
    }

    public ApiGatewayClientBuilder withHeaders(String headers) {
        params.put(ApiGatewayParameters.HEADERS, headers);
        return this;
    }

    public ApiGatewayClientBuilder withBody(String body) {
        params.put(ApiGatewayParameters.BODY, body);
        return this;
    }

    public ApiGatewayClientBuilder withAccessKey(String access_key) {
        params.put(ApiGatewayParameters.ACCESS_KEY, access_key);
        return this;
    }

    public ApiGatewayClientBuilder withSecretKey(String secret_key) {
        params.put(ApiGatewayParameters.SECRET_KEY, secret_key);
        return this;
    }

    public ApiGatewayClientBuilder withSecurityToken(String security_token) {
        params.put(ApiGatewayParameters.SECURITY_TOKEN, security_token);
        return this;
    }

    public ApiGatewayClientBuilder withSessionToken(String session_token) {
        params.put(ApiGatewayParameters.SESSION_TOKEN, session_token);
        return this;
    }

    public ApiGatewayClientBuilder withApiKey(String api_key) {
        params.put(ApiGatewayParameters.API_KEY, api_key);
        return this;
    }

    public ApiGatewayClientBuilder withRegion(String region) {
        params.put(ApiGatewayParameters.REGION, region);
        return this;
    }

    public ApiGatewayClientBuilder withProfile(String profile) {
        params.put(ApiGatewayParameters.PROFILE, profile);
        return this;
    }

    public ApiGatewayClientBuilder withService(String service) {
        params.put(ApiGatewayParameters.SERVICE, service);
        return this;
    }

    public ApiGatewayClient build() {
        return new ApiGatewayClient(params);
    }

}
