package com.cn.xbingo;

import com.cn.xbingo.handler.AbstractHandler;
import com.cn.xbingo.handler.impl.JgHandler;
import com.cn.xbingo.handler.impl.XmlyHandler;
import com.cn.xbingo.handler.impl.YaoHaoHandler;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        Map<String, AbstractHandler> handlerMap = new HashMap<>();
        handlerMap.put("摇号公示", new YaoHaoHandler());
        handlerMap.put("价格公示", new JgHandler());
        handlerMap.put("喜马拉雅", new XmlyHandler());
        SwingFrame swingFrame = new SwingFrame("bingo抓取工具", handlerMap);
        swingFrame.getFrame().setLocationRelativeTo(null);
        swingFrame.getFrame().setVisible(true);
    }

}
