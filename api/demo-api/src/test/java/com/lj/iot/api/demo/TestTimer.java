package com.lj.iot.api.demo;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class TestTimer {

    public static void main(String[] args) {
       final Timer timer = new Timer();

        for (int i = 0; i < 10; i++) {
            final Integer k = i;
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    System.out.println(LocalDateTime.now() + "---" + k);
                }
            };
            timer.schedule(timerTask, 2000*i);
        }

    }
}
