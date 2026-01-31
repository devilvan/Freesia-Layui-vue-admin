package com.freesia.notice.util;

import com.freesia.function.Modifier;
import com.freesia.notice.dto.SysNoticeDto;
import com.freesia.satoken.util.USecurity;
import com.freesia.util.USpring;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author Evad.Wu
 * @Description 消息通知 工具类
 * @date 2025-09-19
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings(value = "unchecked")
public class UNotice {

    /**
     * 构建消息通知实体并保存
     *
     * @param modifier 接收敏感信息的修改器
     * @param <T>      敏感信息bean的类型
     */
    public static <T extends SysNoticeDto> void recordSysNotice(Modifier<T> modifier) {
        SysNoticeDto sysNoticeDto = new SysNoticeDto();
        sysNoticeDto.setPublisherId(USecurity.getUserId());
        sysNoticeDto.setReadFlag(false);
        T dto = modifier.modify((T) sysNoticeDto);
        USpring.context().publishEvent(dto);
    }
}
