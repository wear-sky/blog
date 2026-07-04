package com.wearsky.demo.common.client;

import com.wearsky.demo.common.domain.vo.ApiResponse;
import com.wearsky.demo.common.domain.vo.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "user-service")
public interface UserClient {

    @GetMapping("/user-service/user/me")
    ApiResponse<UserVO> me();

    @GetMapping("/user-service/user/{id}")
    ApiResponse<UserVO> getById(@PathVariable Long id);

    @GetMapping("/user-service/user/ids")
    ApiResponse<List<UserVO>> getByIds(@RequestParam("ids") List<Long> ids);
}
