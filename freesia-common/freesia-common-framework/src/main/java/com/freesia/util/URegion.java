package com.freesia.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HtmlUtil;
import com.freesia.exception.ServiceException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;

import java.io.File;

/**
 * @author Evad.Wu
 * @Description 获取IP所属地 工具类
 * 根据ip地址定位工具类，离线方式
 * 参考地址：<a href="https://gitee.com/lionsoul/ip2region/tree/master/binding/java">集成 ip2region 实现离线IP地址定位库</a>
 * @date 2023-08-14
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class URegion {
    public static final String INTRANET_IP = "内网IP";
    public static final String UNKNWON_IP = "未知IP";
    /**
     * 未知地址
     */
    public static final String UNKNOWN = "XX XX";
    /**
     * ip2region Searcher对象
     */
    private static final Searcher SEARCHER;

    static {
        String fileName = "/ip2region.xdb";
        File existFile = UFile.file(FileUtil.getTmpDir() + FileUtil.FILE_SEPARATOR + fileName);
        if (!UFile.exist(existFile)) {
            ClassPathResource fileStream = new ClassPathResource(fileName);
            if (ObjectUtil.isEmpty(fileStream.getStream())) {
                throw new ServiceException(UMessage.message("service.uregion.initialized.failed",
                        UMessage.message("service.uregion.ipregion.nonExistent")));
            }
            UFile.writeFromStream(fileStream.getStream(), existFile);
        }

        String dbPath = existFile.getPath();

        // 1、从 dbPath 加载整个 xdb 到内存。
        byte[] cBuff;
        try {
            cBuff = Searcher.loadContentFromFile(dbPath);
        } catch (Exception e) {
            throw new ServiceException(UMessage.message("service.uregion.initialized.failed",
                    UMessage.message("service.uregion.ipregion.load.failed")) + "\n" + e.getMessage());
        }
        // 2、使用上述的 cBuff 创建一个完全基于内存的查询对象。
        try {
            SEARCHER = Searcher.newWithBuffer(cBuff);
        } catch (Exception e) {
            throw new ServiceException(UMessage.message("service.uregion.initialized.failed", e.getMessage()));
        }
    }

    /**
     * 获取IP对应的所属地
     *
     * @param ip IP地址
     * @return 所属地
     */
    public static String getRealAddressByIp(String ip) {
        if (UEmpty.isEmpty(ip)) {
            return UNKNOWN;
        }
        // 内网不查询
        ip = "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : HtmlUtil.cleanHtmlTag(ip);
        if (NetUtil.isInnerIP(ip)) {
            return INTRANET_IP;
        }
        return getCityInfo(ip);
    }

    /**
     * 根据IP地址离线获取城市
     *
     * @param ip IP地址
     * @return 所属地
     */
    private static String getCityInfo(String ip) {
        try {
            ip = ip.trim();
            // 3、执行查询
            String region = SEARCHER.search(ip);
            return region.replace("0|", "").replace("|0", "");
        } catch (Exception e) {
            log.error("IP地址离线获取城市异常 {}", ip);
            return UNKNWON_IP;
        }
    }
}
