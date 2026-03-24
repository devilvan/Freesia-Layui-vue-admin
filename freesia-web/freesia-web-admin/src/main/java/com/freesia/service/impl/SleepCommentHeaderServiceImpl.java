package com.freesia.service.impl;

import cn.hutool.core.convert.Convert;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.converter.SleepCommentHeaderConverter;
import com.freesia.dto.SleepCommentHeaderDto;
import com.freesia.entity.ExportSleepEntity;
import com.freesia.mapper.SleepCommentHeaderMapper;
import com.freesia.po.SleepCommentHeaderPo;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.repository.SleepCommentHeaderRepository;
import com.freesia.service.SleepCommentHeaderService;
import com.freesia.util.UCopy;
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
import java.util.*;
import java.util.stream.Collectors;

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
                .eq(UEmpty.isNotEmpty(sleepCommentHeaderDto.getPage()), SleepCommentHeaderPo::getPage, sleepCommentHeaderDto.getPage())
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
    public void handleTrustPilot(SleepCommentHeaderVo sleepCommentHeaderVo) {
//        int maxPage = 32;
//        String targetUrlTemplate = "https://www.trustpilot.com/review/eightsleep.com?languages=all";
//        int maxPage = 124;
//        String targetUrlTemplate = "https://www.trustpilot.com/review/sleepnumber.com?languages=all";
//        int maxPage = 5;
//        String targetUrlTemplate = "https://www.trustpilot.com/review/purple.com?languages=all";
//        int maxPage = 294;
//        String targetUrlTemplate = "https://www.trustpilot.com/review/casper.com?languages=all";
//        int maxPage = 2;
//        String targetUrlTemplate = "https://www.trustpilot.com/review/serta.com?languages=all";
//        int maxPage = 12;
//        String targetUrlTemplate = "https://www.trustpilot.com/review/tempurpedic.com?languages=all";
//        int maxPage = 4;
//        String targetUrlTemplate = "https://www.trustpilot.com/review/bryte.com?languages=all";
//        int maxPage = 60;
//        String targetUrlTemplate = "https://www.trustpilot.com/review/sleep.me?languages=all";
//        int maxPage = 191;
//        String targetUrlTemplate = "https://www.trustpilot.com/review/ouraring.com?languages=all";
//        int maxPage = 216;
//        String targetUrlTemplate = "https://www.trustpilot.com/review/whoop.com?languages=all";
        Integer maxPage = sleepCommentHeaderVo.getMaxPage();
        String targetUrlTemplate = sleepCommentHeaderVo.getTargetUrlTemplate();
        List<SleepCommentHeaderDto> sleepCommentHeaderDtoList = new ArrayList<>();
        int count = 0;
        String source = sleepCommentHeaderVo.getSource();
        for (int i = 1; i <= maxPage; i++) {
            String targetUrl = "";
            if (i == 1) {
                targetUrl = targetUrlTemplate;
            } else {
                targetUrl = targetUrlTemplate + "&page=" + i;
            }
            Connection connect = Jsoup.connect(targetUrl)
//                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890)))
                    .timeout(120000).maxBodySize(0);
            connect.header("Accept-Encoding", "gzip, deflate, br");
            connect.header("Cookie", "TP.uuid=e570b4eb-4498-4a64-9006-525206942b86; ajs_anonymous_id=000da306-89e2-46fb-853a-bcf5efaee7f4; _ga=GA1.1.1252899102.1774231486; OptanonAlertBoxClosed=2026-03-23T02:04:49.041Z; _hjSessionUser_391767=eyJpZCI6ImIwMWFjN2ZmLTQ5NTYtNTY5OC05YmU5LWE0NWM4NmQ5OTU4NCIsImNyZWF0ZWQiOjE3NzQyMzE0ODI5NjEsImV4aXN0aW5nIjp0cnVlfQ==; _gcl_au=1.1.880488244.1774231486.381848436.1774234887.1774234928; tp-b2b-is-sso-saml-authenticated=false; tp-b2b-sso-saml-authenticated-business-account-id=; tp-b2b-sso-saml-recently-authenticated=false; analytics_session_id=1774235007262; _hjSessionUser_402766=eyJpZCI6ImFmNDkyZTI0LWE0NGQtNWY5MS04YjA1LWVjMmIxMjZjN2ZiMSIsImNyZWF0ZWQiOjE3NzQyMzUwMDc2MzAsImV4aXN0aW5nIjp0cnVlfQ==; analytics_session_id.last_access=1774235270198; amplitude_id_de1e2fc13cf22ef1024015ecc1bb8ccdtrustpilot.com=eyJkZXZpY2VJZCI6IjY3ZGM4Yjc1LTkxMjUtNGRlNy1hNzNiLTBiZTk4MTBkY2Q0ZlIiLCJ1c2VySWQiOm51bGwsIm9wdE91dCI6ZmFsc2UsInNlc3Npb25JZCI6MTc3NDIzNTAwNzI4MiwibGFzdEV2ZW50VGltZSI6MTc3NDIzNTI3MDIwNywiZXZlbnRJZCI6MTAsImlkZW50aWZ5SWQiOjMsInNlcXVlbmNlTnVtYmVyIjoxM30=; g_state={\"i_l\":0,\"i_ll\":1774235279782,\"i_e\":{\"enable_itp_optimization\":0}}; tp-consumer-id=69c0ae9607ee8af511fd5060; jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb25zdW1lcklkIjoiNjljMGFlOTYwN2VlOGFmNTExZmQ1MDYwIiwiaGFzQWNjZXB0ZWRUZXJtcyI6dHJ1ZSwiaXNCbG9ja2VkRm9yUmVwb3J0aW5nIjpmYWxzZSwiYWNjZXNzVG9rZW4iOiJ3RTlMV25zNThpaEtKTGxGZ0F0RHJiNTREVjlvIiwiYXV0aGVudGljYXRpb25Tb3VyY2UiOiJnb29nbGUiLCJpYXQiOjE3NzQyMzUyODksImV4cCI6MTc4MjAxMTI4OX0.xk23P7DjYwvHv09tyipK-AYbrQZ740-DfPiTlBN2VvY; ajs_user_id=9587c5cadd363eabd90cb57ffc4208078da21147; _hjSession_391767=eyJpZCI6IjBkNjNjODYxLTFjOTEtNDIwNS1hYTg0LTAwZjg5NWJlYTI4OCIsImMiOjE3NzQzMTk4MTgzMjksInMiOjAsInIiOjAsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjowLCJzcCI6MH0=; amplitude_idundefinedtrustpilot.com=eyJvcHRPdXQiOmZhbHNlLCJzZXNzaW9uSWQiOm51bGwsImxhc3RFdmVudFRpbWUiOm51bGwsImV2ZW50SWQiOjAsImlkZW50aWZ5SWQiOjAsInNlcXVlbmNlTnVtYmVyIjowfQ==; _hjHasCachedUserAttributes=true; aws-waf-token=b632c024-342e-4180-a59e-8b6ed8df1557:AAoAbSYR4ogPAAAA:NPOkZ6nWTStBgt/uD7PlUy+nyuO4PTINwe1/0IHs4N7kIvU/UICV3+ZbZ7/tm6/CX5eoSJwYnAfZOes9/Wn6xgJEi8UYUDFMS5mTAYYt5jrko7r9/MC8F6lteGj5T65tLeAsJ2+ruHU9D9DeEaP1DQ6JXFkEdkcmiypN5Hc6OeZhdTJXw4ohkseBmo7hBh6ae67HzXsX56dqHHqH4zKv+q5vVRukOLb/mcrXKgHVfwcY3hx4tWJjmw5jIrf3djT/WCxtP6a55VLy; amplitude_id_67f7b7e6c8cb1b558b0c5bda2f747b07trustpilot.com=eyJkZXZpY2VJZCI6IjAwMGRhMzA2LTg5ZTItNDZmYi04NTNhLWJjZjVlZmFlZTdmNCIsInVzZXJJZCI6Ijk1ODdjNWNhZGQzNjNlYWJkOTBjYjU3ZmZjNDIwODA3OGRhMjExNDciLCJvcHRPdXQiOmZhbHNlLCJzZXNzaW9uSWQiOjE3NzQzMTk4MjA1NzcsImxhc3RFdmVudFRpbWUiOjE3NzQzMTk4Mjc5NDIsImV2ZW50SWQiOjExNywiaWRlbnRpZnlJZCI6OSwic2VxdWVuY2VOdW1iZXIiOjEyNn0=; _ga_11HBWMC274=GS2.1.s1774319821$o9$g0$t1774319829$j52$l0$h0; OptanonConsent=isGpcEnabled=0&datestamp=Tue+Mar+24+2026+10%3A37%3A10+GMT%2B0800+(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)&version=202602.1.0&browserGpcFlag=0&isIABGlobal=false&hosts=&consentId=edf4c6b2-a961-4d80-a42d-947aff8da6a5&interactionCount=1&isAnonUser=1&prevHadToken=0&landingPath=NotLandingPage&groups=C0001%3A1%2CC0002%3A1%2CC0003%3A1%2CC0004%3A1&intType=1&crTime=1774231489853&geolocation=CN%3BGD&AwaitingReconsent=false");
            connect.header("Referer", targetUrlTemplate);
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
                        dto.setSource(source);
                        dto.setContentType("Comment");
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
                        dto.setUuid(UUID.randomUUID().toString());
                        dto.setPage(i);
                        sleepCommentHeaderDtoList.add(dto);
                        Element replyElement = element.selectFirst("div.CDS_Card_card__146e7a.CDS_Card_borderRadius-m__146e7a.styles_wrapper__WD_1K > div.styles_content__eJmhl");
                        if (replyElement != null) {
                            SleepCommentHeaderDto sleepCommentHeaderDto = handleReply(element, dto, sdf);
                            sleepCommentHeaderDtoList.add(sleepCommentHeaderDto);
                            count++;
                        }
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

    private SleepCommentHeaderDto handleReply(Element element, SleepCommentHeaderDto dto, SimpleDateFormat sdf) {
        SleepCommentHeaderDto sleepCommentHeaderDto = new SleepCommentHeaderDto();
        sleepCommentHeaderDto.setPage(dto.getPage());
        sleepCommentHeaderDto.setSource(dto.getSource());
        int floor = Integer.parseInt(Convert.toStr(dto.getFloor(), "1"));
        sleepCommentHeaderDto.setFloor(String.valueOf(floor + 1));

        Element replyElement = element.selectFirst("div.CDS_Card_card__146e7a.CDS_Card_borderRadius-m__146e7a.styles_wrapper__WD_1K > div.styles_content__eJmhl > div.styles_replyHeader__zKV_w > div.styles_replyInfo__41_in > p");
        if (replyElement != null) {
            String reply = replyElement.text();
            sleepCommentHeaderDto.setUserName(reply);
            sleepCommentHeaderDto.setContentType("Reply");
        }
        Element replyTimeElement = element.selectFirst("div.CDS_Card_card__146e7a.CDS_Card_borderRadius-m__146e7a.styles_wrapper__WD_1K > div.styles_content__eJmhl > div.styles_replyHeader__zKV_w > div.styles_replyInfo__41_in > time");
        if (replyTimeElement != null) {
            String replyTime = replyTimeElement.attr("datetime");
            try {
                sleepCommentHeaderDto.setOperateTime(sdf.parse(replyTime));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        Element replyContentElement = element.selectFirst("div.CDS_Card_card__146e7a.CDS_Card_borderRadius-m__146e7a.styles_wrapper__WD_1K > div.styles_content__eJmhl > p");
        if (replyContentElement != null) {
            String replyContent = replyContentElement.text();
            sleepCommentHeaderDto.setContent(replyContent);
        }
        sleepCommentHeaderDto.setUuid(UUID.randomUUID().toString());
        sleepCommentHeaderDto.setParentId(dto.getUuid());
        return sleepCommentHeaderDto;
    }

    @Override
    public void handleReddit(String key) {

    }

    @Override
    public void handle3B(String key) {

    }

    @Override
    public void exportTrustPilot(SleepCommentHeaderVo sleepCommentHeaderVo) {
        String exportPath = "C:\\Mine\\文本文件\\Sleep.xlsx";
        try (ExcelWriter excelWriter = EasyExcel.write(exportPath)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .excelType(ExcelTypeEnum.XLSX)
                .build()) {
            List<String> sourceList = sleepCommentHeaderVo.getSourceList();
            SleepCommentHeaderDto dto = new SleepCommentHeaderDto();
            dto.setSourceList(sourceList);
            List<SleepCommentHeaderDto> list = findList(dto);
            if (UEmpty.isNotEmpty(list)) {
                List<ExportSleepEntity> sleepEntityList = UCopy.fullCopyList(list, ExportSleepEntity.class);
                Map<String, List<ExportSleepEntity>> groupingBySourceMapList = sleepEntityList.stream().collect(Collectors.groupingBy(ExportSleepEntity::getSource));
                Set<Map.Entry<String, List<ExportSleepEntity>>> entrySet = groupingBySourceMapList.entrySet();
                for (Map.Entry<String, List<ExportSleepEntity>> entry : entrySet) {
                    String source = entry.getKey();
                    List<ExportSleepEntity> value = entry.getValue();
                    WriteSheet writeSheet = EasyExcel.writerSheet(source)
                            .head(ExportSleepEntity.class)
                            .build();
                    excelWriter.write(value, writeSheet);
                }
            }
            excelWriter.finish();
        }
    }
}
