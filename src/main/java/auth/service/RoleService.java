package auth.service;

import javax.naming.AuthenticationException;
import java.util.Set;

/**
 * @author lamen 2022/5/28
 */
public interface RoleService {

    String getRole(String data) throws AuthenticationException;

    String getRole(Set<String> superRoles) throws AuthenticationException;
}
