package com.wearsky.demo.user.domain.vo;

import com.wearsky.demo.common.domain.vo.UserVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class UserPageVO {

    @Schema(description = "总数据量")
    private Long total;

    @Schema(description = "用户信息列表")
    List<UserVO> users;
}
