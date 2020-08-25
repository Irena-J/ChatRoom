package api;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class Util {
    public static String readBody(HttpServletRequest req) throws UnsupportedEncodingException {
        // body 的长度. 单位是 字节.
        int contentLength = req.getContentLength();
        // buffer 来保存读到的 body 内容.
        byte[] buffer = new byte[contentLength];
        try (InputStream inputStream = req.getInputStream()) {
            inputStream.read(buffer, 0, contentLength);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(buffer, 0, contentLength, "UTF-8");
    }


}
