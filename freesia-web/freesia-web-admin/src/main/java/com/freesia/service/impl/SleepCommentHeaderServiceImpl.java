package com.freesia.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.converter.SleepCommentHeaderConverter;
import com.freesia.dto.SleepCommentHeaderDto;
import com.freesia.mapper.SleepCommentHeaderMapper;
import com.freesia.po.SleepCommentHeaderPo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.repository.SleepCommentHeaderRepository;
import com.freesia.service.SleepCommentHeaderService;
import com.freesia.util.UEmpty;
import com.freesia.vo.SleepCommentHeaderVo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Evad.Wu
 * @Description 睡眠产品评论 业务逻辑类
 * @date 2026-03-23
 */
@Service
@RequiredArgsConstructor
public class SleepCommentHeaderServiceImpl extends BaseServiceImpl<SleepCommentHeaderMapper, SleepCommentHeaderVo, SleepCommentHeaderDto, SleepCommentHeaderPo> implements SleepCommentHeaderService {
    private final SleepCommentHeaderRepository sleepCommentHeaderRepository;
    private final SleepCommentHeaderMapper sleepCommentHeaderMapper;
    private final SleepCommentHeaderConverter sleepCommentHeaderConverter;

    @Override
    protected MapStructConverter<SleepCommentHeaderVo, SleepCommentHeaderDto, SleepCommentHeaderPo> getMapStructConverter() {
        return sleepCommentHeaderConverter;
    }

    @Override
    protected JpaRepository<SleepCommentHeaderPo, Long> getRepository() {
        return sleepCommentHeaderRepository;
    }

    @Override
    protected Class<SleepCommentHeaderDto> getDtoClass() {
        return SleepCommentHeaderDto.class;
    }

    @Override
    protected Class<SleepCommentHeaderPo> getPoClass() {
        return SleepCommentHeaderPo.class;
    }

    @Override
    protected Wrapper<SleepCommentHeaderPo> buildQueryWrapper(@NonNull SleepCommentHeaderDto sleepCommentHeaderDto) {
        return new LambdaQueryWrapper<SleepCommentHeaderPo>()
                .eq(SleepCommentHeaderPo::getLogicDel, FlagConstant.DISABLED)
                .eq(UEmpty.isNotEmpty(sleepCommentHeaderDto.getId()), SleepCommentHeaderPo::getId, sleepCommentHeaderDto.getId())
                .eq(UEmpty.isNotEmpty(sleepCommentHeaderDto.getSource()), SleepCommentHeaderPo::getSource, sleepCommentHeaderDto.getSource())
                .eq(UEmpty.isNotEmpty(sleepCommentHeaderDto.getUserId()), SleepCommentHeaderPo::getUserId, sleepCommentHeaderDto.getUserId())
                .eq(UEmpty.isNotEmpty(sleepCommentHeaderDto.getUserName()), SleepCommentHeaderPo::getUserName, sleepCommentHeaderDto.getUserName())
                .eq(UEmpty.isNotEmpty(sleepCommentHeaderDto.getContent()), SleepCommentHeaderPo::getContent, sleepCommentHeaderDto.getContent())
                .eq(UEmpty.isNotEmpty(sleepCommentHeaderDto.getLevel()), SleepCommentHeaderPo::getLevel, sleepCommentHeaderDto.getLevel())
                .eq(UEmpty.isNotEmpty(sleepCommentHeaderDto.getOperateTime()), SleepCommentHeaderPo::getOperateTime, sleepCommentHeaderDto.getOperateTime())
                .eq(UEmpty.isNotEmpty(sleepCommentHeaderDto.getFloor()), SleepCommentHeaderPo::getFloor, sleepCommentHeaderDto.getFloor())
                .eq(UEmpty.isNotEmpty(sleepCommentHeaderDto.getContentType()), SleepCommentHeaderPo::getContentType, sleepCommentHeaderDto.getContentType())
                .eq(UEmpty.isNotEmpty(sleepCommentHeaderDto.getUrl()), SleepCommentHeaderPo::getUrl, sleepCommentHeaderDto.getUrl())
                .eq(UEmpty.isNotEmpty(sleepCommentHeaderDto.getGrade()), SleepCommentHeaderPo::getGrade, sleepCommentHeaderDto.getGrade())
                .eq(UEmpty.isNotEmpty(sleepCommentHeaderDto.getCommentNum()), SleepCommentHeaderPo::getCommentNum, sleepCommentHeaderDto.getCommentNum())
                ;
    }

    @Override
    public TableResult<SleepCommentHeaderDto> findPage(SleepCommentHeaderDto dto, PageQuery pageQuery) {
        Page<SleepCommentHeaderPo> page = sleepCommentHeaderMapper.findPage(dto, pageQuery.build());
        return TableResult.build(sleepCommentHeaderConverter.convertPagePo2Dto(page));
    }

    @Override
    public List<SleepCommentHeaderDto> findList(SleepCommentHeaderDto dto) {
        return sleepCommentHeaderMapper.findList(dto);
    }

    @Override
    public SleepCommentHeaderDto findOne(SleepCommentHeaderDto dto) {
        return sleepCommentHeaderConverter.convertPo2Dto(sleepCommentHeaderMapper.findOne(dto));
    }

    @Override
    public void handleTrustPilot(String key) {
//        int maxPage = 31;
        int maxPage = 124;
        String targetUrl = "";
        List<SleepCommentHeaderDto> sleepCommentHeaderDtoList = new ArrayList<>();
        int count = 0;
        for (int i = 1; i <= maxPage; i++) {
            if (i == 1) {
//                targetUrl = "https://www.trustpilot.com/review/eightsleep.com";
                targetUrl = "https://www.trustpilot.com/review/sleepnumber.com";
            } else {
                targetUrl = "https://www.trustpilot.com/review/sleepnumber.com?page=" + i;
            }
            Connection connect = Jsoup.connect(targetUrl)
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890)))
                    .timeout(120000).maxBodySize(0);
            connect.header("Accept-Encoding", "gzip, deflate, br");
            connect.header("Cookie", "OptanonAlertBoxClosed=2026-03-23T13:11:58.003Z; tp-b2b-is-sso-saml-authenticated=false; tp-b2b-sso-saml-authenticated-business-account-id=; tp-b2b-sso-saml-recently-authenticated=false; ajs_anonymous_id=59cadb3b-b0b3-4c4f-8c3d-4e74e4aec4e8; analytics_session_id=1774271535124; _hjSessionUser_402766=eyJpZCI6Ijc2ZTE5Y2M5LTRlNzEtNTA2Zi1iZTBhLTIyZGM1MmMyZWQ5OCIsImNyZWF0ZWQiOjE3NzQyNzE1MzYxODAsImV4aXN0aW5nIjp0cnVlfQ==; _hjSession_402766=eyJpZCI6IjA3ZDJlMjc5LTNhODMtNDQzMi1iNjdjLWU2OWY1ZTEzZTVkYiIsImMiOjE3NzQyNzE1MzYxODEsInMiOjEsInIiOjAsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjoxLCJzcCI6MH0=; amplitude_idundefinedtrustpilot.com=eyJvcHRPdXQiOmZhbHNlLCJzZXNzaW9uSWQiOm51bGwsImxhc3RFdmVudFRpbWUiOm51bGwsImV2ZW50SWQiOjAsImlkZW50aWZ5SWQiOjAsInNlcXVlbmNlTnVtYmVyIjowfQ==; _hjSessionUser_6552561=eyJpZCI6ImUwOWVmNTdjLWIzZDUtNTZiMC05ODQ4LTU1YjZkNjY3NTQxZiIsImNyZWF0ZWQiOjE3NzQyNzE3NDU1OTAsImV4aXN0aW5nIjp0cnVlfQ==; _hjSession_6552561=eyJpZCI6IjEyYWY0M2FlLWZhNmItNDA2Yy04MzU1LWYwNjg5OWE1ZGVmYiIsImMiOjE3NzQyNzE3NDU1OTEsInMiOjEsInIiOjEsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjoxLCJzcCI6MH0=; _ga=GA1.1.1504951230.1774271752; _biz_uid=6e3999dfd618429bf58bb5000650d9b9; _biz_nA=1; _biz_pendingA=%5B%5D; _biz_flagsA=%7B%22Version%22%3A1%2C%22XDomain%22%3A%221%22%2C%22ViewThrough%22%3A%221%22%7D; TP.uuid=74585d80-d9fe-4333-a3e2-4d8016968dee; _hjSession_391767=eyJpZCI6IjExNjY2Y2VlLWVkMzAtNGZhYS04ZjM0LTYzZjhiMGVhYjI1YyIsImMiOjE3NzQyNzE3NjAyNzIsInMiOjAsInIiOjAsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjoxLCJzcCI6MH0=; _gcl_au=1.1.411991204.1774271765; _hjHasCachedUserAttributes=true; g_state={\"i_l\":0,\"i_ll\":1774271810907,\"i_e\":{\"enable_itp_optimization\":0}}; tp-consumer-id=69c0ae9607ee8af511fd5060; jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb25zdW1lcklkIjoiNjljMGFlOTYwN2VlOGFmNTExZmQ1MDYwIiwiaGFzQWNjZXB0ZWRUZXJtcyI6dHJ1ZSwiaXNCbG9ja2VkRm9yUmVwb3J0aW5nIjpmYWxzZSwiYWNjZXNzVG9rZW4iOiJVMEQ4YVR4MndtU2N1Q3Yyb2pVVHI1alRVQUY1IiwiYXV0aGVudGljYXRpb25Tb3VyY2UiOiJnb29nbGUiLCJpYXQiOjE3NzQyNzE4NTUsImV4cCI6MTc4MjA0Nzg1NX0.2ZBP3Ri81w2oPo8GKUxNGlbJ2RZ5QonsjptuYiWJ4Fk; ajs_user_id=9587c5cadd363eabd90cb57ffc4208078da21147; _hjSessionUser_391767=eyJpZCI6IjEwZjYzYWIzLWUyNTQtNTBlNy1hZmM2LWRlN2MxNDBmZDNhMyIsImNyZWF0ZWQiOjE3NzQyNzE3NjAyNzIsImV4aXN0aW5nIjp0cnVlfQ==; analytics_session_id.last_access=1774271873671; amplitude_id_de1e2fc13cf22ef1024015ecc1bb8ccdtrustpilot.com=eyJkZXZpY2VJZCI6IjIxZjkyNmEwLWIxYjQtNDQyNi05MmRhLTIwYjQ1NjRlNWNjNlIiLCJ1c2VySWQiOm51bGwsIm9wdE91dCI6ZmFsc2UsInNlc3Npb25JZCI6MTc3NDI3MTUzNjkxMiwibGFzdEV2ZW50VGltZSI6MTc3NDI3MTg3MzY4NiwiZXZlbnRJZCI6NiwiaWRlbnRpZnlJZCI6NCwic2VxdWVuY2VOdW1iZXIiOjEwfQ==; amplitude_id_67f7b7e6c8cb1b558b0c5bda2f747b07trustpilot.com=eyJkZXZpY2VJZCI6IjU5Y2FkYjNiLWIwYjMtNGM0Zi04YzNkLTRlNzRlNGFlYzRlOCIsInVzZXJJZCI6Ijk1ODdjNWNhZGQzNjNlYWJkOTBjYjU3ZmZjNDIwODA3OGRhMjExNDciLCJvcHRPdXQiOmZhbHNlLCJzZXNzaW9uSWQiOjE3NzQyNzE3NjE5MjksImxhc3RFdmVudFRpbWUiOjE3NzQyNzIwMTQwNzAsImV2ZW50SWQiOjE2LCJpZGVudGlmeUlkIjo0LCJzZXF1ZW5jZU51bWJlciI6MjB9; aws-waf-token=8f3affee-26d8-467a-a285-20a6a6afde0e:AAoAvedcLAArAAAA:wWaIWusBi2Ut+iM1DsSrvUaUW6FGKsgwV7kf5MpXa8TYC6G+EPbVNwZnyqkxZ4oysqeJAHE2yBgeqQjoIpcB+fZw9/hYD0crU73BYzdC64YKii/Y6u31osy9EY+0uYV/cCwhYzY7JHF7kUQWA1T2coimJG+pdtU6flfpQSI+1/0/vpbMIzvqAAXoqi4U5J3vM8bX2EEYYoVZXVH+1e4TPgyALra+jzLGSgUL4llII2Wo2yMbhf2rTGpb4pHJIjc2WYnN503EoDwz; _ga_11HBWMC274=GS2.1.s1774271751$o1$g1$t1774272040$j29$l0$h0; OptanonConsent=isGpcEnabled=0&datestamp=Mon+Mar+23+2026+21%3A20%3A40+GMT%2B0800+(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)&version=202602.1.0&browserGpcFlag=0&isIABGlobal=false&hosts=&consentId=2316e55d-4b3c-4154-8ea6-6758b9356ac9&interactionCount=1&isAnonUser=1&prevHadToken=0&landingPath=NotLandingPage&groups=C0001%3A1%2CC0002%3A1%2CC0003%3A1%2CC0004%3A1&intType=1&geolocation=HK%3B&AwaitingReconsent=false");
            connect.header("Referer", targetUrl);
            Element body = null;
            try {
                body = connect.get().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (body != null) {
                Elements elementList = body.select("div.styles_wrapper__ie3f0 > div.styles_cardWrapper__g8amG.styles_show__Z8n7u");
                if (UEmpty.isNotEmpty(elementList)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    int floor = 1;
                    for (Element element : elementList) {
                        SleepCommentHeaderDto dto = new SleepCommentHeaderDto();
                        dto.setSource(key);
                        Element userNameElement = element.selectFirst(".styles_reviewCardInnerHeader__8Xqy8 > aside > div > a > span");
                        String username = userNameElement.text();
                        System.out.println("userName: " + username);
                        Element operateTimeElement = element.selectFirst("div > article > div > div.styles_reviewCardInnerHeader__8Xqy8 > div > time");
                        if (operateTimeElement != null) {
                            String operateTime = operateTimeElement.attr("datetime");
                            try {
                                dto.setOperateTime(sdf.parse(operateTime));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            System.out.println("operateTime: " + operateTime);
                        }
                        Element levelElement = element.selectFirst("article > div > section > div.styles_reviewHeader__DzoAZ");
                        if (levelElement != null) {
                            String levelStr = levelElement.attr("data-service-review-rating");
                            if (UEmpty.isNotEmpty(levelStr)) {
                                int level = Integer.parseInt(levelStr);
                                dto.setLevel(levelStr);
                                System.out.println("level: " + level);
                            }
                        }
                        Element titleElement = element.selectFirst("article > div > section > div.styles_reviewContent__tuXiN > a > h2");
                        if (titleElement != null) {
                            String title = titleElement.text();
                            dto.setTitle(title);
                            System.out.println("title: " + title);
                        }
                        Element contentElement = element.selectFirst("article > div > section > div.styles_reviewContent__tuXiN > p");
                        if (contentElement != null) {
                            String content = contentElement.text();
                            dto.setContent(content);
                            System.out.println("content: " + content);
                        }
                        dto.setUserId(null);
                        dto.setUserName(username);
                        dto.setFloor(String.valueOf(floor));
                        dto.setContentType("COMMENT");
                        dto.setUrl(targetUrl);
                        sleepCommentHeaderDtoList.add(dto);
                        count++;
                    }
                } else {
                    break;
                }
            }
        }
        if (UEmpty.isNotEmpty(sleepCommentHeaderDtoList)) {
            for (SleepCommentHeaderDto dto : sleepCommentHeaderDtoList) {
                dto.setCommentNum(count);
            }
            saveUpdateBatch(sleepCommentHeaderDtoList);
        }

    }

    @Override
    public void handleReddit(String key) {

    }

    @Override
    public void handle3B(String key) {

    }
}
