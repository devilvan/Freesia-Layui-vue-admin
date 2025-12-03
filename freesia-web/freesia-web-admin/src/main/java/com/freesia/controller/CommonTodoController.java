package com.freesia.controller;

import com.freesia.dto.CommonTodoDto;
import com.freesia.pojo.PageQuery;
import com.freesia.pojo.TableResult;
import com.freesia.service.CommonTodoService;
import com.freesia.util.UCopy;
import com.freesia.vo.CommonTodoVo;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Evad.Wu
 * @Description 待办事项表 控制器
 * @date 2025-12-03
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/common/commonTodoController")
@Tag(name = "CommonTodoController", description = "待办事项表 控制器")
public class CommonTodoController extends BaseController {
    private final CommonTodoService commonTodoService;

    /**
     * 保存待办事项表信息
     *
     * @param commonTodoVo    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存待办事项表信息")
    @PostMapping(value = "saveUpdate")
    public R<Void> saveUpdate(@RequestBody CommonTodoVo commonTodoVo) {
        CommonTodoDto commonTodoDto = UCopy.copyVo2Dto(commonTodoVo, CommonTodoDto.class);
        commonTodoService.saveUpdate(commonTodoDto);
        return R.ok();
    }

    /**
     * 批量保存待办事项表信息
     *
     * @param commonTodoVoList    待保存对象
     * @return 形式返回
     */
    @Operation(summary = "保存待办事项表信息")
    @PostMapping(value = "saveUpdateBatch")
    public R<Void> saveUpdateBatch(@RequestBody List<CommonTodoVo> commonTodoVoList) {
        List<CommonTodoDto> commonTodoDtoList = UCopy.fullCopyList(commonTodoVoList, CommonTodoDto.class);
        commonTodoService.saveUpdateBatch(commonTodoDtoList);
        return R.ok();
    }

    /**
     * 查询待办事项表分页信息
     *
     * @param commonTodoVo 查询条件
     * @param pageQuery   分页条件
     * @return 形式返回
     */
    @Operation(summary = "查询待办事项表分页信息")
    @GetMapping(value = "findPageCommonTodo")
    public TableResult<CommonTodoDto> findPageCommonTodo(CommonTodoVo commonTodoVo, PageQuery pageQuery) {
        CommonTodoDto commonTodoDto = UCopy.copyVo2Dto(commonTodoVo, CommonTodoDto.class);
        return commonTodoService.findPageCommonTodo(commonTodoDto, pageQuery);
    }

    /**
     * 条件查询待办事项表
     *
     * @param commonTodoVo 查询条件
     * @return 形式返回
     */
    @Operation(summary = "条件查询待办事项表")
    @GetMapping(value = "findCommonTodo")
    public R<CommonTodoDto> findCommonTodo(CommonTodoVo commonTodoVo) {
        CommonTodoDto commonTodoDto = UCopy.copyVo2Dto(commonTodoVo, CommonTodoDto.class);
        commonTodoDto = commonTodoService.findCommonTodo(commonTodoDto);
        return R.ok(commonTodoDto);
    }

    /**
    * 条件查询待办事项表
    *
    * @param commonTodoVo 查询条件
    * @return 形式返回
    */
    @Operation(summary = "条件查询待办事项表")
    @GetMapping(value = "findListCommonTodo")
    public R<List<CommonTodoDto>> findListCommonTodo(CommonTodoVo commonTodoVo) {
        CommonTodoDto commonTodoDto = UCopy.copyVo2Dto(commonTodoVo, CommonTodoDto.class);
        List<CommonTodoDto> commonTodoDtoList = commonTodoService.findListCommonTodo(commonTodoDto);
        return R.ok(commonTodoDtoList);
    }

    /**
     * 删除待办事项表
     *
     * @param idList 主键
     * @return 形式返回
     */
    @Operation(summary = "删除待办事项表")
    @PostMapping(value = "deleteCommonTodo")
    public R<Void> deleteCommonTodo(@RequestBody List<Long> idList) {
        commonTodoService.deleteCommonTodo(idList);
        return R.ok();
    }
}
