package com.ech2o.weiyun.controller;

import com.alibaba.fastjson.JSONObject;
import com.ech2o.weiyun.jsoup.MockWeiyun;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by KAI on 2018/5/18.
 * ectest@foxmail.com
 */
@RestController
public class Magnet2DownloadController {


    @GetMapping("m2d")
    public String m2d(@RequestParam String magnet, String cookie) throws IOException {
        String msg = "试用次数已使用完毕或未找到相应种子,后续将开放贡献 cookie";
        boolean success = false;
        JSONObject invokeResult = new JSONObject();
        if (isMagnet(magnet)) {
            if (StringUtils.isNotEmpty(cookie)) {
                MockWeiyun.setCookie(cookie);
            }
            String beforeResult = MockWeiyun.weiyunOdOfflineDownloadClientBefore(magnet);
            if (beforeResult != null && !beforeResult.contains("3000") && !beforeResult.contains("25301")) {
                String saveResult = MockWeiyun.weiyunOdOfflineDownloadClientSave(beforeResult);
                if (!saveResult.contains("3000") && !saveResult.contains("25313")) {
                    invokeResult.put("videoUrl", MockWeiyun.getDownloadUrlByMovieName());
                    msg = "successfully";
                    success = true;
                }
            }
        } else {
            msg = "错误的磁力链接";
        }
        invokeResult.put("msg", msg);
        invokeResult.put("success", success);
        return invokeResult.toJSONString();
    }

    private boolean isMagnet(String magnet) {
        return Pattern.matches("^(magnet:\\?xt=urn:btih:)[0-9a-fA-F]{40}.*$", magnet);
    }
}
