package cn.balalals.mbliveboost.bean;

public class GiftMessage implements BiliMessage {
    private String action;
    private Integer giftId;
    private String giftName;
    private String uname;
    private Integer num;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "GiftMessage{" +
                "action='" + action + '\'' +
                ", giftId=" + giftId +
                ", giftName='" + giftName + '\'' +
                ", uname='" + uname + '\'' +
                ", num=" + num +
                '}';
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getGiftId() {
        return giftId;
    }

    public void setGiftId(Integer giftId) {
        this.giftId = giftId;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }
}

