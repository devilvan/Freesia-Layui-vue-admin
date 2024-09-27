package com.freesia.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freesia.po.SysOssConfigPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Evad.Wu
 * @Description OSS配置信息表 持久层
 * @date 2024-02-28
 */
@Mapper
public interface SysOssConfigMapper extends BaseMapper<SysOssConfigPo> {

}
