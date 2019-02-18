package com.bing.schat.entity;

public class User {
    private String username;    //用户名（昵称）
    private String chatId;      // id（类似于QQ号码）
    private String status;      // 在线状态（在线；离线）

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
