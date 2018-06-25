package tk.k2zinger.aws;

import com.amazonaws.regions.Region;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.apache.http.HttpHeaders.ACCEPT;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.entity.ContentType.APPLICATION_XML;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.crypto.*","javax.net.ssl.*","javax.security.*"})
public class ApiGatewayClientTest {

    private HttpResponse hr;
    private Map<ApiGatewayParameters, String> params;

    @Before
    public void setup() throws Exception {
        HttpEntity he = mock(HttpEntity.class);
        when(he.getContent()).thenReturn(new ByteArrayInputStream("good request".getBytes()));

        StatusLine sl = mock(StatusLine.class);
        when(sl.getStatusCode()).thenReturn(200);

        hr = mock(HttpResponse.class);
        when(hr.getStatusLine()).thenReturn(sl);
        when(hr.getEntity()).thenReturn(he);

        params = new TreeMap(){{
            put(ApiGatewayParameters.URI, "https://amazonaws.com");
            put(ApiGatewayParameters.ACCESS_KEY, "access");
            put(ApiGatewayParameters.SECRET_KEY, "secret");
            put(ApiGatewayParameters.SECURITY_TOKEN, "123");
        }};
    }

    @Test
    public void test_defaults() throws Exception {
        ApiGatewayClient agc = spy(new ApiGatewayClient(params));
        String method = Whitebox.getInternalState(agc,"method");
        String service = Whitebox.getInternalState(agc,"service");
        Region region = Whitebox.getInternalState(agc, "region");
        assert(method.equals("GET"));
        assert(service.equals("execute-api"));
        assert(region.getName().equals("us-east-1"));
    }

    @Test
    public void test_good_header() throws Exception {
        ApiGatewayClient agc = spy(new ApiGatewayClient(params));
        List<Header> headers = Whitebox.getInternalState(agc,"headers");
        String token = "";
        for(Header h:headers) {
            if(h.getName().equalsIgnoreCase("x-amz-security-token"))
                token = h.getValue();
        }

        assert(token.equals("123"));
    }
}
