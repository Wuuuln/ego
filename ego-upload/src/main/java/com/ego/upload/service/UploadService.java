package com.ego.upload.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
@Service
@Slf4j
public class UploadService {

    // 支持的文件类型
    private static final List<String> suffixes = Arrays.asList("image/png", "image/jpeg");

    public String upload(MultipartFile file) {
        // 校验文件类型
        String contentType = file.getContentType();
        if(!suffixes.contains(contentType)){
            //@Slf4j提供的
            log.info("文件类型不正确.");
            return null;
        }

        // 校验图片内容
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if(image==null){
                log.info("上传失败，文件内容不符合要求");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //保存到硬盘
            //生成保存目录
        File dir = new File("E://Three/images");
        //如果没有这个目录就创建一个
        if(!dir.exists()){
            dir.mkdir();
        }
            // 保存图片
        try {
            file.transferTo(new File(dir, file.getOriginalFilename()));
            // 拼接图片地址
            String url = "http://image.ego.com/upload/" + file.getOriginalFilename();
            return url;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
