package com.example.chat;

public class Messages
{
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Messages(String from, String message, String type) {
        this.from = from;
        this.message = message;
        this.type = type;
    }

    private String from,message,type;
    public Messages()
    {

    }
}
