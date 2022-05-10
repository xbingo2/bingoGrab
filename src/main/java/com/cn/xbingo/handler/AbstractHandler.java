package com.cn.xbingo.handler;

import com.alibaba.excel.EasyExcel;
import com.cn.xbingo.constant.CommonConstants;
import com.cn.xbingo.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 抽象处理类，所有的处理类都继承此类
 */
public abstract class AbstractHandler<T> {
    private List<T> infoList = new ArrayList<T>();

    private Class<T> clazz;

    private String fileName;

    private String sheetName;

    private String outputPath = FileUtil.getExePath() + File.separator;

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getOutputPath() {
        return this.outputPath;
    }

    /**
     * 获取网页数据
     */
    public abstract void getInfo(String id);

    public void hanlder(String id) {
        getInfo(id);
        insetExcel();
    }


    public void setBaseInfo(String fileName, String sheetName, Class<T> clazz) {
        this.fileName = fileName;
        this.sheetName = sheetName;
        this.clazz = clazz;
    }


    /**
     * 添加数据到list中
     * @param t
     */
    public void setInfoList(T t) {
        infoList.add(t);
    }

    /**
     * easyExcel导出数据
     */
    public void insetExcel() {
        try {
            if (!infoList.isEmpty()) {
                String pathName = this.fileName + System.currentTimeMillis() + CommonConstants.EXCEL_POSTFIX;
                EasyExcel.write(pathName, clazz).sheet(this.sheetName).doWrite(infoList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
