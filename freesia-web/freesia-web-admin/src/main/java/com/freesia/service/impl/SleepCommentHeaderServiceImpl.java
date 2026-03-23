package com.freesia.service.impl;

import cn.hutool.core.date.DatePattern;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.constant.FlagConstant;
import com.freesia.convert.MapStructConverter;
import com.freesia.vo.SleepCommentHeaderVo;
import com.freesia.dto.SleepCommentHeaderDto;
import com.freesia.po.SleepCommentHeaderPo;
import com.freesia.service.SleepCommentHeaderService;
import com.freesia.converter.SleepCommentHeaderConverter;
import com.freesia.mapper.SleepCommentHeaderMapper;
import com.freesia.repository.SleepCommentHeaderRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import com.freesia.util.UEmpty;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
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
        int maxPage = 31;
        String targetUrl = "";
        List<SleepCommentHeaderDto> sleepCommentHeaderDtoList = new ArrayList<>();
        int count = 0;
        for (int i = 1; i <= maxPage; i++) {
            if (i == 1) {
                targetUrl = "https://www.trustpilot.com/review/eightsleep.com";
            } else {
                targetUrl = "https://www.trustpilot.com/review/eightsleep.com?page=" + i;
            }
            Connection connect = Jsoup.connect(targetUrl).timeout(120000).maxBodySize(0);
            connect.header("Accept-Encoding", "gzip, deflate, br");
            Element body = null;
            try {
                body = connect.get().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (body != null) {
                SleepCommentHeaderDto dto = new SleepCommentHeaderDto();
                dto.setSource("TrustPilot");
                Elements elementList = body.select("div.styles_wrapper__ie3f0");
                if (UEmpty.isNotEmpty(elementList)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    for (int j = 0; j < elementList.size(); j++) {
                        Element element = elementList.get(j);
                        Element userNameElement = element.selectFirst(".styles_reviewCardInnerHeader__8Xqy8 > aside > div > a > span");
                        String username = userNameElement.text();
                        System.out.println("userName: " + username);
                        String operateTime = element.selectFirst("div > article > div > div.styles_reviewCardInnerHeader__8Xqy8 > div > time").attr("datetime");
                        System.out.println("operateTime: " + operateTime);
                        String levelStr = element.selectFirst("article > div > section > div.styles_reviewHeader__DzoAZ").attr("data-service-review-rating");
                        if (UEmpty.isNotEmpty(levelStr)) {
                            int level = Integer.parseInt(levelStr);
                            dto.setLevel(levelStr);
                            System.out.println("level: " + level);
                        }
                        String title = element.selectFirst("article > div > section > div.styles_reviewContent__tuXiN > a > h2").text();
                        System.out.println("title: " + title);
                        String content = element.selectFirst("article > div > section > div.styles_reviewContent__tuXiN > p").text();
                        System.out.println("content: " + content);
                        dto.setUserId(null);
                        dto.setUserName(username);
                        dto.setContent(content);
                        try {
                            dto.setOperateTime(sdf.parse(operateTime));
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        dto.setFloor(String.valueOf(j + 1));
                        dto.setContentType("COMMENT");
                        dto.setUrl(targetUrl);
                        count++;
                    }
                }
                sleepCommentHeaderDtoList.add(dto);
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
