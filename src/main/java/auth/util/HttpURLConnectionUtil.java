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
