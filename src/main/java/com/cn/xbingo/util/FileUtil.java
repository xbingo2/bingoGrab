/**
 * FileUtil.java
 * Created at 2018-10-9
 * Created by Administrator
 * Copyright (C) 2018 BROADTEXT SOFTWARE, All rights reserved.
 */
package com.cn.xbingo.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * <p>
 * ClassName: FileUtil
 * </p>
 * <p>
 * Description: 文件生成工具
 * </p>
 * <p>
 * Author: Administrator
 * </p>
 * <p>
 * Date: 2018-10-9
 * </p>
 */
public class FileUtil {
    /**
     * 获得类的基路径，打成jar包也可以正确获得路径
     * 
     * @return
     */
    public static String getBasePath() {
        String filePath = FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        if (filePath.endsWith(".jar")) {
            filePath = filePath.substring(0, filePath.lastIndexOf("/"));
            try {
                filePath = URLDecoder.decode(filePath, "UTF-8"); //解决路径中有空格%20的问题  
            } catch (UnsupportedEncodingException ex) {

            }
        }
        File file = new File(filePath);
        filePath = file.getAbsolutePath();
        return filePath;
    }

    /**
     * 获取exe文件路径
     * @return
     * @throws IOException
     */
    public static String getExePath() {
        return new File("").getAbsolutePath();
    }
}
