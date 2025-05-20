package com.freesia.entity;

import com.freesia.dto.TreeDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Bliss.Wu
 * @Description 根据角色ID查询菜单列表 结果集
 * @date 2025-05-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FindSelectedMenuListByRoleIdEntity extends TreeDto<FindSelectedMenuListByRoleIdEntity> {
  /**
   * ID
   */
  private Long id;
  /**
   * 菜单名称
   */
  private String menuName;
  /**
   * 排序号
   */
  private Integer orderNum;
  /**
   * 是否可见
   */
  private String visible;
}
