package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import model.User;
import model.UserDao;
import test.TestEncoding;
import util.ChatroomException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RegisterServlet extends HttpServlet {
    private Gson gson = new GsonBuilder().create();

    // 这个类以内部类的方式来组织. 这个 Request 类只是针对 RegisterServlet 来使用的.
    // 其他的 Servlet 对应的 Request 类可能结构不同.
    // 从 body 的 json 中转换过来的.
    static class Request {
        public String name;
        public String password;
        public String nickName;

        @Override
        public String toString() {
            return "Request{" +
                    "name='" + name + '\'' +
                    ", password='" + password + '\'' +
                    ", nickName='" + nickName + '\'' +
                    '}';
        }
    }

    // 响应的数据内容
    // 要把这个对象再转回 json 字符串, 并写回给客户端.
    static class Response {
        public int ok;
        public String reason;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 显式的告诉 Servlet 按照 utf-8 这样的编码方式来处理请求.
        req.setCharacterEncoding("utf-8");
        Response response = new Response();
        try {
            // 1. 读取 body 中的信息(json 格式的字符串)
            String body = Util.readBody(req);
            // 2. 把 json 数据转成 Java 中的对象.
            //    创建一个 Request 类来表示这次请求的结构
            //    需要把 body => Request 对象~~~
            //    此处最好借助第三方库来完成. (JSON 的第三方库有很多, fastjson, jackson...)
            //    此处咱们使用 GSON (Google 搞的一个库)
            Request request = gson.fromJson(body, Request.class);
            System.out.println("request: " + request);
            // 3. 在数据库中查一下, 看看用户名是否已经存在了. 如果存在了就认为注册失败.
            UserDao userDao = new UserDao();
            User existsUser = userDao.selectByName(request.name);
            if (existsUser != null) {
                throw new ChatroomException("用户名已经存在");
            }
            // 4. 把新的用户名和密码构造 User 对象并插入数据库
            User user = new User();
            user.setName(request.name);
            user.setPassword(request.password);
            user.setNickName(request.nickName);
            userDao.add(user);
            // 5. 返回一个注册成功的响应结果.
            response.ok = 1;
            response.reason = "";
        } catch (ChatroomException | JsonSyntaxException e) {
            e.printStackTrace();
            response.ok = 0;
            response.reason = e.getMessage();
        } finally {
            resp.setContentType("application/json; charset=utf-8");
            String jsonString = gson.toJson(response);
            resp.getWriter().write(jsonString);
        }
    }
}
