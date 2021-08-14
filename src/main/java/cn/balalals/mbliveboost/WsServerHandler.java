package cn.balalals.mbliveboost;

import cn.balalals.mbliveboost.bean.GiftMessage;
import cn.balalals.mbliveboost.bean.PopularMessage;
import cn.balalals.mbliveboost.bean.WsMessage;
import cn.balalals.mbliveboost.websocket.WebSocketClient;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;

import javax.net.ssl.SSLException;

public class WsServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame msg) {
        String text = msg.text();
        WsMessage<?> wsMessage = JSON.parseObject(text, WsMessage.class);
        if("ConnectionRoom".equals(wsMessage.type)) {
            int roomId = (int) wsMessage.data;
            System.out.println("连接房间roomId:"+ roomId);
            channelHandlerContext.channel().attr(AttributeKey.valueOf("roomId")).set(roomId);
            try {
                WebSocketClient webSocketClient = new WebSocketClient(roomId,(biliMsg) -> {
                    if(biliMsg instanceof GiftMessage) {
                        WsMessage<GiftMessage> giftMessageWsMessage = new WsMessage<>();
                        giftMessageWsMessage.type = "Gift";
                        giftMessageWsMessage.data = (GiftMessage)biliMsg;
                        channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(giftMessageWsMessage)));
                    } else if(biliMsg instanceof PopularMessage) {
                        WsMessage<PopularMessage> popularMessageWsMessage = new WsMessage<>();
                        popularMessageWsMessage.type = "Popular";
                        popularMessageWsMessage.data = (PopularMessage)biliMsg;
                        channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(popularMessageWsMessage)));
                    }
                });
                webSocketClient.open();
                channelHandlerContext.channel().closeFuture().addListener(r-> webSocketClient.close());
            } catch (SSLException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("text:" + text);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("异常："+ cause.getLocalizedMessage());
    }
}
