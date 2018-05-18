package com.ech2o.weiyun.jsoup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ech2o.weiyun.constant.WeiyunApi;
import com.ech2o.weiyun.util.WeiyunHttpRequestUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by KAI on 2018/5/14.
 * ectest@foxmail.com
 */
public class MockWeiyun {

    public static void setCookie(String cookie) {
        MockWeiyun.cookie = cookie;
    }

    private static String cookie = "pgv_pvid=9686645906; web_wx_rc=JJJLTKTBFV; uin=o3338614228; skey=@0SXLIrAau; pt2gguin=o3338614228; p_uin=o3338614228; pt4_token=tjE6YjXBjKpGbbah7g57NLRXQHz6QIN9ngCGGgZTgxc_; p_skey=NNmRuUzVm1bhlZ*TLn*qd76Ddqt8CpLZnin*GZQc4-8_";
    private static String skey = getValue("skey=");
    private static String uid = getValue("uin=0");


    /**
     * 获得文件列表
     *
     * @return
     * @throws IOException
     */
    public static String getFileList() throws IOException {
        String payload = "{\"req_header\":\"{\\\"seq\\\":15262837952146048,\\\"type\\\":1,\\\"cmd\\\":26111,\\\"appid\\\":30013,\\\"version\\\":3,\\\"major_version\\\":3,\\\"minor_version\\\":3,\\\"fix_version\\\":3,\\\"wx_openid\\\":\\\"\\\",\\\"user_flag\\\":0}\",\"req_body\":\"{\\\"ReqMsg_body\\\":{\\\"ext_req_head\\\":{\\\"token_info\\\":{\\\"token_type\\\":0,\\\"login_key_type\\\":1,\\\"login_key_value\\\":\\\"" + skey + "\\\"}},\\\".weiyun.LibPageListGetMsgReq_body\\\":{\\\"lib_id\\\":4,\\\"sort_type\\\":1,\\\"offset\\\":0,\\\"count\\\":100,\\\"group_id\\\":0}}}\"}";
        return WeiyunHttpRequestUtils.post(WeiyunApi.FILE_LIST, payload, cookie, null);
    }

    public static void main(String[] args) {
        try {
            JSONObject jsonObject = JSON.parseObject(getFileList());
            if (jsonObject.getInteger("ret") != null) {
                System.out.println(jsonObject.toJSONString());
                return;
            }
            JSONArray fileList = jsonObject.getJSONObject("data").getJSONObject("rsp_body").getJSONObject("RspMsg_body").getJSONArray("FileItem_items");
            fileList.forEach(file -> {
                JSONObject fileObject = JSON.parseObject(file.toString());
                String url = "https://www.weiyun.com/video_preview?videoID=" + fileObject.getString("file_id") + "&dirKey=" + fileObject.getString("pdir_key") + "&pdirKey=" + fileObject.getString("ppdir_key");
                System.out.println("playUrl ：==>" + url);
                delimiter();
                try {
                    weiyunDownloadParse(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            delimiter();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 提交离线下载
     *
     * @param magnet
     * @return
     * @throws IOException
     */
    public static String weiyunOdOfflineDownloadClientBefore(String magnet) throws IOException {
        String payload = "{\"req_header\":\"{\\\"seq\\\":15263540663690256,\\\"type\\\":1,\\\"cmd\\\":28211,\\\"appid\\\":30013,\\\"version\\\":3,\\\"major_version\\\":3,\\\"minor_version\\\":3,\\\"fix_version\\\":3,\\\"wx_openid\\\":\\\"\\\",\\\"user_flag\\\":0}\",\"req_body\":\"{\\\"ReqMsg_body\\\":{\\\"ext_req_head\\\":{\\\"token_info\\\":{\\\"token_type\\\":0,\\\"login_key_type\\\":1,\\\"login_key_value\\\":\\\"" + skey + "\\\"}},\\\".weiyun.OdAddUrlTaskMsgReq_body\\\":{\\\"url\\\":\\\"" + magnet + "\\\"}}}\"}";
        return WeiyunHttpRequestUtils.post(WeiyunApi.WEIYUN_ODOFFLINE_DOWNLOAD_BEFORE, payload, cookie, null);
    }

    public static String weiyunCompass() throws IOException {
        String payload = "{\"uin\":\"" + uid + "\",\"domain\":\"www.weiyun.com\",\"url\":\"https://www.weiyun.com/disk/folder/e907dc43b950a50858e44864e6174ac2\",\"env\":1,\"proto\":\"weiyunOdOfflineDownloadClient\",\"cmdname\":\"OdAddUrlTask\",\"cmd\":28211,\"code\":0,\"result\":0}";
        return WeiyunHttpRequestUtils.post(WeiyunApi.WEIYUN_COMPASS, payload, cookie, null);
    }


    /**
     * 保存离线下载
     *
     * @param fileInfo
     * @return
     * @throws IOException
     */
    public static String weiyunOdOfflineDownloadClientSave(String fileInfo) throws IOException {
        JSONObject beforeResult = JSON.parseObject(fileInfo).getJSONObject("data").getJSONObject("rsp_body").getJSONObject("RspMsg_body");
        String torrentHex = beforeResult.getString("torrent_hex");
        JSONArray fileArray = beforeResult.getJSONArray("file_list");
        String dirName = beforeResult.getString("dir_name");
        String payload = "{\"req_header\":\"{\\\"seq\\\":15266255876846218,\\\"type\\\":1,\\\"cmd\\\":28210,\\\"appid\\\":30013,\\\"version\\\":3,\\\"major_version\\\":3,\\\"minor_version\\\":3,\\\"fix_version\\\":3,\\\"wx_openid\\\":\\\"\\\",\\\"user_flag\\\":0}\",\"req_body\":\"{\\\"ReqMsg_body\\\":{\\\"ext_req_head\\\":{\\\"token_info\\\":{\\\"token_type\\\":0,\\\"login_key_type\\\":1,\\\"login_key_value\\\":\\\"" + skey + "\\\"}},\\\".weiyun.OdAddBtTaskMsgReq_body\\\":{\\\"torrent_hex\\\":\\\"" + torrentHex + "\\\",\\\"is_default_dir\\\":true,\\\"dir_name\\\":\\\"" + dirName + "\\\",\\\"ppdir_key\\\":\\\"\\\",\\\"pdir_key\\\":\\\"\\\",\\\"file_list\\\":myFileList}}}\"}";
        String movieList = StringEscapeUtils.escapeJson(fileArray.toString());
        payload = payload.replace("myFileList", movieList);
        System.out.println("popopopo" + payload);
        delimiter();
        return WeiyunHttpRequestUtils.post(WeiyunApi.WEIYUN_ODOFFLINE_DOWNLOAD_SAVE, payload, cookie, null);
    }

    /**
     * 解析播放页面的 js 变量 window.syncData 获得下载地址
     *
     * @param playUrl
     * @return
     * @throws IOException
     */
    public static String weiyunDownloadParse(String playUrl) throws IOException {
        Document document = Jsoup.connect(playUrl).timeout(1000).header("cookie", cookie).get();
        Elements elements = document.getElementsByTag("script").eq(0);
        String downloadUrl = element2downloadUrl(elements.get(0));
        System.out.println("downloadUrl: ==>" + downloadUrl);
        delimiter();
        return downloadUrl;
    }

    private static String element2downloadUrl(Element weiyun) {
        JSONObject weiyunInfo = JSON.parseObject(weiyun.data().replace("window.syncData = ", "").replace(";", ""));
        JSONObject error = weiyunInfo.getJSONObject("error");
        if (error.size() > 0) {
            String errorMsg = error.getString("msg");
            System.out.println(errorMsg);
            return errorMsg;
        }
        JSONObject cloudPlayInfo = weiyunInfo.getJSONObject("cloudPlayInfo");
        JSONObject originVideo = cloudPlayInfo.getJSONObject("origin_video_play_info");
        JSONObject videoInfo = weiyunInfo.getJSONObject("videoInfo");
        System.out.println("movie name ==> : " + videoInfo.getString("filename"));
        return "http://" + originVideo.getString("server_name") + ":" + originVideo.getString("server_port") + "/ftn_handler/" + originVideo.getString("encode_url") + "/" + videoInfo.getString("filename");
    }

    private static void delimiter() {
        System.out.println("----------------------------------------------------------------------------------------------------------------------");

    }


    /**
     * 获得 cookie 字典
     *
     * @param key
     * @return
     */
    private static String getValue(String key) {
        List<String> cookies = Arrays.asList(cookie.trim().split(";"));
        for (String v : cookies) {
            if (v.contains(key)) {
                return v.replace(key, "").trim();
            }
        }
        return null;
    }

    @Test
    public void sss() throws IOException {
        String offlineDownloadBeforeResult = weiyunOdOfflineDownloadClientBefore("magnet:?xt=urn:btih:5e845d1e358000a04fc4257555b8e2a5144ab27b");
        System.out.println("offline download before result：==>" + offlineDownloadBeforeResult);
        delimiter();
        weiyunCompass();
        String offlineDownloadSaveResult = weiyunOdOfflineDownloadClientSave(offlineDownloadBeforeResult);
        System.out.println("offline download save result: ==>" + offlineDownloadSaveResult);
        delimiter();
    }

}
