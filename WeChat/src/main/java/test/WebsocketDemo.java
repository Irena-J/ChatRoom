package test;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;

@ServerEndpoint(value="/webSocketTest")
public class WebsocketDemo {
    @OnOpen
    public void onOpen(Session session) {
        // 将会在客户端建立连接时被调用.
        System.out.println("建立连接");

        // 创建一个专门的线程, 来源源不断的写回数据.
        Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        session.getBasicRemote().sendText("客户端你好" + new Date());
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }

    @OnClose
    public void onClose() {
        // 客户端断开连接时被调用
        System.out.println("断开连接");
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        // 在服务器收到客户端的请求时调用.
        System.out.println("收到消息: " + message);
        // 收到消息, 就给客户端返回一个数据.
        session.getBasicRemote().sendText("我收到了");
    }

    @OnError
    public void onError(Session session, Throwable error) {
        // 连接意外终止时, 就会调用 onError 方法.
        System.out.println("连接出现错误!");
        error.printStackTrace();
    }
}
