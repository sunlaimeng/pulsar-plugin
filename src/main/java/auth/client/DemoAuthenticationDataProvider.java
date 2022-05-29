package auth.client;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.pulsar.client.api.AuthenticationDataProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author lamen 2022/5/27
 */
public class DemoAuthenticationDataProvider implements AuthenticationDataProvider {
    public static final String HTTP_HEADER_NAME = "Authorization";
    private String commandData;
    private Map<String, String> headers = new HashMap<>();

    public DemoAuthenticationDataProvider(String accessId, String accessKey) {
        commandData = String.format("{\"username\":\"%s\",\"password\":\"%s\"}",
                accessId, DigestUtils.md5Hex(accessId + DigestUtils.md5Hex(accessKey)).substring(8, 24));
        headers.put(HTTP_HEADER_NAME, commandData);
    }

    @Override
    public boolean hasDataForHttp() {
        return true;
    }

    @Override
    public Set<Map.Entry<String, String>> getHttpHeaders() throws Exception {
        return headers.entrySet();
    }

    @Override
    public boolean hasDataFromCommand() {
        return true;
    }

    @Override
    public String getCommandData() {
        return commandData;
    }
}
