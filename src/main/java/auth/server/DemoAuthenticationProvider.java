package auth.server;

import auth.common.GlobalConstants;
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
    private Set<String> superRoles;

    @Override
    public void initialize(ServiceConfiguration config) throws IOException {
        if (config == null) {
            return;
        }
        superRoles = config.getSuperUserRoles();
        logger.info("Super roles: {}", superRoles);
    }

    @Override
    public String getAuthMethodName() {
        return GlobalConstants.AUTH_METHOD_NAME;
    }

    @Override
    public String authenticate(AuthenticationDataSource authData) throws AuthenticationException {
        if (authData.hasDataFromCommand()) {
            String data = authData.getCommandData();
            String role = roleService.getRole(data);
            logger.info("Command authenticate role: {}", role);
            return role;
        } else if (authData.hasDataFromHttp()) {
            String role = roleService.getRole(superRoles);
            logger.info("Http authenticate role: {}", role);
            return role;
        }
        throw new AuthenticationException("authenticate failed");
    }

    @Override
    public void close() throws IOException {
    }
}
