package com.cn.xbingo;

import com.alibaba.excel.util.StringUtils;
import com.cn.xbingo.handler.AbstractHandler;
import com.cn.xbingo.util.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SwingFrame {

    private JFrame frame;

    private JPanel panel = new JPanel();

    private Map<String, String> labelMap = new LinkedHashMap<>();

    private Map<String, JTextField> jTextFieldMap = new LinkedHashMap<>();

    private JTextArea logTextArea = new JTextArea();

    private JScrollPane jsp = new JScrollPane(logTextArea);

    private JComboBox jComboBox = new JComboBox();

    private Map<String, AbstractHandler> handlerMap = new HashMap<>();

    private int labelWidth = 100,
            labelX = 10,
            labelStartY = 20,
            height = 25,
            textWidth = 320,
            textX = 120,
            textColumns = 20,
            padding = 30;

    public SwingFrame(String title, Map<String, AbstractHandler> handlerMap) {
        this.handlerMap = handlerMap;
        // 初始化各页面组件
        initFrame(title);
        initPane();
        initLabelMap();
        initLogTextArea();
        initButton();

        OutputStream textAreaStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                logTextArea.append(String.valueOf((char)b));
                logTextArea.paintImmediately(logTextArea.getBounds());
                jsp.getVerticalScrollBar().setValue(jsp.getVerticalScrollBar().getMaximum());
            }
            @Override
            public void write(byte b[]) throws IOException {
                logTextArea.append(new String(b));
                logTextArea.paintImmediately(logTextArea.getBounds());
                jsp.getVerticalScrollBar().setValue(jsp.getVerticalScrollBar().getMaximum());
            }
            @Override
            public void write(byte b[], int off, int len) throws IOException {
                logTextArea.append(new String(b, off, len));
                logTextArea.paintImmediately(logTextArea.getBounds());
                jsp.getVerticalScrollBar().setValue(jsp.getVerticalScrollBar().getMaximum());
            }
        };
        PrintStream myOut = new PrintStream(textAreaStream);
        System.setOut(myOut);
        System.setErr(myOut);
    }

    public JFrame getFrame() {
        return frame;
    }

    public void initFrame(String title) {
        frame = new JFrame(title);
        frame.setSize(700, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void initPane() {
        //设置布局为null，通过绝对定位来控制布局
        panel.setLayout(null);
        frame.add(panel);
    }

    public void initLabelMap() {
        JLabel label = new JLabel("项目id:");
        label.setBounds(labelX, labelStartY, labelWidth, height);
        panel.add(label);

        JTextField text = new JTextField(textColumns);
        text.setBounds(textX, labelStartY, textWidth, height);
        panel.add(text);
        jTextFieldMap.put("id", text);

        JLabel type = new JLabel("抓取种类:");
        type.setBounds(labelX, labelStartY + padding, labelWidth, height);
        panel.add(type);

        for (Map.Entry<String, AbstractHandler> entry : handlerMap.entrySet()) {
            jComboBox.addItem(entry.getKey());
        }
        jComboBox.setBounds(textX, labelStartY + padding, textWidth, height);
        panel.add(jComboBox);

        JLabel pathLabel = new JLabel("输出路径:");
        pathLabel.setBounds(labelX, labelStartY + (padding * 2), labelWidth, height);
        panel.add(pathLabel);

        JTextField filePath = new JTextField(textColumns);
        filePath.setBounds(textX, (labelStartY + 2 * padding), textWidth, height);
        filePath.setEditable(false);
        filePath.setText(FileUtil.getExePath() + File.separator);
        panel.add(filePath);

        JButton selectButton = new JButton("浏览");
        selectButton.setBounds(textX + textWidth + 10, (labelStartY + 2 * padding), 70, height);

        selectButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser fc = new JFileChooser(text.getText());
                fc.setMultiSelectionEnabled(false);
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.setDialogTitle("选择文件夹");

                int val = fc.showOpenDialog(null);    //文件打开对话框
                if(val == JFileChooser.APPROVE_OPTION)
                {
                    filePath.setText(fc.getSelectedFile().toString());
                    handlerMap.get("喜马拉雅").setOutputPath(fc.getSelectedFile().toString() + File.separator);
                }
            }
        });
        panel.add(selectButton);

    }

    public void initLogTextArea() {
        //日志输出框
        logTextArea.setEditable(false);
        logTextArea.setLineWrap(true);
        logTextArea.setFont(new Font("微软雅黑",Font.PLAIN,15));
        jsp.setBounds(labelX, labelStartY + (labelMap.size()+4) * padding, 660, 530);
        panel.add(jsp);
    }

    public void initButton() {
        // 创建全部生成按钮
        JButton allButton = new JButton("抓取");
        allButton.setBounds(labelX, labelStartY + 3 * padding, labelWidth, height);
        allButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String id = jTextFieldMap.get("id").getText();
                if (StringUtils.isBlank(id)) {
                    System.out.println("请输入项目id");
                    return;
                }
                String type = jComboBox.getSelectedItem().toString();
                if (StringUtils.isBlank(type)) {
                    System.out.println("请选择抓取类型");
                    return;
                }

                Thread thread = new Thread("handler"){
                    @Override
                    public  void run(){
                        System.out.println("抓取中");
                        handlerMap.get(type).hanlder(id);
                        System.out.println("抓取完成");
                    }
                };
                thread.start();
            }
        });
        panel.add(allButton);
    }

}
