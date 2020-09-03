package model;

import java.sql.Timestamp;

public class Message {
    private int messageId;
    private int userId;
    private int channelId;
    private String content;
    private Timestamp sendTime;

    // 此处再新增一个字段. 此处这里的 nickName 就和 userId 是对应的.
    // 直接把用户的昵称放到这里, 方便后面的界面显示.
    // nickName 在 message 表中并不存在. 可以根据 userId 在 user 表中查到
    private String nickName;

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getSendTime() {
        return sendTime;
    }

    public void setSendTime(Timestamp sendTime) {
        this.sendTime = sendTime;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", userId=" + userId +
                ", channelId=" + channelId +
                ", content='" + content + '\'' +
                ", sendTime=" + sendTime +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}
