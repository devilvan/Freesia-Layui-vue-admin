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

import jakarta.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 城市表 映射
 * @date 2025-10-31
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "WORLD_CLOCK_CITY")

@Entity
@Table(name = "WORLD_CLOCK_CITY")
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "城市表 映射")
public class WorldClockCityPo extends BasePo implements Serializable {
    @Schema(description = "城市名称")
    @TableField(value = "CITY_NAME")
    @Column(name = "CITY_NAME", columnDefinition = "VARCHAR(32) NOT NULL COMMENT '城市名称'")
    private String cityName;
    @Schema(description = "国家编码")
    @TableField(value = "COUNTRY_CODE")
    @Column(name = "COUNTRY_CODE", columnDefinition = "VARCHAR(3) NOT NULL COMMENT '国家编码'")
    private String countryCode;
    @Schema(description = "时区")
    @TableField(value = "TIMEZONE")
    @Column(name = "TIMEZONE", columnDefinition = "VARCHAR(64) NOT NULL COMMENT '时区'")
    private String timezone;
    @Schema(description = "纬度")
    @TableField(value = "LATITUDE")
    @Column(name = "LATITUDE", columnDefinition = "DECIMAL(8) NOT NULL COMMENT '纬度'")
    private BigDecimal latitude;
    @Schema(description = "经度")
    @TableField(value = "LONGITUDE")
    @Column(name = "LONGITUDE", columnDefinition = "DECIMAL(9) NOT NULL COMMENT '经度'")
    private BigDecimal longitude;
}
