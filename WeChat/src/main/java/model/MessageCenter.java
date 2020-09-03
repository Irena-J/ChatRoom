package model;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.websocket.Session;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

// 管理消息和用户列表. 实现消息转发.
// 这个类作为一个单例即可.
public class MessageCenter {
    private volatile static MessageCenter instance = null;

    public static MessageCenter getInstance() {
        if (instance == null) {
            synchronized (MessageCenter.class) {
                if (instance == null) {
                    instance = new MessageCenter();
                }
            }
        }
        return instance;
    }

    // 里面包含两个重要的数据结构:
    // 1. 保存消息的队列.
    private BlockingQueue<Message> messages = new LinkedBlockingQueue<>();
    // 2. 保存在线用户列表
    private ConcurrentHashMap<Integer, Session> onlineUsers = new ConcurrentHashMap<>();

    // 实现几个操作这俩结构的方法:
    // 1. 用户上线
    public void addOnlineUser(int userId, Session session) {
        onlineUsers.put(userId, session);
    }
    // 2. 用户下线
    public void delOnlineUser(int userId) {
        onlineUsers.remove(userId);
    }
    // 3. 新增消息
    public void addMessage(Message message) throws InterruptedException {
        messages.put(message);
    }

    // 接下来这个代码非常重要!!!!
    // 创建一个线程, 来一直扫描消息的队列, 把里面存的消息转发给所有的在线用户.
    // 构造 MessageCenter 实例的时候, 就启动这个线程.
    private MessageCenter() {
        Thread t = new Thread() {
            @Override
            public void run() {
                Gson gson = new GsonBuilder().create();
                while (true) {
                    try {
                        // 1. 从队列中尝试取消息
                        //    如果队列为空, 此时 take 就会阻塞
                        Message message = messages.take();
                        // 2. 把消息转成 json 字符串
                        String jsonString = gson.toJson(message);
                        // 3. 遍历在线用户列表, 把消息转发给每个用户
                        for (ConcurrentHashMap.Entry<Integer, Session> entry : onlineUsers.entrySet()) {
                            Session session = entry.getValue();
                            session.getBasicRemote().sendText(jsonString);
                        }
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }
}
