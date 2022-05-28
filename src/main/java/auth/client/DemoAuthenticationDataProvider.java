package auth.client;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.pulsar.client.api.AuthenticationDataProvider;

/**
 * @author lamen 2022/5/27
 */
public class DemoAuthenticationDataProvider implements AuthenticationDataProvider {

    private String commandData;

    public DemoAuthenticationDataProvider(String accessId, String accessKey) {
        commandData = String.format("{\"username\":\"%s\",\"password\":\"%s\"}",
                accessId, DigestUtils.md5Hex(accessId + DigestUtils.md5Hex(accessKey)).substring(8, 24));
    }

    @Override
    public boolean hasDataForHttp() {
        return false;
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
