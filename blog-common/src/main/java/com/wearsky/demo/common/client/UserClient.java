package com.wearsky.demo.common.client;

import com.wearsky.demo.common.domain.vo.ApiResponse;
import com.wearsky.demo.common.domain.vo.UserVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "user-service")
public interface UserClient {

    @GetMapping("/user-service/user/me")
    ApiResponse<UserVO> me();

    @GetMapping("/user-service/user/{id}")
    ApiResponse<UserVO> getUser(@PathVariable Long id);

}
