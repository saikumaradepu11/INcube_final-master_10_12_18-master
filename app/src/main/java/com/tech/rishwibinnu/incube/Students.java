package com.tech.rishwibinnu.incube;

public class Students {

    public String name,htno,mobile;

    public Students()
    {

    }

    public Students(String name, String htno, String mobile) {
        this.name = name;
        this.htno = htno;
        this.mobile = mobile;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
