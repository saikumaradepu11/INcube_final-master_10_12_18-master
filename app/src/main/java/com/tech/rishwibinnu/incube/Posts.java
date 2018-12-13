package com.tech.rishwibinnu.incube;

public class Posts {

    public String date,discription,postimage,fullname,profileimage,time,uid,event,count;

    public Posts()
    {

    }

    public Posts(String date, String discription, String postimage, String fullname, String profileimage, String time, String uid, String event, String count) {
        this.date = date;
        this.discription = discription;
        this.postimage = postimage;
        this.fullname = fullname;
        this.profileimage = profileimage;
        this.time = time;
        this.uid = uid;
        this.event = event;
        this.count = count;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
