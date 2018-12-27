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

    public static String cookie = "pgv_pvid=9686645906; pt2gguin=o17; web_wx_rc=AGTDKY; pgv_pvi=483587072; pgv_si=s6938560512; uin=o1138493417; skey=@QFJZtX9p4; ptisp=ctc; ptcz=e7a08ad98a3bec680ce15625847a5cdb9f531121cbb9a99adebd1b7357f165a6; p_uin=o1138493417; pt4_token=DzCaXmlQq-9YPQxLPfIebJxHfZSZNQFnrLEatGOuU2w_; p_skey=jnG*8lhlMyRtiiL7HhP0bTsW5y7JB0wSTHlA5l18TOA_; wyctoken=535246355";

    public static void setCookie(String cookie) {
        MockWeiyun.cookie = cookie;
    }

    private static String skey = getValue("skey=");
    private static String uid = getValue("uin=0");
    private static String g_tk = null;
    private static String movieName;
    private static String movieUrl;


    /**
     * 获得文件列表
     *
     * @return
     * @throws IOException
     */
    public static String getFileList(String payload) throws IOException {
        return WeiyunHttpRequestUtils.post(WeiyunApi.FILE_LIST + gtk(), payload, cookie, null);
    }

    private static String dirPayload(String dirHash) {
        String atHash = dirHash != null ? dirHash : "4aaf0d1af0a739ae12b58dcd423dce4a";
        return "{\"req_header\":\"{\\\"seq\\\":15272314269809008,\\\"type\\\":1,\\\"cmd\\\":2209,\\\"appid\\\":30013,\\\"version\\\":3,\\\"major_version\\\":3,\\\"minor_version\\\":3,\\\"fix_version\\\":3,\\\"wx_openid\\\":\\\"\\\",\\\"user_flag\\\":0}\",\"req_body\":\"{\\\"ReqMsg_body\\\":{\\\"ext_req_head\\\":{\\\"token_info\\\":{\\\"token_type\\\":0,\\\"login_key_type\\\":1,\\\"login_key_value\\\":\\\"" + skey + "\\\"}},\\\".weiyun.DiskDirBatchListMsgReq_body\\\":{\\\"pdir_key\\\":\\\"4aaf0d1af338fdcb41c5dfa52b9ed888\\\",\\\"dir_list\\\":[{\\\"dir_key\\\":\\\"" + atHash + "\\\",\\\"get_type\\\":0,\\\"start\\\":0,\\\"count\\\":100,\\\"sort_field\\\":2,\\\"reverse_order\\\":false,\\\"get_abstract_url\\\":true,\\\"get_dir_detail_info\\\":true}]}}}\"}";
    }


    /**
     * 根据电影名字获得下载地址
     *
     * @return
     * @throws IOException
     */
    public static String getDownloadUrlByMovieName() throws IOException {
        JSONObject jsonObject = JSON.parseObject(getFileList(dirPayload(null)));
        if (jsonObject.getInteger("ret") != null) {
            return jsonObject.toJSONString();
        }
        JSONArray fileList = JSON.parseObject(jsonObject.getJSONObject("data").getJSONObject("rsp_body").getJSONObject("RspMsg_body").getJSONArray("dir_list").get(0).toString()).getJSONArray("dir_list");
        fileList.forEach(file -> {
            JSONObject fileObject = JSON.parseObject(file.toString());
            if (fileObject.getString("dir_name").equals("离线下载")) {
                try {
                    JSONObject offlineDir = JSON.parseObject(getFileList(dirPayload(fileObject.getString("dir_key"))));

                    JSONArray offlineDirMovies = JSON.parseObject(offlineDir.getJSONObject("data").getJSONObject("rsp_body").getJSONObject("RspMsg_body").getJSONArray("dir_list").get(0).toString()).getJSONArray("dir_list");
                    offlineDirMovies.forEach(movie -> {
                        JSONObject movieObj = JSON.parseObject(movie.toString());
                        String dirName = movieObj.getString("dir_name");
                        if (dirName.equals(movieName)) {
                            try {
                                String dirkey = movieObj.getString("dir_key");
                                JSONObject atMovie = JSON.parseObject(getFileList(dirPayload(dirkey)));
                                JSONObject bese = JSON.parseObject(atMovie.getJSONObject("data").getJSONObject("rsp_body").getJSONObject("RspMsg_body").getJSONArray("dir_list").get(0).toString());
                                JSONArray mv = bese.getJSONArray("file_list");
                                mv.forEach(v -> {
                                    JSONObject mvObj = JSON.parseObject(v.toString());
                                    if (mvObj.getString("filename").contains(movieName)) {
                                        String url = "https://www.weiyun.com/video_preview?videoID=" + mvObj.getString("file_id") + "&dirKey=" + dirkey + "&pdirKey=" + mvObj.getString(bese.getString("pdir_key"));
                                        System.out.println("playUrl ：==>" + url);
                                        try {
                                            movieUrl = weiyunDownloadParse(url);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    System.out.println(offlineDir.toJSONString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        return movieUrl;
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
        return WeiyunHttpRequestUtils.post(WeiyunApi.WEIYUN_ODOFFLINE_DOWNLOAD_BEFORE + gtk(), payload, cookie, null);
    }

    /**
     * 有可能是个心跳类的东西
     * @return
     * @throws IOException
     */
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
        movieName = dirName;
        String payload = "{\"req_header\":\"{\\\"seq\\\":15266255876846218,\\\"type\\\":1,\\\"cmd\\\":28210,\\\"appid\\\":30013,\\\"version\\\":3,\\\"major_version\\\":3,\\\"minor_version\\\":3,\\\"fix_version\\\":3,\\\"wx_openid\\\":\\\"\\\",\\\"user_flag\\\":0}\",\"req_body\":\"{\\\"ReqMsg_body\\\":{\\\"ext_req_head\\\":{\\\"token_info\\\":{\\\"token_type\\\":0,\\\"login_key_type\\\":1,\\\"login_key_value\\\":\\\"" + skey + "\\\"}},\\\".weiyun.OdAddBtTaskMsgReq_body\\\":{\\\"torrent_hex\\\":\\\"" + torrentHex + "\\\",\\\"is_default_dir\\\":true,\\\"dir_name\\\":\\\"" + dirName + "\\\",\\\"ppdir_key\\\":\\\"\\\",\\\"pdir_key\\\":\\\"\\\",\\\"file_list\\\":myFileList}}}\"}";
        String movieList = StringEscapeUtils.escapeJson(fileArray.toString());
        payload = payload.replace("myFileList", movieList);
        delimiter();
        return WeiyunHttpRequestUtils.post(WeiyunApi.WEIYUN_ODOFFLINE_DOWNLOAD_SAVE + gtk(), payload, cookie, null);
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

    /**
     * 获取播放页面的 windows.syncData
     * @param weiyun
     * @return
     */
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


    /**
     * 查询提交的离线任务
     * @return
     * @throws IOException
     */
    public static String weiyunTasks() throws IOException {
        String payload = "{\"req_header\":\"{\\\"seq\\\":15318983146925638,\\\"type\\\":1,\\\"cmd\\\":28220,\\\"appid\\\":30013,\\\"version\\\":3,\\\"major_version\\\":3,\\\"minor_version\\\":3,\\\"fix_version\\\":3,\\\"wx_openid\\\":\\\"\\\",\\\"user_flag\\\":0}\",\"req_body\":\"{\\\"ReqMsg_body\\\":{\\\"ext_req_head\\\":{\\\"token_info\\\":{\\\"token_type\\\":0,\\\"login_key_type\\\":1,\\\"login_key_value\\\":\\\"" + skey + "\\\"}},\\\".weiyun.OdGetTaskListMsgReq_body\\\":{}}}\"}";
        return WeiyunHttpRequestUtils.post(WeiyunApi.WEIYUN_TASK_LIST + gtk(), payload, cookie, null);
    }


    /**
     * 文件搜索，没卵用
     * @param fileName
     * @return
     * @throws IOException
     */
    public String weiyunFileSearch(String fileName) throws IOException {
        String payload = "{\"req_header\":\"{\\\"seq\\\":15272364471634136,\\\"type\\\":1,\\\"cmd\\\":247251,\\\"appid\\\":30013,\\\"version\\\":3,\\\"major_version\\\":3,\\\"minor_version\\\":3,\\\"fix_version\\\":3,\\\"wx_openid\\\":\\\"\\\",\\\"user_flag\\\":0}\",\"req_body\":\"{\\\"ReqMsg_body\\\":{\\\"ext_req_head\\\":{\\\"token_info\\\":{\\\"token_type\\\":0,\\\"login_key_type\\\":1,\\\"login_key_value\\\":\\\"" + skey + "\\\"}},\\\".weiyun.FileSearchbyKeyWordMsgReq_body\\\":{\\\"type\\\":0,\\\"key_word\\\":\\\"" + fileName + "\\\",\\\"local_context\\\":\\\"\\\",\\\"location\\\":0}}}\"}";
        return WeiyunHttpRequestUtils.post(WeiyunApi.WEIYUN_FILE_SEARCH + gtk(), payload, cookie, null);
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


    private static String gtk() {
        if (g_tk == null) {
            //g_tk = "&g_tk=" + WeiyunHttpRequestUtils.getGTK(getValue("p_skey="));
            // 之前的 global token 是由 p_skey 算出来的，现在 微云会直接返回在 cookie 中 ，即 wyctoken
            g_tk = "&g_tk=" + getValue("wyctoken=");
        }
        return g_tk;
    }


    @Test
    public void sss() throws IOException {
        //System.out.println(getFileList(dirPayload("4aaf0d1a06f9a7ee01ee142fc7fc3fd1")));
        //System.out.println(weiyunDownloadParse("https://www.weiyun.com/video_preview?videoID=390dd94c-1e36-4611-a1db-6e63c2dc81a3&dirKey=4aaf0d1a7ad4b906e0a823f5cffd1788&pdirKey=4aaf0d1a06f9a7ee01ee142fc7fc3fd1"));
        System.out.println(weiyunFileSearch("战狼"));
        System.out.println(weiyunTasks());
    }


}
