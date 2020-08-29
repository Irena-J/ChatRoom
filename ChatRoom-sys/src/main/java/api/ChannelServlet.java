package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import model.Channel;
import model.ChannelDao;
import model.User;
import util.ChatroomException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 这个代码是处理频道操作的 API
public class ChannelServlet extends HttpServlet {
    private Gson gson = new GsonBuilder().create();

    static class Request {
        public String channelName;
        public int channelId;
    }

    static class Response {
        public int ok;
        public String reason;
    }

    // 新增频道
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        Response response = new Response();
        // 1. 读取 body 中的信息（json格式的字符串）

        try {
            // 1. 验证登陆状态. 如果未登陆则不能查看.
            HttpSession httpSession = req.getSession(false);
            if (httpSession == null) {
                throw new ChatroomException("您尚未登陆");
            }
            String body = Util.readBody(req);
            // 2. 把 json 数据转成 Java 中的对象.
            Request request = gson.fromJson(body,Request.class);
            System.out.println("request:"+request);
            // 3. 频道可以任意添加的，因为 id 是不同的

            ChannelDao channelDao = new ChannelDao();

            Channel channel = new Channel();
            channel.setChannelName(request.channelName);
            channelDao.add(channel);

            response.ok = 1;
            response.reason = "";
        } catch (ChatroomException | JsonSyntaxException e) {
            e.printStackTrace();
            response.ok = 0;
            response.reason = e.getMessage();
        } finally {
            resp.setContentType("application/json;charset=utf-8");
            String jsonString = gson.toJson(response);
            resp.getWriter().write(jsonString);
        }
    }

    // 删除频道
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        Response response = new Response();
        // 1. 读取 body 中的信息
        try {
            // 1. 验证登陆状态. 如果未登陆则不能查看.
            HttpSession httpSession = req.getSession(false);
            if (httpSession == null) {
                throw new ChatroomException("您尚未登陆");
            }
            String body = Util.readBody(req);
            // 2. 把 json 数据转成 Java 中的对象
            Request request = gson.fromJson(body,Request.class);
            System.out.println("request:"+request);
            ChannelDao channelDao = new ChannelDao();
            channelDao.delete(request.channelId);

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

    // 获取频道列表
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Channel> channels = new ArrayList<>();
        try {
            // 1. 验证登陆状态. 如果未登陆则不能查看.
            HttpSession httpSession = req.getSession(false);
            if (httpSession == null) {
                throw new ChatroomException("您尚未登陆");
            }
            User user = (User) httpSession.getAttribute("user");
            // 2. 查数据库
            ChannelDao channelDao = new ChannelDao();
            channels = channelDao.selectAll();
        } catch (ChatroomException e) {
            e.printStackTrace();
            // 如果前面触发了异常, 此时 channels 将是一个空的 List
            // 下面的 finally 中将会构造出一个空数组的 json
        } finally {
            // 3. 把查询结果包装成响应内容
            //    如果参数是个 List, 转出的 json 字符串就是一个 数组
            resp.setContentType("application/json; charset=utf-8");
            String jsonString = gson.toJson(channels);
            resp.getWriter().write(jsonString);
        }
    }
}
