package auth;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author lamen 2022/5/28
 */
public class DigestUtilsTest {
    public static void main(String[] args) {
//        String accessId = DigestUtils.md5Hex("zhangsan").substring(12);
//        String accessKey = DigestUtils.md5Hex("123456");
        String accessId = "0e7bd9443513f22ab9af";
        String accessKey = "e10adc3949ba59abbe56e057f20f883e";

        System.out.println(accessId);
        System.out.println(accessKey);

        String username = accessId;
        String password = DigestUtils.md5Hex(accessId + DigestUtils.md5Hex(accessKey)).substring(8, 24);
        System.out.println(username);
        System.out.println(password);
    }
}
