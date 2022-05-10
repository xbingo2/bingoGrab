package com.cn.xbingo.handler.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.xbingo.constant.CommonConstants;
import com.cn.xbingo.handler.AbstractHandler;
import com.cn.xbingo.util.Mp3;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class XmlyHandler extends AbstractHandler<String> {

    @Override
    public void getInfo(String id) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 100, 100000, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(100000));
        String listUrl = "https://www.ximalaya.com/revision/album/v1/getTracksList?sort=0&pageSize=100&";
        String mp3Url = "https://www.ximalaya.com/revision/play/v1/audio?ptype=1&id=";
        try {

            for (int i = 1; i < 1000; i ++) {
                Document document = Jsoup.connect(listUrl + "albumId="+ id +"&pageNum="+ i).userAgent(CommonConstants.USER_AGENT).get();
                Element body = document.body();
                JSONObject json = JSONObject.parseObject(body.text());
                JSONObject trackData = json.getJSONObject("data");
                Integer trackTotalCount = trackData.getInteger("trackTotalCount");
                JSONArray trackArray = trackData.getJSONArray("tracks");
                Integer index = trackTotalCount/100 + (trackTotalCount%100 > 0 ? 1 : 0);
                if (i > index) {
                    break;
                }
                for (int k = 0; k < trackArray.size(); k ++) {
                    Document mp3Doc = Jsoup.connect(mp3Url + trackArray.getJSONObject(k).getString("trackId")).userAgent(CommonConstants.USER_AGENT).get();
                    Element mp3Body = mp3Doc.body();
                    JSONObject mp3Obj = JSONObject.parseObject(mp3Body.text());
                    if (mp3Obj.getJSONObject("data").getBoolean("canPlay")) {
                        Mp3 myTask = new Mp3(mp3Obj.getJSONObject("data").getString("src"), getOutputPath() + trackArray.getJSONObject(k).getString("title") + ".mp3");
                        executor.execute(myTask);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }

}
