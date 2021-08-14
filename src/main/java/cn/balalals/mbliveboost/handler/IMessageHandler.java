package cn.balalals.mbliveboost.handler;

public interface IMessageHandler<T> {
    void handler(T msg);
}
