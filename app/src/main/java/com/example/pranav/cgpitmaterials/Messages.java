package com.example.pranav.cgpitmaterials;

public class Messages {
    String message;
    String type;
    String seen;
    String from;
    Long time;

    public Messages(String message, String type, String seen, String from, Long time) {
        this.message = message;
        this.type = type;
        this.seen = seen;
        this.from = from;
        this.time = time;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }



    public Messages() {
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

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }


}
