package auth.service;

import auth.bean.User;
import auth.util.DBUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.AuthenticationException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author lamen 2022/5/28
 */
public class RoleServiceImpl implements RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Override
    public String getRole(String data) throws AuthenticationException {
        try {
            JsonObject jsonObject = new Gson().fromJson(data, JsonObject.class);
            String username = jsonObject.get("username").getAsString();
            String password = jsonObject.get("password").getAsString();
            User user = getUser(username);
            if (user == null) {
                throw new AuthenticationException("User not found");
            }
            String accessId = user.getAccessId();
            String accessKey = user.getAccessKey();
            String originalPassword = DigestUtils.md5Hex(accessId + DigestUtils.md5Hex(accessKey)).substring(8, 24);
            if (password.equals(originalPassword)) {
                return user.getRole();
            }
        } catch (Exception e) {
            logger.error("Get role failed.", e);
        }
        throw new AuthenticationException("Invalid authentication");
    }

    @Override
    public String getRole(Set<String> superRoles) throws AuthenticationException {
        if (superRoles != null && superRoles.size() > 0) {
            for (String role : superRoles) {
                return role;
            }
        }
        throw new AuthenticationException("Super role empty");
    }

    private User getUser(String accessId) {
        Connection conn = DBUtil.getConnection();
        String sql = "select access_id, access_key, `role` from `user` where access_id = ?";
        ResultSet resultSet = null;
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, accessId);
            resultSet = statement.executeQuery();
            while(resultSet.next()){
                User user = new User();
                user.setAccessId(resultSet.getString("access_id"));
                user.setAccessKey(resultSet.getString("access_key"));
                user.setRole(resultSet.getString("role"));
                return user;
            }
        } catch (Exception e) {
            logger.error("Execute sql failed.", e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    logger.error("ResultSet close failed.", e);
                }
            }
        }
        return null;
    }
}
