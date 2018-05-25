package com.ech2o.weiyun.jsoup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ech2o.weiyun.constant.WeiyunApi;
import com.ech2o.weiyun.util.WeiyunHttpRequestUtils;
import lombok.experimental.var;
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

    private static String cookie = "pgv_pvid=9686645906; web_wx_rc=OTWVSHZRNBHGP; uin=o0437104458; skey=@62Rz4Jla1; pt2gguin=o0437104458; p_uin=o0437104458; pt4_token=ttnyB1iN0LK2pXIwEbs6W7x1hrVinsmm*sAQXNEgtfk_; p_skey=BAfUwpxutFKfoCbsBeIuv3UnZv1kRREEzaEBMozpkLk_; pgv_pvid=9686645906; web_wx_rc=OTWVSHZRNBHGP; uin=o0437104458; skey=@62Rz4Jla1; pt2gguin=o0437104458; p_uin=o0437104458; pt4_token=ttnyB1iN0LK2pXIwEbs6W7x1hrVinsmm*sAQXNEgtfk_; p_skey=BAfUwpxutFKfoCbsBeIuv3UnZv1kRREEzaEBMozpkLk_";
    private static String skey = getValue("skey=");
    private static String uid = getValue("uin=0");
    private static String movieName;
    private static String movieUrl;


    /**
     * 获得文件列表
     *
     * @return
     * @throws IOException
     */
    public static String getFileList() throws IOException {
        //String payload = "{\"req_header\":\"{\\\"seq\\\":15272240303038348,\\\"type\\\":1,\\\"cmd\\\":2209,\\\"appid\\\":30013,\\\"version\\\":3,\\\"major_version\\\":3,\\\"minor_version\\\":3,\\\"fix_version\\\":3,\\\"wx_openid\\\":\\\"\\\",\\\"user_flag\\\":0}\",\"req_body\":\"{\\\"ReqMsg_body\\\":{\\\"ext_req_head\\\":{\\\"token_info\\\":{\\\"token_type\\\":0,\\\"login_key_type\\\":1,\\\"login_key_value\\\":\\\"" + skey + "\\\"}},\\\".weiyun.DiskDirBatchListMsgReq_body\\\":{\\\"pdir_key\\\":\\\"e907dc43f0a739ae12b58dcd423dce4a\\\",\\\"dir_list\\\":[{\\\"dir_key\\\":\\\"e907dc43b950a50858e44864e6174ac2\\\",\\\"get_type\\\":0,\\\"start\\\":0,\\\"count\\\":100,\\\"sort_field\\\":2,\\\"reverse_order\\\":false,\\\"get_abstract_url\\\":true,\\\"get_dir_detail_info\\\":true}]}}}\"}";
        String payload = "{\"req_header\":\"{\\\"seq\\\":15272245014516668,\\\"type\\\":1,\\\"cmd\\\":2209,\\\"appid\\\":30013,\\\"version\\\":3,\\\"major_version\\\":3,\\\"minor_version\\\":3,\\\"fix_version\\\":3,\\\"wx_openid\\\":\\\"\\\",\\\"user_flag\\\":0}\",\"req_body\":\"{\\\"ReqMsg_body\\\":{\\\"ext_req_head\\\":{\\\"token_info\\\":{\\\"token_type\\\":0,\\\"login_key_type\\\":1,\\\"login_key_value\\\":\\\"@62Rz4Jla1\\\"}},\\\".weiyun.DiskDirBatchListMsgReq_body\\\":{\\\"pdir_key\\\":\\\"4aaf0d1af0a739ae12b58dcd423dce4a\\\",\\\"dir_list\\\":[{\\\"dir_key\\\":\\\"4aaf0d1a06f9a7ee01ee142fc7fc3fd1\\\",\\\"get_type\\\":0,\\\"start\\\":0,\\\"count\\\":100,\\\"sort_field\\\":2,\\\"reverse_order\\\":false,\\\"get_abstract_url\\\":true,\\\"get_dir_detail_info\\\":true}]}}}\"}";
        return WeiyunHttpRequestUtils.post(WeiyunApi.FILE_LIST, payload, cookie, null);
    }


    /**
     * 根据电影名字获得下载地址
     *
     * @return
     * @throws IOException
     */
    public static String getDownloadUrlByMovieName() throws IOException {
        JSONObject jsonObject = JSON.parseObject(getFileList());
        if (jsonObject.getInteger("ret") != null) {
            return jsonObject.toJSONString();
        }
        JSONArray fileList = jsonObject.getJSONObject("data").getJSONObject("rsp_body").getJSONObject("RspMsg_body").getJSONArray("FileItem_items");
        fileList.forEach(file -> {
            JSONObject fileObject = JSON.parseObject(file.toString());
            if (fileObject.getString("filename").contains(movieName)) {
                String url = "https://www.weiyun.com/video_preview?videoID=" + fileObject.getString("file_id") + "&dirKey=" + fileObject.getString("pdir_key") + "&pdirKey=" + fileObject.getString("ppdir_key");
                System.out.println("playUrl ：==>" + url);
                try {
                    movieUrl = weiyunDownloadParse(url);
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
        movieName = dirName;
        String payload = "{\"req_header\":\"{\\\"seq\\\":15266255876846218,\\\"type\\\":1,\\\"cmd\\\":28210,\\\"appid\\\":30013,\\\"version\\\":3,\\\"major_version\\\":3,\\\"minor_version\\\":3,\\\"fix_version\\\":3,\\\"wx_openid\\\":\\\"\\\",\\\"user_flag\\\":0}\",\"req_body\":\"{\\\"ReqMsg_body\\\":{\\\"ext_req_head\\\":{\\\"token_info\\\":{\\\"token_type\\\":0,\\\"login_key_type\\\":1,\\\"login_key_value\\\":\\\"" + skey + "\\\"}},\\\".weiyun.OdAddBtTaskMsgReq_body\\\":{\\\"torrent_hex\\\":\\\"" + torrentHex + "\\\",\\\"is_default_dir\\\":true,\\\"dir_name\\\":\\\"" + dirName + "\\\",\\\"ppdir_key\\\":\\\"\\\",\\\"pdir_key\\\":\\\"\\\",\\\"file_list\\\":myFileList}}}\"}";
        String movieList = StringEscapeUtils.escapeJson(fileArray.toString());
        payload = payload.replace("myFileList", movieList);
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

    /**
     * 获得 global token
     * @param p_skey
     * @return
     */
    private int getGTK(String p_skey) {
        int hash = 5381;
        for (int i = 0; i < p_skey.length(); i++) {
            hash += (hash << 5) + (int) p_skey.charAt(i);
        }
        return hash & 0x7fffffff;
    }


    @Test
    public void sss() throws IOException {
        //System.out.println(getFileList());
        String p_skey = "BAfUwpxutFKfoCbsBeIuv3UnZv1kRREEzaEBMozpkLk_";
        System.out.println(getGTK(p_skey));
    }


}
