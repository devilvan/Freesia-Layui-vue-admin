package com.freesia.oss.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freesia.oss.po.SysOssPo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Evad.Wu
 * @Description OSS对象存储表 持久层
 * @date 2024-02-27
 */
@Mapper
public interface SysOssMapper extends BaseMapper<SysOssPo> {

}
