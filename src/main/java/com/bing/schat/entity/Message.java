package com.bing.schat.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

//@AllArgsConstructor
@Data
public class Message {
    public static final String ONLINE = "ONLINE";   //上线
    public static final String SPEAK = "SPEAK";
    public static final String OFFLINE = "OFFLINE"; //离线

    private String type;    //消息类型
    private String username;    //发送人
    private String msg; //发送消息
    private String time;  // 消息发送时间




    public Message(String username, String message, String time) {
        this.username = username;
        this.msg = msg;
        this.time = time;
    }

    public Message(String type, String username, String message, String time, int onlineCount) {
        this.type = type;
        this.username = username;
        this.msg = message;
        this.time = time;
    }

    public Message(String type, String username, String msg, int onlineTotal) {
        this.type = type;
        this.username = username;
        this.msg = msg;
    }

    public static String jsonStr(String type, String username, String msg, int onlineTotal) {
        return JSON.toJSONString(new Message(type, username, msg, onlineTotal));
    }



    public static String getSPEAK() {
        return SPEAK;
    }

    public static String getONLINE() {
        return ONLINE;
    }

    public static String getOFFLINE() {
        return OFFLINE;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getTime() {
        return time;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
