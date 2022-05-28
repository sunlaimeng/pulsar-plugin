# Pulsar使用文档



## 安装Pulsar

1. 安装vim命令

```
apt-get update
apt-get install vim
```

2. 基于docker安装启动Pulsar

```
docker run -d -it -p 80:80 -p 8080:8080 -p 6650:6650 apachepulsar/pulsar-standalone
```



### Pulsar开启JWT认证授权

```
非对称秘钥：包含由私钥和公钥组成的一对密钥，使用Private key生成Token，使用Public key验证Token。
```

1. 生成密钥对

```
# bin/pulsar tokens create-key-pair --output-private-key ./conf/jwt-private.key --output-public-key ./conf/jwt-public.key
```

2. 使用私钥创建一个管理员用户的Token

```
# bin/pulsar tokens create --private-key ./conf/jwt-private.key --subject admin

eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhZG1pbiJ9.dcPcKzFF9x82bmq14VvDJbZqCou2cDhbzjQ_TUZI6A2QWp7wl2NdzpDWbUoe_5puJKe1eNksXRIknJpqBX8aQTMkuSACeLm47L7ea-AT13Qr6VvcJ8fVg3k_-UdwpFl6izMFqTeR2XWTgCASikBYxx1pmRdVMiWQnhsWxdWCWJonbL9bVyQpBqeThksrnTggQQguKC9sRtrNugl24mkG7gegIojXRG35gA-BCyi3P2o49QM0WdUnm5c5BrdMgV3zYvv16dRpy3EsHT-Wmu0ZQJ-aVJlkrAHG7N-EMAvUSLhCGdzyXuYu1KYQmLnEdnDoM9jtfmX63T5Z3btdlvCG_w
```

3. 使用私钥创建一个普通用户的Token

```
# bin/pulsar tokens create --private-key ./conf/jwt-private.key --subject test-user

eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ0ZXN0LXVzZXIifQ.Q9cMceksPMAUAxV1b7QcocPku03Ab27jYyblqJa221h6iN45DCnLzeapaen-W04L8cU_Ca3uMCiPSx96f9Xb34qYqRiTSDPmRmegjBJ-khBIyM9pO1dak4a7xXLSLxywWS_qIQZwupAQ-J7qlU6i0G8eynUY_ot6WzMD1Y6Prb27p-eu5vzX0AMSMjHVreZWa_FZPW5ZwRdTT9pGVWoixuUQLGB4TKa2dd43-DwMSFeOH27yU5gYKlXQJwdcXB2K0H3Sm86elhoLlAUPY6UnObsumR-4UYWdreAerZ3NM0w1WRT8XqHhHbCVFz2hUdPfc32s53qAvscb7vX8D1FZgg
```

4. 对普通用户test-user进行授权

```
# bin/pulsar-admin namespaces grant-permission my-tenant/my-namespace --role test-user --actions produce,consume
```

5. broker.conf配置

```properties
# broker对原始的Auth数据进行身份验证
authenticateOriginalAuthData=true
# 启用认证和授权
authenticationEnabled=true
authorizationEnabled=true
# 可以提供N个处理验证的处理类，然后broker接收到客户端连接后就会调用此类的方法进行处理
authenticationProviders=org.apache.pulsar.broker.authentication.AuthenticationProviderToken
# 指定admin用户为超级用户
superUserRoles=admin
# 设置broker自身的认证, broker连接其他broker时用到
brokerClientAuthenticationPlugin=org.apache.pulsar.client.impl.auth.AuthenticationToken
# 使用私钥创建的管理员用户的Token
brokerClientAuthenticationParameters={"token":"eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhZG1pbiJ9.dcPcKzFF9x82bmq14VvDJbZqCou2cDhbzjQ_TUZI6A2QWp7wl2NdzpDWbUoe_5puJKe1eNksXRIknJpqBX8aQTMkuSACeLm47L7ea-AT13Qr6VvcJ8fVg3k_-UdwpFl6izMFqTeR2XWTgCASikBYxx1pmRdVMiWQnhsWxdWCWJonbL9bVyQpBqeThksrnTggQQguKC9sRtrNugl24mkG7gegIojXRG35gA-BCyi3P2o49QM0WdUnm5c5BrdMgV3zYvv16dRpy3EsHT-Wmu0ZQJ-aVJlkrAHG7N-EMAvUSLhCGdzyXuYu1KYQmLnEdnDoM9jtfmX63T5Z3btdlvCG_w"}
# 公钥文件位置
tokenPublicKey=/pulsar/conf/jwt-public.key
```

```
注意：集群部署的Pulsar，在Broker上开启认证修改的是各个Broker节点的broker.conf配置文件；
对于使用docker容器启动的单机Pulsar，需要修改的配置文件是standalone.conf。
```



### PulsarAdmin开启认证

1. client.conf配置

```properties
webServiceUrl=http://localhost:8080/
brokerServiceUrl=pulsar://localhost:6650/
authPlugin=org.apache.pulsar.client.impl.auth.AuthenticationToken
authParams={"token":"eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhZG1pbiJ9.dcPcKzFF9x82bmq14VvDJbZqCou2cDhbzjQ_TUZI6A2QWp7wl2NdzpDWbUoe_5puJKe1eNksXRIknJpqBX8aQTMkuSACeLm47L7ea-AT13Qr6VvcJ8fVg3k_-UdwpFl6izMFqTeR2XWTgCASikBYxx1pmRdVMiWQnhsWxdWCWJonbL9bVyQpBqeThksrnTggQQguKC9sRtrNugl24mkG7gegIojXRG35gA-BCyi3P2o49QM0WdUnm5c5BrdMgV3zYvv16dRpy3EsHT-Wmu0ZQJ-aVJlkrAHG7N-EMAvUSLhCGdzyXuYu1KYQmLnEdnDoM9jtfmX63T5Z3btdlvCG_w"}
```

```
注意：authParams中的token配置的是admin的token。
```



## 安装Pulsar-Manager

1. 安装curl命令

```
# apk update

# vi /etc/apk/repositories
在首行添加
http://mirrors.ustc.edu.cn/alpine/v3.3/main

# apk add curl
```

2. 基于docker安装启动Pulsar-Manager

```
docker run -dit -p 9527:9527 -p 7750:7750 -e SPRING_CONFIGURATION_FILE=/pulsar-manager/pulsar-manager/application.properties --link crazy_darwin apachepulsar/pulsar-manager:v0.2.0

其中，--link表示与其他容器通信，crazy_darwin是启动Pulsar容器名称
```

3. 添加账号

```
# CSRF_TOKEN=$(curl http://localhost:7750/pulsar-manager/csrf-token)

# curl -H 'X-XSRF-TOKEN: $CSRF_TOKEN' -H 'Cookie: XSRF-TOKEN=$CSRF_TOKEN;' -H "Content-Type: application/json" -X PUT http://localhost:7750/pulsar-manager/users/superuser -d '{"name": "admin", "password": "apachepulsar", "description": "test", "email": "username@test.org"}'
```

4. 登录

```
http://127.0.0.1:9527
```

5. 配置

```
Environment Name：test
Service URL：http://172.17.0.2:8080

其中，172.17.0.2是启动Pulsar容器ip

查看Pulsar的ip，crazy_darwin是启动Pulsar容器名称
docker inspect --format='{{.NetworkSettings.IPAddress}}' crazy_darwin
```




### Pulsar-Manager开启认证

1. 从Pulsar容器copy密钥文件到本地

```
docker cp 14a8f7a6ad25:/pulsar/conf/jwt-private.key .
docker cp 14a8f7a6ad25:/pulsar/conf/jwt-public.key .
```

2. 将密钥文件从本地copy到Pulsar-Manager容器

```
docker cp D:\jwt-key\jwt-private.key c0a351d25157:/pulsar-manager/pulsar-manager/
docker cp D:\jwt-key\jwt-public.key c0a351d25157:/pulsar-manager/pulsar-manager/
```

3. application.properties配置

```properties
backend.jwt.token=eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhZG1pbiJ9.dcPcKzFF9x82bmq14VvDJbZqCou2cDhbzjQ_TUZI6A2QWp7wl2NdzpDWbUoe_5puJKe1eNksXRIknJpqBX8aQTMkuSACeLm47L7ea-AT13Qr6VvcJ8fVg3k_-UdwpFl6izMFqTeR2XWTgCASikBYxx1pmRdVMiWQnhsWxdWCWJonbL9bVyQpBqeThksrnTggQQguKC9sRtrNugl24mkG7gegIojXRG35gA-BCyi3P2o49QM0WdUnm5c5BrdMgV3zYvv16dRpy3EsHT-Wmu0ZQJ-aVJlkrAHG7N-EMAvUSLhCGdzyXuYu1KYQmLnEdnDoM9jtfmX63T5Z3btdlvCG_w
jwt.broker.token.mode=PRIVATE
jwt.broker.public.key=/pulsar-manager/pulsar-manager/jwt-public.key
jwt.broker.private.key=/pulsar-manager/pulsar-manager/jwt-private.key
```



## 命令行操作

### 租户

1. 查询租户列表

```
# bin/pulsar-admin tenants list
```

2. 创建租户

```
# bin/pulsar-admin tenants create my-tenant
```

3. 删除租户

```
# bin/pulsar-admin tenants delete my-tenant
```

4. 获取配置

```
# bin/pulsar-admin tenants get my-tenant
```

5. 更新配置

```
# bin/pulsar-admin tenants update my-tenant
```



### 命名空间

1. 查询命名空间列表

```
# bin/pulsar-admin namespaces list my-tenant
```

2. 创建命名空间

```
# bin/pulsar-admin namespaces create my-tenant/my-namespace
```

3. 删除命名空间

```
# bin/pulsar-admin namespaces delete my-tenant/my-namespace
```

4. 获取配置

```
# bin/pulsar-admin namespaces policies my-tenant/my-namespace
```



### Topic

1. 查询topic

```
# bin/pulsar-admin topics list my-tenant/my-namespace
```

2. 创建topic - 无分区

```
# bin/pulsar-admin topics create persistent://my-tenant/my-namespace/my-topic
```

3. 创建topic - 有分区

```
# bin/pulsar-admin topics create-partitioned-topic persistent://my-tenant/my-namespace/my-topic  --partitions 3
```

4. 删除topic - 无分区

```
# bin/pulsar-admin topics delete persistent://my-tenant/my-namespace/my-topic
```

5. 删除topic - 有分区

```
# bin/pulsar-admin topics delete-partitioned-topic persistent://my-tenant/my-namespace/my-topic
```

6. 更新topic

```
# bin/pulsar-admin topics update-partitioned-topic persistent://my-tenant/my-namespace/my-topic --partitions 8
```



### 角色

1. 创建角色 test-user

```
# bin/pulsar tokens create --private-key ./conf/jwt-private.key --subject test-user
```



### 权限

1. 创建 test-user 的权限：生产、消费

```
# bin/pulsar-admin namespaces grant-permission public/default --role test-user --actions produce,consume
```

2. 创建 test-user 的权限：生产

```
# bin/pulsar-admin namespaces grant-permission public/default --role test-user --actions produce
```

3. 创建 test-user 的权限：消费

```
# bin/pulsar-admin namespaces grant-permission public/default --role test-user --actions consume
```

4. 查看角色对应权限

```
# bin/pulsar-admin topics permissions persistent://public/default/my-topic
```



## 自定义认证插件

```
注意：AUTH_METHOD_NAME，该值服务端和客户端一定要一致，否则认证失败。
```

### 代码

#### bean

```java
package auth.bean;

/**
 * @author lamen 2022/5/28
 */
public class User {
    private String account;
    private String accessId;
    private String accessKey;
    private String role;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
```

#### client

```java
package auth.client;

import auth.common.GlobalConstants;
import org.apache.pulsar.client.api.Authentication;
import org.apache.pulsar.client.api.AuthenticationDataProvider;
import org.apache.pulsar.client.api.PulsarClientException;

import java.io.IOException;
import java.util.Map;

/**
 * @author lamen 2022/5/27
 */
public class DemoAuthentication implements Authentication {

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
        return GlobalConstants.AUTH_METHOD_NAME;
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
```

```java
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
```

#### common

```java
package auth.common;

/**
 * @author lamen 2022/5/27
 */
public interface GlobalConstants {

    String AUTH_METHOD_NAME = "demo_hello_world";
}
```

#### server

```java
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

    @Override
    public void initialize(ServiceConfiguration config) throws IOException {
        if (config == null) {
            return;
        }
        Set<String> superRoles = config.getSuperUserRoles();
        if (superRoles == null) {
            return;
        }
        for (String role : superRoles) {
            logger.info("initialize, method name: {}, super role: {}", GlobalConstants.AUTH_METHOD_NAME, role);
        }
    }

    @Override
    public String getAuthMethodName() {
        return GlobalConstants.AUTH_METHOD_NAME;
    }

    @Override
    public String authenticate(AuthenticationDataSource authData) throws AuthenticationException {
        logger.info("-----------------hasDataFromCommand: {}, hasDataFromHttp: {}", authData.hasDataFromCommand(), authData.hasDataFromHttp());
        if (authData.hasDataFromCommand()) {
            String data = authData.getCommandData();
            String role = roleService.getRole(data);
            logger.info("authenticate role: {}", role);
            return role;
        } else if (authData.hasDataFromHttp()) {
            String httpHeader = authData.getHttpHeader("X-Pulsar-Auth-Method-Name");
            logger.info("---------------------------httpHeader: {}", httpHeader);
            return "admin";
        }
        throw new AuthenticationException("authenticate failed");
    }

    @Override
    public void close() throws IOException {
    }
}
```

#### service

```java
package auth.service;

import javax.naming.AuthenticationException;

/**
 * @author lamen 2022/5/28
 */
public interface RoleService {

    String getRole(String data) throws AuthenticationException;
}
```

```java
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
```

#### util

```java
package auth.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author lamen 2022/5/28
 */
public class DBUtil implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);
    private static final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://192.168.1.6:3306/pulsar?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static Connection connection = null;

    static {
        try {
            Class.forName(DRIVER_NAME);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            logger.error("driver init failed.", e);
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                logger.error("driver close failed.", e);
            }
        }
    }
}
```

```java
package auth.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author lamen 2022/5/27
 */
public abstract class HttpURLConnectionUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpURLConnectionUtil.class);
    private static final int CONNECT_TIME_OUT = 10000;
    private static final int READ_TIME_OUT = 10000;
    private static final int SUCCESS_CODE = 200;
    private static final String CHARSET_NAME = "UTF-8";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    /**
     * get请求
     * @param httpUrl 地址
     * @return
     */
    public static String doGet(String httpUrl) {
        HttpURLConnection connection = null;
        try {
            connection = initHttpURLConnection(httpUrl, RequestMethod.GET);
            return getResult(connection);
        } catch (Exception e) {
            logger.error("request get failed.", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return "";
    }

    /**
     * post请求
     * @param httpUrl 地址
     * @param param 参数
     * @return
     */
    public static String doPost(String httpUrl, String param) {
        HttpURLConnection connection = null;
        try {
            connection = initHttpURLConnection(httpUrl, RequestMethod.POST);
            setParam(connection, param);
            return getResult(connection);
        } catch (Exception e) {
            logger.error("request post failed.", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return "";
    }

    /**
     * 设置参数
     */
    private static void setParam(HttpURLConnection connection, String param) {
        OutputStream os = null;
        try {
            if (param != null && !param.equals("")) {
                os = connection.getOutputStream();
                os.write(param.getBytes(CHARSET_NAME));
            }
        } catch (Exception e) {
            logger.error("request set param failed.", e);
        } finally {
            closeQuietly(os);
        }
    }

    /**
     * 初始化连接
     * @param httpUrl 地址
     * @param method 请求方式（GET、POST）
     * @return
     * @throws Exception
     */
    private static HttpURLConnection initHttpURLConnection(String httpUrl, RequestMethod method) throws Exception {
        URL url = new URL(httpUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method.name());
        connection.setConnectTimeout(CONNECT_TIME_OUT);
        connection.setReadTimeout(READ_TIME_OUT);
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
        return connection;
    }

    /**
     * 获取结果
     * @param connection
     * @return
     */
    private static String getResult(HttpURLConnection connection) {
        StringBuilder result = new StringBuilder();
        InputStream is = null;
        BufferedReader br = null;
        try {
            connection.connect();
            if (connection.getResponseCode() == SUCCESS_CODE) {
                is = connection.getInputStream();
                if (is != null) {
                    br = new BufferedReader(new InputStreamReader(is, CHARSET_NAME));
                    String temp;
                    if ((temp = br.readLine()) != null) {
                        result.append(temp);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("response result failed.", e);
        } finally {
            closeQuietly(br);
            closeQuietly(is);
        }
        return result.toString();
    }

    private static void closeQuietly(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException e) {
            // ignore
        }
    }

    private enum RequestMethod {
        GET, POST;
    }
}
```

#### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>pulsar-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>io.streamnative</groupId>
            <artifactId>pulsar-broker-common</artifactId>
            <version>2.9.1.5</version>
        </dependency>
        <dependency>
            <groupId>org.apache.pulsar</groupId>
            <artifactId>pulsar-client-api</artifactId>
            <version>2.9.1</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.25</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.27</version>
        </dependency>
    </dependencies>

</project>
```



### 服务端配置

1. 服务端客户端代码打成jar包，copy到pulsar容器

```
docker cp D:\idea\IdeaProjects\demo\pulsar-plugin\target\pulsar-plugin-1.0-SNAPSHOT.jar 14a8f7a6ad25:/pulsar/lib/
```

2. mysql的jar包，copy到pulsar容器

```
docker cp D:\repository\mysql\mysql-connector-java\8.0.27\mysql-connector-java-8.0.27.jar 14a8f7a6ad25:/pulsar/lib/
```

3. broker.conf配置（单机配置standalone.conf）

```properties
authenticationEnabled=true
authenticationProviders=auth.server.DemoAuthenticationProvider
brokerClientAuthenticationPlugin=auth.client.DemoAuthentication
brokerClientAuthenticationParameters=accessId:2a76b9719d911017c592,accessKey:7d793037a0760186574b0282f2f435e7
```



### 数据库

```mysql
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账号',
  `access_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '访问凭证',
  `access_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '盐值',
  `role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

INSERT INTO `user` VALUES (1, 'hello', '2a76b9719d911017c592', '7d793037a0760186574b0282f2f435e7', 'admin');
INSERT INTO `user` VALUES (2, 'zhangsan', '0e7bd9443513f22ab9af', 'e10adc3949ba59abbe56e057f20f883e', 'test-user');
```



### 报错

'host.docker.internal' is not allowed to connect to this MySQL serverConnection closed by foreign host

```mysql
# Docker容器中使用ip连接数据库时默认不允许，库mysql中执行
update user set host = '%' where user = 'root';
flush privileges;
```

