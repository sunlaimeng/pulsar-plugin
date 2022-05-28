package auth;

import auth.util.HttpURLConnectionUtil;

/**
 * @author lamen 2022/5/28
 */
public class HttpURLConnectionUtilTest {
    public static void main(String[] args) {
        doGetTest();
        doPostTest();
    }

    private static void doGetTest() {
        String url = "http://localhost:8090/api/community/app/house-rent/details?id=b4b98680-bb09-11ec-93d3-534f3426582b&parkIdStr=6a3466e4-4612-11e7-a929-92ebcb67fe16";
        String result = HttpURLConnectionUtil.doGet(url);
        System.out.println(result);
    }

    private static void doPostTest() {
        String url = "http://localhost:8090/api/community/app/auth/login/password";
        String param = "{\"phone\": \"18539950002\", \"password\": \"123456\"}";
        String result = HttpURLConnectionUtil.doPost(url, param);
        System.out.println(result);
    }
}
