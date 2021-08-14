package cn.balalals.mbliveboost.websocket;

import cn.balalals.mbliveboost.bean.DanMuMessage;
import cn.balalals.mbliveboost.bean.EntryRoomMessage;
import cn.balalals.mbliveboost.bean.GiftMessage;
import cn.balalals.mbliveboost.bean.PopularMessage;
import cn.balalals.mbliveboost.handler.DanMuMsgHandler;
import cn.balalals.mbliveboost.handler.EntryRoomHandler;
import cn.balalals.mbliveboost.handler.GiftMsgHandler;
import cn.balalals.mbliveboost.handler.PopularHandler;
import cn.balalals.mbliveboost.util.BilibiliMsgSplit;
import cn.balalals.mbliveboost.util.Zlib;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Logger;

import static cn.balalals.mbliveboost.BiliBiliConstants.*;


public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    private final WebSocketClientHandshaker handShaker;
    private final WebSocketClient client;
    private ChannelPromise handshakeFuture;
    public WebSocketClientHandler(WebSocketClientHandshaker handShaker,WebSocketClient client) {
        this.client = client;
        this.handShaker = handShaker;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handShaker.handshake(ctx.channel());
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handShaker.isHandshakeComplete()) {
            handShaker.finishHandshake(ch, (FullHttpResponse) msg);
            Logger.getLogger("WebSocketClientHandler").info("WebSocket Client connected!");
            handshakeFuture.setSuccess();
            return;
        }
        if (msg instanceof FullHttpResponse) {
            final FullHttpResponse response = (FullHttpResponse) msg;
            throw new Exception("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content="
                    + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        final WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof BinaryWebSocketFrame) {
            ByteBuf data = frame.content();
            int protocol = data.getShort(PROTOCOL_VERSION_OFFSET);
            System.out.println("protocol:"+ protocol);
            switch (protocol) {
                case JSON_PROTOCOL_VERSION:
                    return;
                case POPULAR_PROTOCOL_VERSION:
                    //handPopularMessage(data);
                    return;
                case BUFFER_PROTOCOL_VERSION:
                    handBufferMessage(data);
                    return;
                default:
            }
        }
    }

    private void handBufferMessage(ByteBuf data) throws Exception {
        int packetLength = data.getInt(PACKET_LENGTH_OFFSET);
        int operation = data.getInt(OPERATION_OFFSET);
        if (operation == POPULAR_OPERATION) {
            PopularMessage popularMessage = new PopularMessage();
            popularMessage.setPopular(data.getInt(BODY_OFFSET));
            client.receivedMessage(popularMessage);
            return;
        }

        if (operation == MESSAGE_OPERATION) {
            byte[] uncompressedData = new byte[packetLength - BODY_OFFSET];
            data.getBytes(BODY_OFFSET, uncompressedData);
            byte[] decompressData = Zlib.decompress(uncompressedData);
            byte[] msgBytes = Arrays.copyOfRange(decompressData, BODY_OFFSET, decompressData.length);
            String[] message = BilibiliMsgSplit.split(IOUtils.toString(msgBytes, StandardCharsets.UTF_8.toString()));
            for (String msg : message) {
                handStringMessage(msg);
            }
        }
    }
    private void handStringMessage(String message) {
        JSONObject jsonObject = JSON.parseObject(message);
        String cmd = jsonObject.getString("cmd");
        switch (cmd) {
            case "INTERACT_WORD":
                // msg_type 1 为进入直播间 2 为关注 3为分享直播间
                EntryRoomMessage entryRoomMessage = new EntryRoomMessage();
//                if(jsonObject.getInteger("msg_type")) {
//
//                }
                entryRoomMessage.setUname(jsonObject.getJSONObject("data").getString("uname"));
                client.receivedMessage(entryRoomMessage);
//                new EntryRoomHandler().handler(entryRoomMessage);
                break;
            case "DANMU_MSG":
                JSONArray info = jsonObject.getJSONArray("info");
                DanMuMessage danMuMessage = new DanMuMessage();
                danMuMessage.setMsg(info.getString(1));
                danMuMessage.setUname(info.getJSONArray(2).getString(1));
                client.receivedMessage(danMuMessage);
//                new DanMuMsgHandler().handler(danMuMessage);
                break;
            case "SEND_GIFT":
                GiftMessage giftMessage = new GiftMessage();
                JSONObject data = jsonObject.getJSONObject("data");
                giftMessage.setAction(data.getString("action"));
                giftMessage.setGiftId(data.getInteger("giftId"));
                giftMessage.setGiftName(data.getString("giftName"));
                giftMessage.setUname(data.getString("uname"));
                giftMessage.setNum(data.getInteger("num"));
                System.out.println("giftName:"+ giftMessage.getGiftName());
                client.receivedMessage(giftMessage);
//                new GiftMsgHandler().handler(giftMessage);
            case "COMBO_SEND":
            default:
        }
    }
    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }
}
