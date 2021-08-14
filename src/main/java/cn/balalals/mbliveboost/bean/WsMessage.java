package cn.balalals.mbliveboost.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class WsMessage<T> {
    @JSONField(name = "Data")
    public T data;
    @JSONField(name = "Type")
    public String type;
}
