package com.freesia.worldclock.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.freesia.po.BasePo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * @author Evad.Wu
 * @Description 日出日落时间表 映射
 * @date 2025-10-31
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "WORLD_CLOCK_SUNRISE_SUNSET")

@Entity
@Table(name = "WORLD_CLOCK_SUNRISE_SUNSET")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "日出日落时间表 映射")
public class WorldClockSunriseSunsetPo extends BasePo implements Serializable {
    @Schema(description = "城市ID")
    @TableField(value = "CITY_ID")
    @Column(name = "CITY_ID", columnDefinition = "BIGINT(19) NOT NULL COMMENT '城市ID'")
    private Long cityId;
    @Schema(description = "日期")
    @TableField(value = "DATE")
    @Column(name = "DATE", columnDefinition = "DATE NOT NULL COMMENT '日期'")
    private LocalDate date;
    @Schema(description = "日出时间")
    @TableField(value = "SUNRISE_TIME")
    @Column(name = "SUNRISE_TIME", columnDefinition = "TIME NOT NULL COMMENT '日出时间'")
    private LocalTime sunriseTime;
    @Schema(description = "日落时间")
    @TableField(value = "SUNSET_TIME")
    @Column(name = "SUNSET_TIME", columnDefinition = "TIME NOT NULL COMMENT '日落时间'")
    private LocalTime sunsetTime;
    @Schema(description = "日长时间（分钟）")
    @TableField(value = "DAY_LENGTH_MINUTES")
    @Column(name = "DAY_LENGTH_MINUTES", columnDefinition = "INT(10) NOT NULL COMMENT '日长时间（分钟）'")
    private Integer dayLengthMinutes;
}
