package com.freesia.url.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freesia.url.po.UrlConfigPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Evad.Wu
 * @Description URL配置信息表 持久层
 * @date 2024-01-24
 */
@Mapper
public interface UrlConfigMapper extends BaseMapper<UrlConfigPo> {

}
