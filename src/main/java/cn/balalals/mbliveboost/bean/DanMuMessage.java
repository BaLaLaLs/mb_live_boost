package cn.balalals.mbliveboost.bean;

public class DanMuMessage implements BiliMessage{
    private String uname;
    private String msg;

    @Override
    public String toString() {
        return "DanMuMessage{" +
                "uname='" + uname + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
