package com.lj.iot.common.aiui.core.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 发送邮件
 * @author tyj
 * @date   2023-9-19
 */
public class JavaMailUtils {

    public static void sendMial(String context,String subject){
        //1.创建Session
        try {
            Session session = JavaMailUtils.createSession();

            //2.创建邮件对象
            MimeMessage message = new MimeMessage(session);
            //设置邮件主题
            message.setSubject(subject);
            //设置邮件内容
            message.setText(context);
            //设置发件人
            message.setFrom(new InternetAddress("builder0523@foxmail.com"));
            //设置收件人
            message.setRecipient(Message.RecipientType.TO, new InternetAddress("1213866606@qq.com"));

            //3.发送邮件
            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Session createSession() {
        Properties pros = new Properties();
        pros.put("mail.smtp.host", "smtp.qq.com");
        //pros.put("mail.smtp.port", "25");
        pros.put("mail.smtp.auth", "true");
        pros.put("mail.smtp.starttls.enable", "true");
        pros.setProperty("mail.transport.protocol", "smtp");

        //创建Session
        Session session = Session.getInstance(pros, new Authenticator() {
            String userName = "builder0523@foxmail.com";
            String password = "dkihkmzbzqmxbcgh";

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });
        session.setDebug(true);
        return session;
    }
}
