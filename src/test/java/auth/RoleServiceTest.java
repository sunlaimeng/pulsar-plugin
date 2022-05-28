package auth;

import auth.service.RoleService;
import auth.service.RoleServiceImpl;

/**
 * @author lamen 2022/5/28
 */
public class RoleServiceTest {

    public static void main(String[] args) throws Exception {
        String data = "{\"username\":\"0e7bd9443513f22ab9af\",\"password\":\"0f947542a9827af0\"}";
        RoleService roleService = new RoleServiceImpl();
        System.out.println(roleService.getRole(data));
    }
}
