package com.example.messenger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.yandex.com");
        mailSender.setPort(465);

        mailSender.setUsername(System.getenv("email-username"));
        mailSender.setPassword(System.getenv("email-password"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.debug", "false");

        props.put("mail.transport.protocol", "smtps");
        props.put("mail.host", "smtp.yandex.com");

        return mailSender;
    }

}
