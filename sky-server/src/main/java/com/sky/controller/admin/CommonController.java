package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.properties.AliOssProperties;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil ossUtil;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传:{}",file);
        try {
            String fileName = file.getOriginalFilename();
            /* 为了实现不覆盖 不能用文件原名 但是后缀别改了*/
            String suffix = fileName.substring(fileName.lastIndexOf('.'));// .jpg
            String prefix = UUID.randomUUID().toString();
            fileName = prefix+suffix;
            String pictureUrl = ossUtil.upload(file.getBytes(), fileName);
            return Result.success(pictureUrl);
        } catch (IOException e) {
            log.error("文件上传失败:{}",e);
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
