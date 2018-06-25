package tk.k2zinger.aws;

import org.junit.Test;

public class ApiGatewayClientBuilderTest {
    @Test
    public void test_standard() throws Exception {
        ApiGatewayClientBuilder.standard()
                .withUri("https://amazonaws.com")
                .withAccessKey("access")
                .withSecretKey("secret")
                .build();
    }

    @Test
    public void test_no_uri() throws Exception {
        try {
            ApiGatewayClientBuilder.standard().build();
        } catch(Exception e) {
            e.printStackTrace();
            assert(e.getMessage().indexOf("No URI provided") > -1);
        }
    }

}
