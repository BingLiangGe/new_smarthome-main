package com.lj.iot.common.util;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

/**
 * vsFtp上传工具类
 * @author tyj
 * @date   2018-01-20 22:49:24
 */
public class FtpJSch {

    private static ChannelSftp sftp = null;

    // 账号
    private static String user = "ftpuser";
    // 主机ip
    private static String host = "47.100.238.205";
    // 密码
    private static String password = "lj@123456";
    // 端口
    private static int port = 22;
    // 上传地址
    private static String directory = "/home/ftpuser/appPortrait";
    // 下载目录

    public static FtpJSch getConnect() {
        FtpJSch ftp = new FtpJSch();
        try {
            JSch jsch = new JSch();

            // 获取sshSession 账号-ip-端口
            Session sshSession = jsch.getSession(user, host, port);
            // 添加密码
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            // 严格主机密钥检查
            sshConfig.put("StrictHostKeyChecking", "no");

            sshSession.setConfig(sshConfig);
            // 开启sshSession链接
            sshSession.connect();
            // 获取sftp通道
            Channel channel = sshSession.openChannel("sftp");
            // 开启
            channel.connect();
            sftp = (ChannelSftp) channel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ftp;
    }

    /**
     *
     * @return 服务器上文件名
     */
    public static String uploadImg(InputStream file,String oldName) {
        getConnect();
        String fileName = null;
        try {
            fileName = UUID.randomUUID().toString().replace("-", "") +
                    oldName.substring(oldName.lastIndexOf("."));
            sftp.cd(directory); // 获取随机文件名
            // 文件名是 随机数加文件名的后5位
            sftp.put(file, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName == null ? null : fileName;
    }


    /**
     *
     * @param stream 上传二维码的路径
     * @return 服务器上文件名
     */
    public static String upload(InputStream stream, String newName) {
        getConnect();
        String fileName = null;
        try {
            fileName = newName + ".gif";
            sftp.cd(directory);
            // 文件名是 随机数加文件名的后5位
            sftp.put(stream, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName == null ? null : fileName;
    }

    /**
     * 删除文件
     * @param deleteFileName 要删除文件所在目录
     */
    public static void delete( String deleteFileName) {
        try {
            getConnect();
            sftp.cd(directory);
            sftp.rm(deleteFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @param file 上传文件的路径
     * @return 服务器上文件名
     */
    public static String uploadBackgroundImg(InputStream file,String oldName) {
        getConnect();
        String fileName = null;
        try {
            //	sftp.cd(directory); // 获取随机文件名
            // 文件名是 随机数加文件名的后5位
            sftp.cd(directory); // 获取随机文件名
            sftp.put(file, oldName);
        } catch (Exception e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName == null ? null : fileName;
    }


}
