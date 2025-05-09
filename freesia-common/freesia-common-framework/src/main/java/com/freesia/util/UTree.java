package com.freesia.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.lang.tree.parser.NodeParser;
import com.freesia.constant.AdminConstant;
import com.freesia.dto.TreeDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Evad.Wu
 * @Description 构造树形结构 工具类
 * @date 2023-09-03
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UTree extends TreeUtil {
    public static final String LABEL = "label";
    public static final String PARENT_ID = "parentId";
    /**
     * 根据前端定制差异化字段
     */
    public static final TreeNodeConfig DEFAULT_CONFIG = TreeNodeConfig.DEFAULT_CONFIG.setNameKey(LABEL);


    public static <T, K> List<Tree<K>> build(List<T> list, NodeParser<T, K> nodeParser) {
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        K k = UReflect.invokeGetter(list.get(0), PARENT_ID);
        return TreeUtil.build(list, k, DEFAULT_CONFIG, nodeParser);
    }

//    /**
//     * 广度优先算法
//     * 获取目标节点及其子节点
//     *
//     * @param list   需要遍历的集合
//     * @param target 选取的节点
//     * @return 选取的节点及其所有子节点
//     */
//    private <T> List<T> bfs(List<T> list, T target) {
//        List<T> nodeAndChildren = new ArrayList<>();
//        Queue<T> queue = new LinkedList<>();
//        queue.offer(target);
//        while (!queue.isEmpty()) {
//            T current = queue.poll();
//            for (T menuPo : list) {
//                if (current.getId().equals(menuPo.getParentId())) {
//                    queue.offer(menuPo);
//                }
//            }
//            nodeAndChildren.add(current);
//        }
//        return nodeAndChildren;
//    }

    /**
     * 构建树结构
     *
     * @param targetList 总数据
     * @param <T>        节点类型
     * @return 生成的树
     */
    public static <T extends TreeDto<T>> List<T> buildTree(List<T> targetList) {
        List<T> resultList = new ArrayList<>();
        for (T target : targetList) {
            // 从顶级节点开始装配
            if (AdminConstant.MENU_TOP_PARENT_ID.equals(target.getParentId())) {
                recursionChildList(targetList, target);
                resultList.add(target);
            }
        }
        return resultList;
    }

    /**
     * 递归遍历节点，生成树
     *
     * @param targetList 总数据
     * @param target     递归的节点
     * @param <T>        节点类型
     */
    private static <T extends TreeDto<T>> void recursionChildList(List<T> targetList, T target) {
        List<T> childList = findChildList(targetList, target);
        target.setChildren(childList);
        for (T child : childList) {
            if (hasChild(targetList, child)) {
                recursionChildList(targetList, child);
            }
        }
    }

    /**
     * 判断节点是否有子节点
     *
     * @param targetList 总数据
     * @param target     待验证子节点的节点
     * @param <T>        节点类型
     * @return flag
     */
    private static <T extends TreeDto<T>> boolean hasChild(List<T> targetList, T target) {
        return UEmpty.isNotEmpty(findChildList(targetList, target));
    }

    /**
     * 查询target在targetList中是否存在子节点
     *
     * @param targetList 总数据
     * @param target     待查询子节点的节点
     * @param <T>        节点类型
     * @return 子节点列表
     */
    public static <T extends TreeDto<T>> List<T> findChildList(List<T> targetList, T target) {
        return targetList.stream().filter(f -> f.getParentId().equals(target.getId())).toList();
    }
}
