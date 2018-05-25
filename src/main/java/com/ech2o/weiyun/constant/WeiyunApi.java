package com.ech2o.weiyun.constant;

/**
 * Created by KAI on 2018/5/15.
 * ectest@foxmail.com
 */
public class WeiyunApi {
    public static final String HOST = "https://www.weiyun.com";

    /**
     * 文件列表
     */
    public static final String FILE_LIST = HOST + "/webapp/json/weiyunQdisk/DiskDirBatchList?refer=chrome_windows&g_tk=1510002013&r=0.36542725989447056";

    /**
     * 提交离线下载
     */
    public static final String WEIYUN_ODOFFLINE_DOWNLOAD_BEFORE = HOST + "/webapp/json/weiyunOdOfflineDownloadClient/OdAddUrlTask?refer=chrome_windows&g_tk=420079050&r=0.3479153457630959";
    /**
     * 保存离线下载
     */
    public static final String WEIYUN_ODOFFLINE_DOWNLOAD_SAVE = HOST + "/webapp/json/weiyunOdOfflineDownloadClient/OdAddBtTask?refer=chrome_windows&g_tk=420079050&r=0.17209610498489747";
    public static final String WEIYUN_COMPASS = HOST + "/weiyun/compass/dc01956";
}
