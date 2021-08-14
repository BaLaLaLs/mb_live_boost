package cn.balalals.mbliveboost.bean;

public abstract class RoomMessage {
    public RoomMessage(String type, Object data) {
        this.type = type;
        this.data = data;
    }

    public RoomMessage() {
    }

    protected String type;
//    String userName;
    protected Object data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RoomMessage{" +
                "type='" + type + '\'' +
                ", data=" + data +
                '}';
    }
}
