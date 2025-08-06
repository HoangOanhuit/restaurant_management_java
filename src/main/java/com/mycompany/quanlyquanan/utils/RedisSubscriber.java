/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quanlyquanan.utils;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class RedisSubscriber {
    public void subscribe(String channel) {
        new Thread(() -> {
            try (Jedis jedis = RedisConnector.getConnection()) {
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        System.out.println("🔔 Nhận được tin nhắn: " + message);
                        // Xử lý logic khi có thông báo
                    }
                }, channel);
            }
        }).start(); // Chạy lắng nghe ở thread riêng
    }
}