package com.freesia.exception;

import com.freesia.constant.GiteeModule;

import java.io.Serial;

/**
 * @author Evad.Wu
 * @Description Gitee提交记录功能 异常类
 * @date 2024-01-12
 */

public class GiteeCommitException extends ServiceException {
    @Serial
    private static final long serialVersionUID = -5609316263727929931L;

    public GiteeCommitException(String code, Object... args) {
        super(GiteeModule.GITEE_MANAGEMENT, code, args, null);
    }
}
