package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.*;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

// 用来实现 websocket 接口
// 此处 userId 是一个变量. 客户端连接的时候, 具体提交的 userId 可能都有差异.
// 服务器需要获取到具体的 userId 是啥.
@ServerEndpoint(value="/message/{userId}")
public class MessageAPI {
    private Gson gson = new GsonBuilder().create();
    // 通过用户连接的 url 中获取到的.
    private int userId;

    @OnOpen
    public void onOpen(@PathParam("userId") String userIdStr, Session session) throws IOException {
        // 1. 获取 userId
        this.userId = Integer.parseInt(userIdStr);
        System.out.println("连接建立: " + userId);
        // 2. 把该用户加入到在线用户列表中.
        MessageCenter.getInstance().addOnlineUser(userId, session);
        // 3. 拉取历史消息, 并直接转发给该用户.
        UserDao userDao = new UserDao();
        User user = userDao.selectById(userId);
        Timestamp lastLogout = user.getLastLogout();
        MessageDao messageDao = new MessageDao();
        List<Message> messages = messageDao.selectByTimeStamp(lastLogout, new Timestamp(System.currentTimeMillis()));
        for (Message message : messages) {
            String jsonString = gson.toJson(message);
            session.getBasicRemote().sendText(jsonString);
        }
    }

    @OnClose
    public void onClose() {
        System.out.println("连接断开! " + userId);
        // 把用户从在线列表中删掉
        MessageCenter.getInstance().delOnlineUser(userId);
        // 更新下线时间
        UserDao userDao = new UserDao();
        userDao.updateLogout(userId);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("连接出现错误! " + userId);
        error.printStackTrace();
        // 把用户从在线列表中删掉
        MessageCenter.getInstance().delOnlineUser(userId);
        // 更新下线时间
        UserDao userDao = new UserDao();
        userDao.updateLogout(userId);
    }

    @OnMessage
    public void onMessage(String request, Session session) throws InterruptedException {
        System.out.println("收到消息! " + userId + ": " + request);
        // 1. 解析 message 格式. 收到的 request 对象应该是一个 json 格式的字符串
        Message message = gson.fromJson(request, Message.class);
        // 2. 把消息的发送时间填写一下
        message.setSendTime(new Timestamp(System.currentTimeMillis()));
        // 3. 把消息写入消息中心.
        MessageCenter.getInstance().addMessage(message);
        // 4. 把这个消息写入数据库
        MessageDao messageDao = new MessageDao();
        messageDao.add(message);
    }
}
