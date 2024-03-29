package com.ego.sms.listener;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.ego.sms.config.SmsProperties;
import com.ego.sms.utils.SmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SmsListener {
    @Autowired
    private SmsUtils smsUtils;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ego.sms.queue", durable = "true"),
            exchange = @Exchange(value = "ego.sms.exchange",
                    ignoreDeclarationExceptions = "true"),
            key = {"sms.verify.code"}))
    public void sendSms(Map<String,String> map)
    {
        String phone = map.get("phone");
        String code = map.get("code");

        //调用阿里大于短信平台发送短信
        System.out.println("正在发送短信...");
        try {
            smsUtils.sendSms(phone, code)
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

}
