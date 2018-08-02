package tk.k2zinger.aws;

import org.apache.http.Header;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.net.URI;
import java.util.Date;
import java.util.List;

import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ApiGatewayRequestHeaders.class)
@PowerMockIgnore({"javax.crypto.*" })
public class ApiGatewayRequestHeadersTest {

    @Before
    public void setup() {
        spy(ApiGatewayRequestHeaders.class);
        when(ApiGatewayRequestHeaders.now()).thenReturn(new Date(0));
    }

    @Test
    public void test_body_hash() throws Exception {
        List<Header> headers = ApiGatewayRequestHeaders.calculateHeaders(new URI("https://amazonaws.com"), "POST", "body", "access", "secret", "", "", "us-east-1", "execute-api");
        String content_hash = "";
        for(Header h:headers) {
            if(h.getName().equalsIgnoreCase("x-amz-content-sha256"))
                content_hash = h.getValue();
        }

        assert(content_hash.equals("230d8358dc8e8890b4c58deeb62912ee2f20357ae92a5cc861b98e68fe31acb5"));
    }

    @Test
    public void test_known_signature() throws Exception {
        List<Header> headers = ApiGatewayRequestHeaders.calculateHeaders(new URI("https://amazonaws.com"), "GET", "", "access", "secret", "", "", "us-east-1", "execute-api");
        String signature = "";
        for(Header h:headers) {
            if(h.getName().equalsIgnoreCase(AUTHORIZATION))
                signature = h.getValue().split(",")[2].split("=")[1];
        }

        assert(signature.equals("9a05e1d2180fd610e750da4b64cc36addd9e4ffa815a4f3d3c8e79473ead93cb"));
    }

}
