package com.freesia.icon.vo;

import com.freesia.vo.BaseVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Bliss.Wu
 * @Description 保存 值对象
 * @date 2025-03-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "保存 值对象")
public class CommonIconSaveUpdateVo extends BaseVo {
    /**
     * 文件
     */
    @Schema(description = "文件")
    @NotNull(message = "{oss.file.required}")
    private List<MultipartFile> fileList;
    /**
     * 待保存的入参
     */
    @Schema(description = "待保存的入参")
    private CommonIconVo commonIconVo;
}
