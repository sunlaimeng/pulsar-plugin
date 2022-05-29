package auth.server;

import auth.client.DemoAuthentication;
import auth.client.DemoAuthenticationDataProvider;
import auth.service.RoleService;
import auth.service.RoleServiceImpl;
import org.apache.pulsar.broker.ServiceConfiguration;
import org.apache.pulsar.broker.authentication.AuthenticationDataSource;
import org.apache.pulsar.broker.authentication.AuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.util.Set;

/**
 * @author lamen 2022/5/27
 */
public class DemoAuthenticationProvider implements AuthenticationProvider {

    private static final Logger logger = LoggerFactory.getLogger(DemoAuthenticationProvider.class);
    private static RoleService roleService = new RoleServiceImpl();

    @Override
    public void initialize(ServiceConfiguration config) throws IOException {
        if (config == null) {
            return;
        }
        Set<String> superRoles = config.getSuperUserRoles();
        logger.info("Super roles: {}", superRoles);
    }

    @Override
    public String getAuthMethodName() {
        return DemoAuthentication.AUTH_METHOD_NAME;
    }

    @Override
    public String authenticate(AuthenticationDataSource authData) throws AuthenticationException {
        String data;
        if (authData.hasDataFromCommand()) {
            data = authData.getCommandData();
            logger.info("FromCommand, data: {}", data);
        } else if (authData.hasDataFromHttp()) {
            data = authData.getHttpHeader(DemoAuthenticationDataProvider.HTTP_HEADER_NAME);
            logger.info("FromHttp, data: {}", data);
        } else {
            throw new AuthenticationException("authenticate failed");
        }
        String role = roleService.getRole(data);
        logger.info("Authenticate role: {}", role);
        return role;
    }

    @Override
    public void close() throws IOException {
    }
}
