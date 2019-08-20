package com.hairol2k.vcoresmsbulk;

public class Message {
    private String phone;
    private  String message;
    private  String level;

    public Message(String phone, String message, String level) {
        this.phone = phone;
        this.message = message;
        this.level = level;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
