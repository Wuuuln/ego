package com.ego.user.service;

import com.ego.common.pojo.CodecUtils;
import com.ego.common.pojo.NumberUtils;
import com.ego.user.mapper.UserMapper;
import com.ego.user.pojo.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final String prefix = "user:sms:code:";
    public Boolean check(String data, Integer type) {
        Boolean result = true;
        if(type!=null){
            User user = new User();
            if(type==1){
               user.setUsername(data);
            }else if(type==2){
                user.setPhone(data);
            }
            List<User> userList = userMapper.select(user);
            if(userList==null || userList.size()==0){
                result = false;
            }
        }
        return result;
    }
    //发送验证码短信
    public Boolean sendMsg(String phone) {
        //生成随机验证码
        String code = NumberUtils.generateCode(4);
        Map<String, String> map = new HashMap<>();
        map.put("phone",phone);
        map.put("code",code);
        //通知短信微服务发送短信
        amqpTemplate.convertAndSend("code.send",map);
        return null;
    }

    public Boolean sendMessage(String phone) {
        boolean result = false;
        //生成6位随机验证码
        String code = NumberUtils.generateCode(6);
        Map<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("code", code);
        //通知短信微服务发送短信
        amqpTemplate.convertAndSend("sms.verify.code",map);

        //将验证码保存到redis中
        redisTemplate.opsForValue().set(prefix + phone, code,5, TimeUnit.MINUTES);
        result = true;
        return result;
    }

    public Boolean register(User user, String code) {
        Boolean result = false;
        //判断验证码是否正确
        if(StringUtils.isNotEmpty(code))
        {
            //从redis中获取当前手机对应的验证码
            String key = prefix+user.getPhone();
            String oldCode = redisTemplate.opsForValue().get(key);
            if(code.equals(oldCode))
            {
                //保存到数据库
                user.setCreated(new Date());

                //对密码加密
                String encodePassword = CodecUtils.passwordBcryptEncode(user.getUsername(), user.getPassword());
                user.setPassword(encodePassword);

                userMapper.insertSelective(user);
                result = true;

                //删除redis中的验证码
                redisTemplate.delete(key);
            }
        }

        return result;
    }

    public User queryUser(String username, String password) {
        // 查询
        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        // 校验用户名
        if (user == null) {
            return null;
        }
        // 校验密码
        if (!CodecUtils.passwordConfirm(username+password,user.getPassword())) {
            return null;
        }
        // 用户名密码都正确
        return user;
    }
}
