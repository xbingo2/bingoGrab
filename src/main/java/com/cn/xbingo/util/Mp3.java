package com.cn.xbingo.util;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class Mp3 implements Runnable {

    private static final int BUFFER_SIZE = 1024;
    private String destUrl;
    private String fileName;

    public Mp3(String destUrl, String fileName)
    {
        this.destUrl = destUrl;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        try {
            if (destUrl.startsWith("https")) {
                downLoadFromUrlHttps(destUrl,fileName);
            }
            else {
                saveToFile(destUrl,fileName);
            }

            System.out.println("文件："+destUrl+"下载完成，保存为"+fileName);
        } catch (Exception e) {
            System.out.println("文件下载失败，信息："+e.getMessage());
        }
    }

// 将网络文件保存为本地文件的方法
// @param destUrl
// @param fileName
// @throws IOException

    public void saveToFile(String destUrl, String fileName) throws Exception {
        URL url = new URL(destUrl);
        byte[] buffer=new byte[1024];
        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
        urlCon.setConnectTimeout(6000);
        urlCon.setReadTimeout(6000);
        int code = urlCon.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            throw new Exception("文件读取失败" + code);
        }
        //读文件流；
        DataInputStream in = new DataInputStream(urlCon.getInputStream());
        DataOutputStream out = new DataOutputStream(new FileOutputStream(fileName));
        int count = 0;
        while ((count = in.read(buffer)) > 0) {
            out.write(buffer, 0, count);
        }
        out.close();
        in.close();
    }

    public void downLoadFromUrlHttps(String urlStr, String fileName) {
        try {
            // 创建SSLContext
            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManager[] tm = {new MyX509TrustManager()};
            // 初始化
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 获取SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            // url对象
            URL url = new URL(urlStr);
            // 打开连接
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("User-agent","Mozilla/5.0 (Windows NT 6.1; rv:30.0) Gecko/20100101 Firefox/30.0");

            /*
             * 这一步的原因: 当访问HTTPS的网址。您可能已经安装了服务器证书到您的JRE的keystore
             * 但是服务器的名称与证书实际域名不相等。这通常发生在你使用的是非标准网上签发的证书。
             *
             * 解决方法：让JRE相信所有的证书和对系统的域名和证书域名。
             *
             * 如果少了这一步会报错:java.io.IOException: HTTPS hostname wrong: should be <localhost>
             */
            conn.setHostnameVerifier(new MyX509TrustManager().new TrustAnyHostnameVerifier());
            // 设置一些参数
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置当前实例使用的SSLSoctetFactory
            conn.setSSLSocketFactory(ssf);
            conn.connect();

            DataInputStream in = new DataInputStream(conn.getInputStream());
            DataOutputStream out = new DataOutputStream(new FileOutputStream(fileName));
            byte[] buffer=new byte[1024];
            int count = 0;
            while ((count = in.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }
            out.close();
            in.close();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

