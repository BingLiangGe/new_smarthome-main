package com.lj.iot.biz.service;

public interface BizMusicOrderService {

    void completeOrder(String orderNo, String transactionId);

    void active(String deviceId,String userId);
}
