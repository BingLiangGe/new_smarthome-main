/*
 * Copyright 2022 learn-netty4 Project
 *
 * The learn-netty4 Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.lj.iot.common.aiui.core.ws;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lj.iot.common.aiui.core.service.ISkillWsAckProcessor;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Slf4j
public class AIUISocketUploadFileHandler extends SimpleChannelInboundHandler<Object> {

    private ISkillWsAckProcessor skillWsAckProcessor;
    private final WebSocketClientHandshaker handshaker;
    private final byte[] data;

    public AIUISocketUploadFileHandler(byte[] data, WebSocketClientHandshaker handshaker, ISkillWsAckProcessor skillWsAckProcessor) {
        this.skillWsAckProcessor = skillWsAckProcessor;
        this.handshaker = handshaker;
        this.data = data;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("channelInactive!");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            try {
                handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                log.info("websocket Handshake 完成!");
            } catch (WebSocketHandshakeException e) {
                log.error("websocket连接失败!");
            }
            return;
        }
        if (msg instanceof TextWebSocketFrame) {
            try {
                JSONObject body = JSON.parseObject(((TextWebSocketFrame) msg).text());
                log.info("AIUISocketUploadFileHandler.channelRead0" + body.toJSONString());
                //{"action":"started","data":"","sid":"awa01e8c02a@dx0001164e98e9a10b00","code":"0","desc":"success"}
                if ("0".equals(body.get("code").toString()) && "started".equals(body.get("action").toString())) {
                    // 发送音频
                    final int length = data.length;
                    int from = 0;
                    int to = Math.min(length, 10240);
                    while (from < to) {
                        byte[] bytes = Arrays.copyOfRange(data, from, to);
                        from = to;
                        to = Math.min(to + bytes.length, length);
                        ctx.channel().writeAndFlush(new BinaryWebSocketFrame(Unpooled.copiedBuffer(bytes)));
                    }
                    ctx.channel().writeAndFlush(new BinaryWebSocketFrame(Unpooled.copiedBuffer("--end--".getBytes(StandardCharsets.UTF_8))));
                }

                /**
                 * {"code":"0","data":{"sub":"nlp","is_last":true,"auth_id":"20220810115703609716843778949121","result_id":1,"is_finish":false,"intent":{"semantic":[{"template":"{app}","score":1,"slots":[{"name":"app","end":4,"begin":0,"value":"华为钱包","normValue":"华为钱包"}],"hazard":false,"entrypoint":"ent","intent":"custom_all_match"}],"shouldEndSession":true,"data":{},"uuid":"awa02013556@dx0001165000e0a14200","version":"6.0","sid":"awa02013556@dx0001165000e0a14200","rc":0,"intentType":"custom","answer":{"text":"好像出了点问题，稍后再试试吧。","type":"T"},"service":"OS10286212306.jx_test","vendor":"OS10286212306","semanticType":0,"voice_answer":[{"type":"TTS","content":"好像出了点问题，稍后再试试吧。"}],"sessionIsEnd":true,"text":"华为钱包","category":"OS10286212306.jx_test"}},"action":"result","sid":"awa02013556@dx0001165000e0a14200","desc":"success"}
                 */
                if ("0".equals(body.get("code").toString()) && "result".equals(body.get("action").toString())) {
                    JSONObject jsonObject = body.getJSONObject("data");
                    if (jsonObject != null && "nlp".equals(jsonObject.getString("sub"))) {
                        skillWsAckProcessor.handle(jsonObject);
                    }
                }

            } catch (Exception e) {
                log.error("AIUISocketUploadFileHandler.channelRead0{}", e);
            }
        }
    }
}
