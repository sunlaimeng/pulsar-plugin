package auth.client;

import org.apache.pulsar.client.api.Authentication;
import org.apache.pulsar.client.api.AuthenticationDataProvider;
import org.apache.pulsar.client.api.PulsarClientException;

import java.io.IOException;
import java.util.Map;

/**
 * @author lamen 2022/5/27
 */
public class DemoAuthentication implements Authentication {
    public static final String AUTH_METHOD_NAME = "demo_hello_world";
    private String accessId;
    private String accessKey;

    public DemoAuthentication() {
    }

    public DemoAuthentication(String accessId, String accessKey) {
        this.accessId = accessId;
        this.accessKey = accessKey;
    }

    @Override
    public String getAuthMethodName() {
        return AUTH_METHOD_NAME;
    }

    @Override
    public AuthenticationDataProvider getAuthData() throws PulsarClientException {
        return new DemoAuthenticationDataProvider(accessId, accessKey);
    }

    @Override
    public void configure(Map<String, String> authParams) {
        this.accessId = authParams.get("accessId");
        this.accessKey = authParams.get("accessKey");
    }

    @Override
    public void start() throws PulsarClientException {
    }

    @Override
    public void close() throws IOException {
    }
}
