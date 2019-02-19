package com.bing.schat.service;

import com.alibaba.fastjson.JSON;
import com.bing.schat.entity.Message;
import com.bing.schat.entity.User;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/chat/{username}")
public class WebSocketServer {

    /**
     * 存储在线会话（好友），Key为用户标识
     */
    private static ConcurrentHashMap<String, Session> onlineFriends = new ConcurrentHashMap<>();

    /**
     * 当前在线数。须使用线程安全操作
     */
    private static int onlineCount = 0;

    /**
     * 客户端连接会话，通过它来给客户端发送数据
     */
    private Session WebSession;

    /**
     * 消息发送者
     */
    private String sender = "";

    /**
     * 消息接收者
     */
    private String receiver = "";

    /**
     * 获取当前时间
     * @return
     */
    private String getNowTime() {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(date);
        return time;
    }

    /**
     * @Description 客户端连接时调用方法
     * @author      daiyongbing
     * @param       session
     * @date        2019/2/18
     */
    @OnOpen
    public void onOpen(@PathParam(value = "username") String username, Session session) {
        onlineFriends.put(username, session);   //将用户添加到在线会话
        this.sender = username;
        this.WebSession  =session;
        sendToAll(username);    //通知所有人用户上线
        addOnlineCount();   //在线数加1
        System.out.println(username+"上线,当前在线人数：" + getOnlineCount());
    }

    /**
     * 群发消息
     * @param message
     */
    private void sendToAll(String message) {
        String now = getNowTime();
        //遍历HashMap
        for (String key : onlineFriends.keySet()) {
            try {
                //判断接收用户是否是当前发消息的用户
                if (!sender.equals(key)) {
                    sendMessage(now + "用户" + sender + "发来消息：" + " <br/> " + message);
                    System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送消息。没有使用注解
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.WebSession.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
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
        sendMessageToAll(Message.jsonStr(Message.SPEAK, message.getUsername(), message.getMsg(), onlineFriends.size()));
    }


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
        onlineFriends.forEach((id, session) -> {
            try {
                session.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    /*************************************指定消息发送************************************/


    /**
     * 客户端关闭处理
     */
    @OnClose
    public void onClose() {
        if (!sender.equals("")) {
            onlineFriends.remove(sender); //从set中删除
            subtractOnlineCount();  //在线数减1
            System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
        }
    }

    /**
     * 收到客户端消息后调用
     * @param message
     * @param session
     */
    @SuppressWarnings("unused")
    //@OnMessage
    /*public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);
        //session.get
        //群发消息
        if (1 < 2) {
            sendAll(message);
        } else {
            //给指定的人发消息
            sendToUser(message);
        }
    }*/


    /**
     * 给指定客户端发送消息
     * @param message
     */
    /*@OnMessage
    public void sendToUser(String message) {
        String sendUserno = message.split("[|]")[1];
        String sendMessage = message.split("[|]")[0];
        String now = getNowTime();
        try {
            if (webSocketSet.get(sendUserno) != null) {
                webSocketSet.get(sendUserno).sendMessage(now + "用户" + senderNo + "发来消息：" + " <br/> " + sendMessage);
            } else {
                System.out.println("当前用户不在线");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/



    /**
     * 同步获得当前在线人数
     * @return
     */
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    /**
     * 同步增加在线人数
     */
    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    /**
     * 同步减少在线人数
     */
    public static synchronized void subtractOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
