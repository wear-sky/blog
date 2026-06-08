package com.wearsky.demo.common.client;

import com.wearsky.demo.common.domain.vo.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "blog-service")
public interface BlogClient {

    @DeleteMapping("/blog-service/blog/author/{authorId}")
    ApiResponse<Void> deleteBogsByAuthorId(@PathVariable Long authorId);

}
