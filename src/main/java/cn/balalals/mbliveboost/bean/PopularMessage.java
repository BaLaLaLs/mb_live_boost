package cn.balalals.mbliveboost.bean;

public class PopularMessage implements BiliMessage {
    private int popular;

    @Override
    public String toString() {
        return "PopularMessage{" +
                "popular=" + popular +
                '}';
    }

    public int getPopular() {
        return popular;
    }

    public void setPopular(int popular) {
        this.popular = popular;
    }
}
