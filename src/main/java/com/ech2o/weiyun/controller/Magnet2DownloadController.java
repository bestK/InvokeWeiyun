package com.ech2o.weiyun.controller;

import com.alibaba.fastjson.JSONObject;
import com.ech2o.weiyun.jsoup.MockWeiyun;
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
    public String m2d(@RequestParam String magnet) throws IOException {
        String msg = "错误的磁力链接";
        JSONObject invokeResult = new JSONObject();
        if (isMagnet(magnet)) {
            MockWeiyun.setCookie("pt2gguin=o2554497177; web_wx_rc=TSIHJV; uin=o2554497177; skey=@dQjxzvoSe; p_uin=o2554497177; pt4_token=NBLBn7mF7ySz6Y5K2t-dBZFlk7nCfq18r5lXi5R8aQA_; p_skey=UxQbYCuaugNTa8XwYSFuK448gjKce-jDEZ5zAZ65sIU_");
            String beforeResult = MockWeiyun.weiyunOdOfflineDownloadClientBefore(magnet);
            if (beforeResult.contains("3000")) {
                msg = "试用次数已使用完毕";
            } else {
                String saveResult = MockWeiyun.weiyunOdOfflineDownloadClientSave(beforeResult);
                if (!(saveResult.contains("3000") || saveResult.contains("25313"))) {
                    invokeResult.put("videoUrl", MockWeiyun.getDownloadUrlByMovieName());
                    msg = "successfully";
                }
            }

        }
        invokeResult.put("msg", msg);
        return invokeResult.toJSONString();
    }

    private boolean isMagnet(String magnet) {
        return Pattern.matches("^(magnet:\\?xt=urn:btih:)[0-9a-fA-F]{40}.*$", magnet);
    }
}
