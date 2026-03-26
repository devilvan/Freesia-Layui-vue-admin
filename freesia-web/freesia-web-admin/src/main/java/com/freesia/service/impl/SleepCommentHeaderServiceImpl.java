package com.freesia.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
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
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
    public void handle3B(SleepCommentHeaderVo sleepCommentHeaderVo) {
        String targetUrl = "";
        String source = sleepCommentHeaderVo.getSource();
        int maxPage = Convert.toInt(sleepCommentHeaderVo.getMaxPage(), 1);
        String targetUrlTemplate = sleepCommentHeaderVo.getTargetUrlTemplate();
        for (int i = 1; i <= maxPage; i++) {
            targetUrl = targetUrlTemplate + "?page=" + i;
            Connection connect = Jsoup.connect(targetUrl)
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 7890)))
                    .timeout(60000)
                    .maxBodySize(0);
//            connect.header("Accept", "*/*");
            connect.header("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
            connect.header("accept-encoding", "gzip, deflate");
            connect.header("Cookie", "iabbb_user_culture=en-us; iabbb_user_location=Cherryland_CA_USA; iabbb_user_bbb=1116; iabbb_find_location=Cherryland_CA_USA; _ga=GA1.1.1677767890.1774233284; _evga_f108={%22uuid%22:%22487f845cb1f47503%22}; _fbp=fb.1.1774233284428.417049412401313480; _sfid_03fc={%22anonymousId%22:%22487f845cb1f47503%22%2C%22consents%22:[]}; iabbb_user_postalcode=94541; iabbb_cookies_preferences_set=true; iabbb_cookies_policy=%7B%22necessary%22%3Atrue%2C%22functional%22%3Atrue%2C%22performance%22%3Atrue%2C%22marketing%22%3Atrue%7D; _gcl_au=1.1.272628837.1774233283.415972280.1774247063.1774247063; iabbb_auth_refresh_token=eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAifQ.ZVTHTWKOu792dNSSpxqKpvd_62xH15eXwgXn1l52sBGjqY8rI0ApBSeSzmIx4C0hu6t6hftBsjTDRrGX4Hhh-Rqq3qcRbm43oAQt-TybxaSBaM_R7Fmp_o67xKnsZIb9DasVIq1ZgnhCaRG0IKmxO4PyrmHVHD1t6zJBJQESfbpNoXwMeIolrsHOJ7JHnmzCSov-XoisMt5mEoyoSO6L58_VhKKk0EQ61yp9gPOPhcNu39ojAOuVS-VrzxB1x7QUzYBre-ytZC5lhfEZk6u9zc1AWjB41qhSmCub-KjnU5J6C6Ma5SmbXqc3ZsJ3XII5Y4wqWGsrX0H1i0hwUVXf9g.dYV3ibseYsXLN6xj.5sS-Y0_OJWRYM3R3szFO4jncknVcbLhDdOo_4sueykY2_s6Xv1I7p_AaCbXCxGLin2NYm-apoU9YtVqJrtcdaW9Ct2zjxLXNzUDQaPRlty7yZjlsAG6dFddoi-ed9Vgk-R2XSSUgdNDoojy7rB9iEWBA0D522nD_RD7o6fW3OBsvpqK2zhT6Eyx9LalfTaJAossdmk910vqTNEv755fTXZn1LrSa5Y5yXj0iVHjN7_JZJWXtfZmBLnVQcjhCpIc9moS3x7L47OwZpJRFKZsV8XQNPs4-Vtq2rhxpoiM6PYLG_23MgFlKfCX4zBKZMpcn-AT9quCNsZk0Nb1qYE2puhJlU9520dULN6CdxWiZ_XfW1hcqcIoyzSfPxjp2QH6VDUZGvWimywM0g7t_cxsw4y1eV-ptBLD5iGVfHN1ofor-h6zONzJ9XqYF5BXv2ctXk-SqBfZMORiy2rMRTOohPfLSMvmqEfn_Y7P9IRzU3PIy0qT40B1ODkwIBUPp_9ic_JxoaDt0iVnVZmMflQApCekjuYaovx9YCXvsnW9Vhh0MYN4CdZ4gWrZ0R9qSfEbBQBxVX4wjiMrqRtvSlV6jXcTUI_Dh_f__Yy1RIZMwAp7F6rnoFBKgemHor-Z5qhBT-CRp4nRTR_eONQM4HhTbI0Uvr2rw4cJ7-DBKR8hdxbcvzbNI_Kc3D_EiXcFn7sRy686TAdsiE4s3duTtWz7z2tQ4HyNpkPNHmjvmvM70vAHoqoO9UEf_FEsOUUf3qYVu51dcabwwtFD1MWHagQ7s-fHoCH0t8VooEL4B54Yj4QFlQcWiZBb9nHpDjGoH0zq1uLnbUR_2edqqFpL5ZQ-QUIIVjqTUTJWDdzIvseY0uqlQt18haFyaIcWWRD--l5tXga787BaJ1gJZHIoEnu6cHse_KFQpqG8F-4qL_1oVDCOw7KMshiO2_TnaUYU-9TH1Eim2Hk0qhPW7bCjgrQR3uA0hQ57nAXl13GCpuSDlqWe-Ogu2Q5h7_ANHs8uf2I0Ffv0sUKFuFWPKGjbmH4xzQvT_ykTfviFjA_ireXeD7SjjPpdUgmqkPGCc1jxDE-GsTTSBc0uX-sSChzk_ZBS3VyceY8vJNMb6bgfTa1bTQVRHd7tFvz7XHdPJkL2s0P-q---Pqk7fI7KE5eCXfHPCmKuO1JUK6qqQlWYiFj_iK356cae6S9uLBoVgchmy692NeCP27WzzGybVh00nqWgy2ZuJBlzEB12Dr7dUZQ.qS4EUkUGajZBO0F8YMAf7A; iabbb_session_id=0c684374-933d-4638-93ba-d4fc347436e8; iabbb_auth_id_token=eyJraWQiOiI5YTg2VE1wS0xJK2kzRUN3ajFSbVhaRFNBSmRYXC9ldGUzME5RWWJKR0hKWT0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJiNDA4OTRiOC0wMGQxLTcwNmUtNGUxNi01MDNhYzQ5Y2U1M2YiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMS5hbWF6b25hd3MuY29tXC91cy1lYXN0LTFfWkNpMGMxMTNXIiwiY29nbml0bzp1c2VybmFtZSI6ImZiNWIzNzM2LTY1MTUtNDgyOC1hZDQ5LWJmOTU3MGNlMTI5MCIsInByZWZlcnJlZF91c2VybmFtZSI6ImRldmlsdmFuZXZhZEBvdXRsb29rLmNvbSIsImdpdmVuX25hbWUiOiJFdmFkIiwib3JpZ2luX2p0aSI6IjhiNjcwMGJmLTMyNTgtNDk2MC04NWZiLTg2MWEwYjdiOGVjOSIsImF1ZCI6IjdxbDAxczRiZ2ZqNmxobW5zYWVrc2J1YWY2IiwiaWRlbnRpdGllcyI6W3siZGF0ZUNyZWF0ZWQiOiIxNzc0MjQ3MDU3NzcxIiwidXNlcklkIjoiQUFBQUFBQUFBQUFBQUFBQUFBQUFBTlNrOHZtSURHcUZDLWpReVZlcUVMUSIsInByb3ZpZGVyTmFtZSI6Ik1pY3Jvc29mdCIsInByb3ZpZGVyVHlwZSI6Ik9JREMiLCJpc3N1ZXIiOm51bGwsInByaW1hcnkiOiJmYWxzZSJ9XSwidG9rZW5fdXNlIjoiaWQiLCJhdXRoX3RpbWUiOjE3NzQyNDcwNTgsImN1c3RvbTp1c2VyYXR0cnMiOiJ7XCJsZ0F0XCI6MCxcInJlZ0NkVmZBdFwiOjAsXCJsZ0NkVmZBdFwiOjAsXCJwd3JzQ2RWZkF0XCI6MCxcInNlbmRQcmVmXCI6XCJlbWFpbFwiLFwibGVnYWN5U3ViXCI6XCJcIixcInJlc2V0XCI6XCJ0cnVlXCIsXCJsb2dpbk1ldGhvZFwiOm51bGx9IiwiZXhwIjoxNzc0NTE5NDkyLCJpYXQiOjE3NzQ1MTU4OTIsImZhbWlseV9uYW1lIjoiRGV2aWx2YW4iLCJqdGkiOiJkZDBlNzdlYS1lOTkzLTQyMzItODhhNi0zMzdhYTNlZWFiNmYiLCJlbWFpbCI6ImRldmlsdmFuZXZhZEBvdXRsb29rLmNvbSJ9.MUnwokfxFvKgFW5jS1blcQ9_VInM-aUww9pi-Pks94r4FA_Lvy3u0tT63XNa08Lh8cHpFPKjPfwHpfaIKUEvZTc6Tb2_7n4W4hAEGWJuqEsgQNrEYlcFbREuKa1wyFNlXW8Xtoz-_BTsLQac5jqFChuPBiew2AzrrbzOAzQYz4SMdqnLp5mwPvjrQNs7Qi-1j1-MEW1CBNiqoAPNQ0ZnVfEOCUOyjCs3rCSfHzVpP8Ly0Kt3QBTWEoaGQ62nUxHl2v7XpokdJzyMG0QHuuG66rmjrTwrdg7mRqb3-obWze-bK0GBPLepyl9ZKGTpMd6PWkDSkqYHTRCx2y8ynbJWSA; AMCVS_CB586B8557EA40917F000101%40AdobeOrg=1; AMCV_CB586B8557EA40917F000101%40AdobeOrg=179643557%7CMCIDTS%7C20538%7CMCMID%7C31995776247252270270214319852973150098%7CMCAAMLH-1775120693%7C3%7CMCAAMB-1775120693%7C6G1ynYcLPuiQxYZrsz_pkqfLG9yMXBpb2zX5dvJdYQJzPXImdj0y%7CMCOPTOUT-1774523093s%7CNONE%7CMCAID%7CNONE%7CMCSYNCSOP%7C411-20543%7CvVersion%7C5.5.0; s_cc=true; cf_clearance=L8OVk5blgrzbe8rHQauA9gEC.0SrKKm0GURbP4R6tS8-1774516767-1.2.1.1-ZVr.0m5h4iC4GqpvVCI1G8fovioADOaDI7d8yb66z_waUqD9UdyfBNniLqYUORfYLzKQ2ZkdZiJBoyAu8q3Rn7j0ocRzO_VduV1Vb3XcfZCd_L8ChLG1NW2hb5K9Y4eKiKdxCDhCNxbnVP9z21coREjJuH_qNfn5dA9wDzOCle7Ltw144pUTGnXNuMaKaXX5kXys1L0xXhcIuqUfKRJzAvOgpRRUU9g1RG5nTMYYXiU; __cf_bm=DBw05UsNnWViAshZciLcCxh6eKPAXI8pRsVcozHJW.c-1774516767.9531803-1.0.1.1-B.GI9gL4HJhCsDi62shO97aasFXUFm4g_n7auSE.9i7U5JxZ.3iNf_zgIoVp7EyI_CIxJUxYNS_QbEVCYD_UR7zWLkhRVuXHDW0DbwoKff0La6i.Cl.MBK3QZzBcFyLB; s_tp=4249; gpv_PageUrl=https%3A%2F%2Fwww.bbb.org%2Fus%2Fny%2Fnew-york%2Fprofile%2Fmattress%2Feight-0121-166228%2Fcustomer-reviews%3Fpage%3D2; s_ips=584; s_ppv=eight%2520%257C%2520bbb%2520reviews%2520%257C%2520better%2520business%2520bureau%2C99%2C14%2C4227%2C8%2C8; s_nr30=1774516912772-Repeat; s_sq=cbbbproduction%3D%2526c.%2526a.%2526activitymap.%2526page%253Deight%252520%25257C%252520bbb%252520reviews%252520%25257C%252520better%252520business%252520bureau%2526link%253DPage%2525201%2526region%253Dcontent%2526pageIDType%253D1%2526.activitymap%2526.a%2526.c%2526pid%253Deight%252520%25257C%252520bbb%252520reviews%252520%25257C%252520better%252520business%252520bureau%2526pidt%253D1%2526oid%253Dhttps%25253A%25252F%25252Fwww.bbb.org%25252Fus%25252Fny%25252Fnew-york%25252Fprofile%25252Fmattress%25252Feight-0121-166228%25252Fcustomer-reviews%25253Fpage%25253D1%2526ot%253DA; CF_Authorization=eyJraWQiOiJkYjM1ZGI4Zjc4MzljNjk2ODE5ZjVhOGE0NWU4N2ZlM2VjNGQwZDFiOTY2YmI4ZDYxYThkMjY4OTAxZDNjYWE5IiwiYWxnIjoiUlMyNTYiLCJ0eXAiOiJKV1QifQ.eyJ0eXBlIjoiYXBwIiwiYXVkIjoiMmNhYzEyYTJmOWY0OTI2YjdmYmY3OTJmNTA5MjA1NjA5YWMwMmIwZmQ0MWQ1ZTcyZjY0YzY5NWY3MDg2ODkzYyIsImV4cCI6MTc3NDYwMzMxMywiaXNzIjoiaHR0cHM6XC9cL2lhYmJiLmNsb3VkZmxhcmVhY2Nlc3MuY29tIiwiY29tbW9uX25hbWUiOiJhNTBlNTEzYjFjNzM0ZjFhMjJmZDJlN2ZkMjI5ZmI4NC5hY2Nlc3MiLCJpYXQiOjE3NzQ1MTY5MTMsInN1YiI6IiJ9.i1jP_wlrJER0eo7NvWvI71L5drJ0Bx8AkQRY3aRyyiUnYNEB7qsA6YVYJUYTgiNVFnIGD06_3Q5hb0ZZz-pFcSmBpiv_zfjzCavvkUUp0Eleskb8EpngO4zg21FtO0F5-beSnAeF0DXlh-HTI_Y5oYsExiNpkdP0z-SXViISLTQYqdb88tm6Oqg2UMX8r79VBmLWp1BaoBvqoYR9SfMlfaDCEvgPYCRtWhPLgF7GJfmeg6pHPBZ51Qf7IMqh3JUD2vsY4t593IpsCMWeHVwXKSQ_FIx3ViGti2p5hz5WmiXTya7YjCyOTrhMFl-JwuZ65dU7hSlSeyUtELJLpExoXA; _ga_MP6NWVNK4P=GS2.1.s1774515893$o1$g1$t1774516913$j60$l0$h0; _ga_PKZXBXGJHK=GS2.1.s1774515905$o6$g1$t1774516913$j56$l0$h0; _ga_QWV3Q1HBDG=GS2.1.s1774515893$o1$g1$t1774516913$j60$l0$h0");
//            connect.header("Origin", "https://www.bbb.org");
            connect.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36 Edg/146.0.0.0");
            connect.header("Referer", targetUrl);
            connect.header("sec-fetch-storage-access", "active");
            Element body = null;
            try {
                connect.ignoreContentType(true);
                body = connect.get().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (body != null) {
                int count = 0;
                List<SleepCommentHeaderDto> sleepCommentHeaderDtoList = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                Elements elements = body.select(".card.bpr-review.stack.dtm-review");
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
                        }
                        Element operateTimeElement = element.selectFirst("p.bds-body");
                        if (operateTimeElement != null) {
                            String operateTimeStr = operateTimeElement.text();
                            operateTimeStr = operateTimeStr.replaceAll("Date: ", "");
                            try {
                                sleepCommentHeaderDto.setOperateTime(sdf.parse(operateTimeStr));
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        Element levelElement = element.selectFirst("div:eq(0)");
                        if (levelElement != null) {
                            String level = levelElement.text();
                            level = level.replaceAll(" Star", "");
                            sleepCommentHeaderDto.setLevel(level);
                        }
                        Element contentElement = element.selectFirst("div:eq(1)");
                        if (contentElement != null) {
                            String content = contentElement.text();
                            sleepCommentHeaderDto.setContent(content);
                        }
                        sleepCommentHeaderDtoList.add(sleepCommentHeaderDto);
                        // 评论
                        Elements subReplyElementList = element.select("div.bpr-review-business-response-grid");
                        if (UEmpty.isNotEmpty(subReplyElementList)) {
                            for (int j = 0; j < subReplyElementList.size(); j++) {
                                Element subReplyElement = subReplyElementList.get(j);
                                SleepCommentHeaderDto replyDto = new SleepCommentHeaderDto();
                                replyDto.setFloor(String.valueOf(j + 2));
                                replyDto.setUrl(sleepCommentHeaderDto.getUrl());
                                replyDto.setPage(sleepCommentHeaderVo.getPage());
                                replyDto.setUuid(UUID.randomUUID().toString());
                                replyDto.setDomain(sleepCommentHeaderDto.getDomain());

                                replyDto.setSource(sleepCommentHeaderVo.getSource());
                                replyDto.setContentType("REPLY");
                                Element replyTitleElement = subReplyElement.selectFirst("p.bpr-review-business-response-title");
                                if (replyTitleElement != null) {
                                    replyDto.setUserName(replyTitleElement.text());
                                }
                                Element replyTimeElement = subReplyElement.selectFirst("p.bpr-review-business-response-date");
                                if (replyTimeElement != null) {
                                    String replyTimeStr = replyTimeElement.text();
                                    replyTimeStr = replyTimeStr.replaceAll("Date: ", "");
                                    try {
                                        replyDto.setOperateTime(sdf.parse(replyTimeStr));
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                Element replyContentElement = subReplyElement.selectFirst("div.bpr-review-business-response-body");
                                if (replyContentElement != null) {
                                    String replyStr = replyContentElement.text();
                                    replyDto.setContent(replyStr);
                                }
                                replyDto.setFloor(String.valueOf(j + 2));
                                replyDto.setParentId(sleepCommentHeaderDto.getUuid());
                                sleepCommentHeaderDtoList.add(replyDto);
                                count++;
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
