package com.bing.schat.service;

import com.alibaba.fastjson.JSON;
import com.bing.schat.entity.Message;
import com.bing.schat.entity.User;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/chat")
public class WebSocketServer {
    /**
     * 存储在线会话(所有人)
     */
    private static Map<String, Session> onlineSessions = new ConcurrentHashMap<>();

    /**
     * 存储在线会话（好友）
     */
    private static Map<String, Session> onlineFriends = new ConcurrentHashMap<>();

    /**
     * @Description 客户端打开连接时的动作
     * @author      daiyongbing
     * @param       session
     * @date        2019/2/18
     */
    @OnOpen
    public void onOpen(Session session) {
        onlineSessions.put(session.getId(), session);   //将用户添加到在线会话
        System.out.println("有人上线");
        sendMessageToAll(Message.jsonStr(Message.ENTER, "", "", onlineSessions.size()));    //通知所有人用户上线
    }



    /**
     * @Description 发送消息给所有人
     * @author      daiyongbing
     * @param
     * @date        2019/2/18
     */
    @OnMessage
    public void onMessage(Session session, String jsonStr) {
        Message message = JSON.parseObject(jsonStr, Message.class);
        System.out.println(jsonStr);
        sendMessageToAll(Message.jsonStr(Message.SPEAK, message.getUsername(), message.getMsg(), onlineSessions.size()));
    }

    /**
     * @Description 用户推出时将其移除在线会话并通知所有人
     * @author      daiyongbing
     * @param       session
     * @date        2019/2/18
     */
    @OnClose
    public void onClose(Session session) {
        onlineSessions.remove(session.getId());
        sendMessageToAll(Message.jsonStr(Message.QUIT, "", "", onlineSessions.size()));
    }

    /**
     * @Description 用户退出时将其移除在线列表，并通知好友，对应的操作为好友收到通知后将其状态置为“离线”
     * @author      daiyongbing
     * @param       session
     * @date        2019/2/18
     */
   /* @OnClose
    public void onClose(Session session) {
        onlineFriends.remove(session.getId());
        sendMessageToAll(Message.jsonStr(Message.QUIT, "", "", onlineSessions.size()));
    }*/

    /**
     * 当通信发生异常：打印错误日志
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    /**
     * @Description 发送消息给所有人（整个系统的用户）
     * @author      daiyongbing
     * @param       msg
     * @date        2019/2/18
     */
    private static void sendMessageToAll(String msg) {
        onlineSessions.forEach((id, session) -> {
            try {
                session.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * @Description 发送消息给在线好友
     * @author      daiyongbing
     * @param       msg
     * @date        2019/2/18
     */
    private static void sendMessageToNetFriend(String msg) {
        onlineFriends.forEach((id, session) -> {
            try {
                session.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
