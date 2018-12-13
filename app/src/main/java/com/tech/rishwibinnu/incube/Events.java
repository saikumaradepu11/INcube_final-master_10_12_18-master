package com.tech.rishwibinnu.incube;

public class Events {

    public String event,postimage,name,htno;

    public Events()
    {

    }

    public Events(String event, String postimage, String name, String htno) {
        this.event = event;
        this.postimage = postimage;
        this.name = name;
        this.htno = htno;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHtno() {
        return htno;
    }

    public void setHtno(String htno) {
        this.htno = htno;
    }
}
