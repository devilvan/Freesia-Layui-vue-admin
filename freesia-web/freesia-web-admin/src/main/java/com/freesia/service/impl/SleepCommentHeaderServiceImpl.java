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
        return sleepCommentHeaderConverter.convertBatchPo2Dto(sleepCommentHeaderMapper.findList(dto));
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
        for (int i = 11; i <= maxPage; i++) {
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
            connect.header("Cookie", "TP.uuid=47d081bd-ae20-4d12-82f3-0e1ec6fd5a61; _hjSession_391767=eyJpZCI6ImIyYWZmNDY3LTUzMmUtNGQxZi05Y2RiLTBkYzM0YTI0NmExYyIsImMiOjE3NzQzNTYyOTU3NzEsInMiOjAsInIiOjAsInNiIjowLCJzciI6MCwic2UiOjAsImZzIjoxLCJzcCI6MH0=; ajs_anonymous_id=b1e369a7-99ef-4253-8ca2-3f5e4bb6cc69; amplitude_idundefinedtrustpilot.com=eyJvcHRPdXQiOmZhbHNlLCJzZXNzaW9uSWQiOm51bGwsImxhc3RFdmVudFRpbWUiOm51bGwsImV2ZW50SWQiOjAsImlkZW50aWZ5SWQiOjAsInNlcXVlbmNlTnVtYmVyIjowfQ==; _gcl_au=1.1.1608621595.1774356296; _hjHasCachedUserAttributes=true; _ga=GA1.1.1323577505.1774356297; OptanonAlertBoxClosed=2026-03-24T12:44:58.921Z; _hjSessionUser_391767=eyJpZCI6Ijc5M2E1ZjY2LTg4YzktNTI4MC1hNjA5LWE2MmU4MDQ3YjVhMSIsImNyZWF0ZWQiOjE3NzQzNTYyOTU3NzAsImV4aXN0aW5nIjp0cnVlfQ==; tp-b2b-is-sso-saml-authenticated=false; tp-b2b-sso-saml-authenticated-business-account-id=; tp-b2b-sso-saml-recently-authenticated=false; g_state={\"i_l\":0,\"i_ll\":1774356403011,\"i_e\":{\"enable_itp_optimization\":0}}; tp-consumer-id=69c0ae9607ee8af511fd5060; jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb25zdW1lcklkIjoiNjljMGFlOTYwN2VlOGFmNTExZmQ1MDYwIiwiaGFzQWNjZXB0ZWRUZXJtcyI6dHJ1ZSwiaXNCbG9ja2VkRm9yUmVwb3J0aW5nIjpmYWxzZSwiYWNjZXNzVG9rZW4iOiJKOFRtcG4zRXVaUmhHOUNnRWI5MWNYcVN5UW1CIiwiYXV0aGVudGljYXRpb25Tb3VyY2UiOiJnb29nbGUiLCJpYXQiOjE3NzQzNTY0NTAsImV4cCI6MTc4MjEzMjQ1MH0.xBmMaMOaD8U2ZJMZMV22xtkKVkHKODhW62XDUazb-jA; ajs_user_id=9587c5cadd363eabd90cb57ffc4208078da21147; amplitude_id_67f7b7e6c8cb1b558b0c5bda2f747b07trustpilot.com=eyJkZXZpY2VJZCI6ImIxZTM2OWE3LTk5ZWYtNDI1My04Y2EyLTNmNWU0YmI2Y2M2OSIsInVzZXJJZCI6Ijk1ODdjNWNhZGQzNjNlYWJkOTBjYjU3ZmZjNDIwODA3OGRhMjExNDciLCJvcHRPdXQiOmZhbHNlLCJzZXNzaW9uSWQiOjE3NzQzNTYyOTYxNDgsImxhc3RFdmVudFRpbWUiOjE3NzQzNTY0NTM5NDIsImV2ZW50SWQiOjE3LCJpZGVudGlmeUlkIjo1LCJzZXF1ZW5jZU51bWJlciI6MjJ9; aws-waf-token=b632c024-342e-4180-a59e-8b6ed8df1557:AAoAiR9YTXAEAAAA:feSLmz1DSnnfI5fyH/AyDLUp4rMs/9AK3gMpdyE+wp7UEDu4O1judb9rcfVFneO84Al3U0F59TEzlWNAisYWLfOjh9ODPY5y2usBAtjTjGv6ES9a+JCQ/QPF0+ZO92Y1uklHG7mLbHpZfbYoFYglpAUWl/RD5o3fqBxds1TrFhSYuVSAaW2iXeW9jpUDsMRSABn3bTaQqVs1h2CUELqGsCD6NvwaeUVMBE2VEtMqB21VHN7cbJude2o/9YOaIb9ZWxoNnTMZ+wQB; _ga_11HBWMC274=GS2.1.s1774356296$o1$g1$t1774356467$j60$l0$h0; OptanonConsent=isGpcEnabled=0&datestamp=Tue+Mar+24+2026+20%3A47%3A47+GMT%2B0800+(%E4%B8%AD%E5%9B%BD%E6%A0%87%E5%87%86%E6%97%B6%E9%97%B4)&version=202602.1.0&browserGpcFlag=0&isIABGlobal=false&hosts=&consentId=6613fd70-dc5f-4541-b6ea-59e561ba5bb6&interactionCount=1&isAnonUser=1&prevHadToken=0&landingPath=NotLandingPage&groups=C0001%3A1%2CC0002%3A1%2CC0003%3A1%2CC0004%3A1&intType=1&geolocation=CN%3BGD&AwaitingReconsent=false");
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
                            SleepCommentHeaderDto sleepCommentHeaderDto = handleTrustPilotReply(element, dto, sdf);
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

    @Override
    public void handleReddit(SleepCommentHeaderVo sleepCommentHeaderVo) {

    }

    @Override
    public void handle3B(SleepCommentHeaderVo sleepCommentHeaderVo, Map<String, String> headersAsMap) {
        String targetUrl = "";
        String source = sleepCommentHeaderVo.getSource();
        int startPage = Convert.toInt(sleepCommentHeaderVo.getStartPage(), 1);
        int maxPage = Convert.toInt(sleepCommentHeaderVo.getMaxPage(), 1);
        String targetUrlTemplate = sleepCommentHeaderVo.getTargetUrlTemplate();
        for (int i = startPage; i <= maxPage; i++) {
            List<SleepCommentHeaderDto> sleepCommentHeaderDtoList = new ArrayList<>();
            targetUrl = targetUrlTemplate + "?page=" + i;
            Connection connect = Jsoup.connect(targetUrl)
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890)))
                    .timeout(60000)
                    .maxBodySize(0);
            connect.headers(headersAsMap);
            connect.header("Referer", targetUrl);
            Element body = null;
            try {
                connect.ignoreContentType(true);
                body = connect.get().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            int count = 0;
            if (body != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                Elements elements = body.select("li.card.bpr-review.stack.dtm-review");
                if (UEmpty.isNotEmpty(elements)) {
                    for (Element element : elements) {
                        SleepCommentHeaderDto sleepCommentHeaderDto = new SleepCommentHeaderDto();
                        sleepCommentHeaderDto.setSource(source);
                        sleepCommentHeaderDto.setContentType("COMMENT");
                        sleepCommentHeaderDto.setUuid(UUID.randomUUID().toString());
                        sleepCommentHeaderDto.setPage(i);
                        sleepCommentHeaderDto.setUrl(targetUrl);
                        sleepCommentHeaderDto.setDomain("BBB");
                        sleepCommentHeaderDto.setFloor(String.valueOf(1));

                        String username = element.selectFirst("h3 > span").text();
                        if (UEmpty.isNotEmpty(username)) {
                            username = username.replaceAll("Review from", "");
                            sleepCommentHeaderDto.setUserName(username);
                            System.out.println(username);
                        }
                        Element operateTimeElement = element.selectFirst("p.bds-body");
                        if (operateTimeElement != null) {
                            String operateTimeStr = operateTimeElement.text();
                            operateTimeStr = operateTimeStr.replaceAll("Date: ", "");
                            try {
                                sleepCommentHeaderDto.setOperateTime(sdf.parse(operateTimeStr));
                                System.out.println(operateTimeStr);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        Element contentElement = element.selectFirst("div:nth-child(4)");
                        if (contentElement != null) {
                            String content = contentElement.text();
                            sleepCommentHeaderDto.setContent(content);
                            System.out.println(content);
                        }
                        Element levelElement = element.selectFirst("div:nth-child(3) > span");
                        if (levelElement != null) {
                            String level = levelElement.text();
                            level = level.substring(0, 1);
                            sleepCommentHeaderDto.setLevel(level);
                            System.out.println(level);
                        }
                        sleepCommentHeaderDtoList.add(sleepCommentHeaderDto);
                        // 评论
                        Elements subReplyElementList = element.select("div.bpr-review-business-response-grid,div.bpr-review-customer-response-grid");

                        if (UEmpty.isNotEmpty(subReplyElementList)) {
                            // 用于存储上一个business回复的UUID
                            String lastBusinessUuid = null;

                            for (int j = 0; j < subReplyElementList.size(); j++) {
                                Element subReplyElement = subReplyElementList.get(j);

                                // 判断当前元素是business回复还是customer回复
                                boolean isBusinessReply = subReplyElement.hasClass("bpr-review-business-response-grid");
                                boolean isCustomerReply = subReplyElement.hasClass("bpr-review-customer-response-grid");

                                SleepCommentHeaderDto replyDto = new SleepCommentHeaderDto();
                                replyDto.setFloor(String.valueOf(j + 2));
                                replyDto.setUrl(sleepCommentHeaderDto.getUrl());
                                replyDto.setPage(sleepCommentHeaderDto.getPage());
                                replyDto.setUuid(UUID.randomUUID().toString());
                                replyDto.setDomain(sleepCommentHeaderDto.getDomain());
                                replyDto.setSource(source);

                                // 设置内容类型
                                if (isBusinessReply) {
                                    replyDto.setContentType("REPLY");
                                } else if (isCustomerReply) {
                                    replyDto.setContentType("REPLY"); // 或者你需要的其他类型
                                }

                                Element replyTitleElement = subReplyElement.selectFirst("h4.bpr-review-business-response-title,p.bpr-review-customer-response-heading");
                                if (replyTitleElement != null) {
                                    String replyTitle = replyTitleElement.text();
                                    replyDto.setUserName(replyTitle);
                                    System.out.println(replyTitle);
                                }

                                Element replyTimeElement = subReplyElement.selectFirst("p.bpr-review-business-response-date,p.bpr-review-customer-response-date");
                                if (replyTimeElement != null) {
                                    String replyTimeStr = replyTimeElement.text();
                                    replyTimeStr = replyTimeStr.replaceAll("Date: ", "");
                                    try {
                                        replyDto.setOperateTime(sdf.parse(replyTimeStr));
                                        System.out.println(replyTimeStr);
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                Element replyContentElement = subReplyElement.selectFirst("div.bpr-review-business-response-body,div.bpr-review-customer-response-body");
                                if (replyContentElement != null) {
                                    String replyStr = replyContentElement.text();
                                    replyDto.setContent(replyStr);
                                    System.out.println(replyStr);
                                }

                                replyDto.setFloor(String.valueOf(j + 2));

                                // 设置parentId的逻辑
                                if (isBusinessReply) {
                                    // 如果是business回复，先设置parentId为原始评论的UUID
                                    replyDto.setParentId(sleepCommentHeaderDto.getUuid());
                                    // 记录当前business回复的UUID，供后续customer回复使用
                                    lastBusinessUuid = replyDto.getUuid();
                                } else if (isCustomerReply) {
                                    // 如果是customer回复，检查前一个元素是否为business回复
                                    // 通过检查j > 0且前一个元素是business回复来判断
                                    if (j > 0) {
                                        Element previousElement = subReplyElementList.get(j - 1);
                                        if (previousElement.hasClass("bpr-review-business-response-grid")) {
                                            // 如果前一个元素是business回复，使用lastBusinessUuid
                                            replyDto.setParentId(lastBusinessUuid);
                                        } else {
                                            // 否则使用原始评论的UUID
                                            replyDto.setParentId(sleepCommentHeaderDto.getUuid());
                                        }
                                    } else {
                                        // 第一个元素就是customer回复，使用原始评论的UUID
                                        replyDto.setParentId(sleepCommentHeaderDto.getUuid());
                                    }
                                }

                                sleepCommentHeaderDtoList.add(replyDto);
                                count++;

                                // 如果不是business回复，重置lastBusinessUuid（可选）
                                // 根据业务需求决定是否需要重置
                                if (!isBusinessReply) {
                                    // lastBusinessUuid = null; // 如果不需要跨多个customer回复关联，可以取消注释
                                }
                            }
                        }
                        count++;
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
    }

    private SleepCommentHeaderDto handleTrustPilotReply(Element element, SleepCommentHeaderDto dto, SimpleDateFormat sdf) {
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
                // 2. 按 id 分组，建立快速索引 Map
                Map<String, SleepCommentHeaderDto> entityMap = list.stream()
                        .collect(Collectors.toMap(SleepCommentHeaderDto::getUuid, item -> item));
                // 3. 用于存储最终结果的列表 (顶级节点)
                List<SleepCommentHeaderDto> resultList = new ArrayList<>();
                // 4. 遍历所有节点，构建父子关系
                for (SleepCommentHeaderDto node : list) {
                    String parentId = node.getParentId();

                    // 关键判断：如果 parentId 为 null 或空，或者父节点不存在，则视为顶级节点
                    if (parentId == null || parentId.isEmpty() || !entityMap.containsKey(parentId)) {
                        resultList.add(node);
                    } else {
                        // 找到父节点，将当前节点添加到父节点的 children 中
                        SleepCommentHeaderDto parent = entityMap.get(parentId);
                        if (parent.getChildren() == null) {
                            parent.setChildren(new ArrayList<>());
                        }
                        parent.getChildren().add(node);
                    }
                }
                List<ExportSleepEntity> sleepEntityList = buildExportSleepEntity(resultList);
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

    @Override
    public void export3B(SleepCommentHeaderVo sleepCommentHeaderVo) {
        String exportPath = "C:\\Mine\\文本文件\\Sleep_BBB.xlsx";
        try (ExcelWriter excelWriter = EasyExcel.write(exportPath)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .excelType(ExcelTypeEnum.XLSX)
                .build()) {
            List<String> sourceList = sleepCommentHeaderVo.getSourceList();
            SleepCommentHeaderDto dto = new SleepCommentHeaderDto();
            dto.setSourceList(sourceList);
            dto.setDomain(sleepCommentHeaderVo.getDomain());
            List<SleepCommentHeaderDto> list = findList(dto);
            if (UEmpty.isNotEmpty(list)) {
                // 2. 按 id 分组，建立快速索引 Map
                Map<String, SleepCommentHeaderDto> entityMap = list.stream()
                        .collect(Collectors.toMap(SleepCommentHeaderDto::getUuid, item -> item));
                // 3. 用于存储最终结果的列表 (顶级节点)
                List<SleepCommentHeaderDto> resultList = new ArrayList<>();
                // 4. 遍历所有节点，构建父子关系
                for (SleepCommentHeaderDto node : list) {
                    String parentId = node.getParentId();

                    // 关键判断：如果 parentId 为 null 或空，或者父节点不存在，则视为顶级节点
                    if (parentId == null || parentId.isEmpty() || !entityMap.containsKey(parentId)) {
                        resultList.add(node);
                    } else {
                        // 找到父节点，将当前节点添加到父节点的 children 中
                        SleepCommentHeaderDto parent = entityMap.get(parentId);
                        if (parent.getChildren() == null) {
                            parent.setChildren(new ArrayList<>());
                        }
                        parent.getChildren().add(node);
                    }
                }
                List<ExportSleepEntity> sleepEntityList = buildExportSleepEntity(resultList);
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

    private List<ExportSleepEntity> buildExportSleepEntity(List<SleepCommentHeaderDto> resultList) {
        List<ExportSleepEntity> exportSleepEntityList = new ArrayList<>();
        for (SleepCommentHeaderDto sleepCommentHeaderDto : resultList) {
            List<SleepCommentHeaderDto> children = sleepCommentHeaderDto.getChildren();
            ExportSleepEntity exportSleepEntity = buildExportSleepEntity(sleepCommentHeaderDto);
            exportSleepEntityList.add(exportSleepEntity);
            if (UEmpty.isNotEmpty(children)) {
                exportSleepEntityList.addAll(buildExportSleepEntity(children));
            }
        }
        return exportSleepEntityList;
    }

    private static ExportSleepEntity buildExportSleepEntity(SleepCommentHeaderDto sleepCommentHeaderDto) {
        ExportSleepEntity exportSleepEntity = new ExportSleepEntity();
        exportSleepEntity.setSource(sleepCommentHeaderDto.getSource());
        exportSleepEntity.setUserId(sleepCommentHeaderDto.getUserId());
        exportSleepEntity.setUserName(sleepCommentHeaderDto.getUserName());
        exportSleepEntity.setTitle(sleepCommentHeaderDto.getTitle());
        String contentType = sleepCommentHeaderDto.getContentType();
        if (contentType.equalsIgnoreCase("COMMENT")) {
            exportSleepEntity.setContentType("评论");
        } else if (contentType.equalsIgnoreCase("REPLY")) {
            exportSleepEntity.setContentType("回复");
        }
        exportSleepEntity.setContent(sleepCommentHeaderDto.getContent());
        exportSleepEntity.setFloor(sleepCommentHeaderDto.getFloor());
        exportSleepEntity.setLevel(sleepCommentHeaderDto.getLevel());
        exportSleepEntity.setOperateTime(sleepCommentHeaderDto.getOperateTime());
        exportSleepEntity.setUrl(sleepCommentHeaderDto.getUrl());
        exportSleepEntity.setPage(sleepCommentHeaderDto.getPage());
        return exportSleepEntity;
    }
}
