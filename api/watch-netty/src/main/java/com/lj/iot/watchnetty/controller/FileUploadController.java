package com.lj.iot.watchnetty.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/api/open/file")
public class FileUploadController {
    private String uploadPath="C:\\Users\\A\\Desktop\\cuigong"; // 上传文件保存的路径

    @PostMapping("/upload") // 文件上传接口的URL为：/upload
    @ResponseBody // 返回值为JSON格式
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) { // 接收上传的文件参数名为file，类型为MultipartFile
        if (file.isEmpty()) { // 判断上传的文件是否为空
            return new ResponseEntity<>("请选择要上传的文件！", HttpStatus.BAD_REQUEST); // 返回错误信息，状态码为400（Bad Request）
        }
        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename()); // 获取上传文件的文件名（去掉路径）
            Path filePath = Paths.get(uploadPath, fileName); // 拼接文件保存路径和文件名，生成文件路径
            Files.createDirectories(filePath.getParent()); // 如果文件目录不存在，则创建目录
            Files.write(filePath, file.getBytes()); // 将上传的文件保存到指定路径下，使用文件的字节数组写入文件
            return new ResponseEntity<>("文件上传成功！", HttpStatus.OK); // 返回成功信息，状态码为200（OK）
        } catch (IOException e) {
            e.printStackTrace(); // 如果上传过程中出现异常，打印异常信息
            return new ResponseEntity<>("文件上传失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 返回错误信息，状态码为500（Internal Server Error）
        }
    }
}
