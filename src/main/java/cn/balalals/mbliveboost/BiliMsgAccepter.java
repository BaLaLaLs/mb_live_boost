package cn.balalals.mbliveboost;

import cn.balalals.mbliveboost.bean.BiliMessage;

@FunctionalInterface
public interface BiliMsgAccepter {
    public void msgAccept(BiliMessage biliMessage);
}
