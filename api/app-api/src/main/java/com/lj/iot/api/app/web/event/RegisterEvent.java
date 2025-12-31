package com.lj.iot.api.app.web.event;


import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public class RegisterEvent extends ApplicationEvent {
    public RegisterEvent(Object source) {
        super(source);
    }
}
