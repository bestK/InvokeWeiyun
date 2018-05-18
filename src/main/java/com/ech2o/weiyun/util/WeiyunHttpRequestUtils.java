package com.ech2o.weiyun.util;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.IOException;

/**
 * Created by KAI on 2018/5/14.
 * ectest@foxmail.com
 */
public class WeiyunHttpRequestUtils {


    public static String post(String url, String payload, String cookie, Form form) throws IOException {
        String returnString = null;
        try {
            returnString = Request.Post(url).connectTimeout(3000)
                    .setHeader("cookie", cookie)
                    .bodyForm(form != null ? form.build() : Form.form().build())
                    .setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36")
                    .bodyString(payload, ContentType.APPLICATION_JSON).execute().returnContent().asString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnString;
    }
}
