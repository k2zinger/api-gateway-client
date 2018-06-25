package tk.k2zinger.aws;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ApiGatewayResponse {

    private int response_code;
    private String content;
    private JsonNode content_json;

    public ApiGatewayResponse(HttpResponse response) throws IOException {
        StatusLine sl = response.getStatusLine();
        response_code = response.getStatusLine().getStatusCode();
        content = readContent(response.getEntity().getContent()).toString();
    }

    private StringBuffer readContent(InputStream content) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(content));
        StringBuffer sb = new StringBuffer();
        String s;
        while ((s = br.readLine()) != null) {
            sb.append(s);
        }
        return sb;
    }

    public int getResponseCode() {
        return response_code;
    }

    public String getContent() {
        return content;
    }

    public JsonNode getContentAsJson() throws IOException {
        return content_json != null ? content_json : new ObjectMapper().readTree(content);
    }

}
