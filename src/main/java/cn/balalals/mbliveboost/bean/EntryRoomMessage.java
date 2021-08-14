package cn.balalals.mbliveboost.bean;


public class EntryRoomMessage implements BiliMessage{
    private String uname;

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    @Override
    public String toString() {
        return "EntryRoomMessage{" +
                "uname='" + uname + '\'' +
                '}';
    }
}
