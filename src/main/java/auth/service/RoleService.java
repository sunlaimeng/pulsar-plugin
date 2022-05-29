package auth.service;

import javax.naming.AuthenticationException;

/**
 * @author lamen 2022/5/28
 */
public interface RoleService {

    String getRole(String data) throws AuthenticationException;
}
