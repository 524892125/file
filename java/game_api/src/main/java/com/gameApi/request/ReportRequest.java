package com.gameApi.request;

import javax.validation.constraints.NotNull;

public class ReportRequest {
        @NotNull(message = "用户ID不能为空")
        private String userId;

        @NotNull(message = "被举报用户ID不能为空")
        private Integer reportUid;

        @NotNull(message = "举报内容不能为空")
        private String content;

        @NotNull(message = "举报类型不能为空")
        private String type;
}
