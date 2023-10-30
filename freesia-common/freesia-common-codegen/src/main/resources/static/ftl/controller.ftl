package ${packageName}.controller;

import ${packageName}.vo.${dataBaseDto.className}Vo;
import ${packageName}.service.${dataBaseDto.className}Service;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import javax.annotation.Resource;

/**
 * @author ${author}
 * @Description ${dataBaseDto.comment} 控制器
 * @date ${date}
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "${dataBaseDto.className?uncap_first}Controller")
@Tag(name = "${dataBaseDto.className}Controller", description = "${dataBaseDto.comment} 控制器")
public class ${dataBaseDto.className}Controller {
    private final ${dataBaseDto.className}Service ${dataBaseDto.className?uncap_first}Service;

    /**
     * 总请求控制器
     *
     * @return 形式返回
     */
    @Operation(summary = "总请求控制器")
    @RequestMapping(value = "do${dataBaseDto.className}")
    public R<${dataBaseDto.className}Vo> do${dataBaseDto.className}() {
        return R.ok();
    }

    /**
     * 采集报文控制器
     *
     * @return 形式返回
     */
    @Operation(summary = "采集报文控制器")
    @RequestMapping(value = "do${dataBaseDto.className}Crawler")
    public R<${dataBaseDto.className}Vo> do${dataBaseDto.className}Crawler() {
        return R.ok();
    }

    /**
     * 解析报文控制器
     *
     * @param request 报文
     * @return 形式返回
     */
    @Operation(summary = "解析报文控制器")
    @RequestMapping(value = "do${dataBaseDto.className}Request")
    public R<${dataBaseDto.className}Vo> do${dataBaseDto.className}Request(@RequestBody String request) {
        return R.ok();
    }
}
