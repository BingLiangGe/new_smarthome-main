package com.lj.iot.api.app.web.open;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lj.iot.biz.db.smart.entity.PublicFile;
import com.lj.iot.biz.db.smart.service.IPublicFileService;
import com.lj.iot.common.base.vo.CommonResultVo;
import com.lj.iot.common.util.OkHttpUtils;
import com.lj.iot.common.util.TokenDownload;
import io.netty.handler.codec.http.HttpResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 获取文件接口
 */
@RestController
@RequestMapping("api/open/publicfile")
public class PublicFileController {
    @Resource
    IPublicFileService publicFileService;
    /**
     *  查询文件接口
     * @param fileName 文件名称
     * @return
     */
    @GetMapping("getFile")
    public CommonResultVo<PublicFile> findPublicFileByFileName(@RequestParam(value = "fileName") String fileName){
        return CommonResultVo.SUCCESS(publicFileService.getOne(new QueryWrapper<>(PublicFile.builder()
                .fileName(fileName).build())));
    }

    /**
     * 辉联红外离线包下载
     */
    @RequestMapping("ir_downLoad")
    public CommonResultVo<String> IrDownLoad(@RequestParam("kfid")String kfid, HttpServletResponse response){
        String mac = "ff92e0f2cd6d30a5";
        //辉联内部token获取
        String tokens = TokenDownload.getTokens(mac, kfid);
        //请求辉联红外包数据
        try {
            System.out.println("http://ir.hongwaimaku.com/vipdownload.php?kfid=" + kfid + "&mac=ff92e0f2cd6d30a5&tokens=" + tokens);
            String result = OkHttpUtils.get("http://ir.hongwaimaku.com/vipdownload.php?kfid=" + kfid + "&mac=ff92e0f2cd6d30a5&tokens=" + tokens);
            File file =new File("E:/ir_files/"+kfid+".bin");
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(file.getName(),true);

            JSONObject object = JSONObject.parseObject(result);
            fileWritter.write(object.get("data").toString());

            fileWritter.close();

            System.out.println("finish");

            return CommonResultVo.SUCCESS(result);
        } catch (IOException e) {
            e.printStackTrace();
            return CommonResultVo.FAILURE();
        }

    }

    public static void main(String[] args) throws IOException {
        String kfid = "C010164";
        String mac = "ff92e0f2cd6d30a5";
        //辉联内部token获取
        String tokens = TokenDownload.getTokens(mac, kfid);
        System.out.println("http://ir.hongwaimaku.com/vipdownload.php?kfid=" + kfid + "&mac=ff92e0f2cd6d30a5&tokens=" + tokens);
        String result = OkHttpUtils.get("http://ir.hongwaimaku.com/vipdownload.php?kfid=" + kfid + "&mac=ff92e0f2cd6d30a5&tokens=" + tokens);


        String fileName = "E:/ir_files/"+kfid+".bin";

        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(result.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("finish");


    }


    public class Node {
        public int data;
        public Node next;

        public Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    public class LinkedList {
        private Node head;

        public LinkedList() {
            this.head = null;
        }

        // 在链表头部插入节点
        public void addFirst(int data) {
            Node newNode = new Node(data);
            newNode.next = head;
            head = newNode;
        }

        // 在链表尾部插入节点
        public void addLast(int data) {
            Node newNode = new Node(data);
            if (head == null) {
                head = newNode;
                return;
            }
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }

        // 删除指定节点
        public void remove(int data) {
            if (head == null) {
                return;
            }
            if (head.data == data) {
                head = head.next;
                return;
            }
            Node current = head;
            while (current.next != null && current.next.data != data) {
                current = current.next;
            }
            if (current.next != null) {
                current.next = current.next.next;
            }
        }

        // 查找指定节点
        public Node find(int data) {
            if (head == null) {
                return null;
            }
            Node current = head;
            while (current != null && current.data != data) {
                current = current.next;
            }
            return current;
        }
    }
}
