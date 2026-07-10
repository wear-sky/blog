package com.wearsky.demo.search.controller;

import com.wearsky.demo.common.domain.vo.ApiResponse;
import com.wearsky.demo.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 全文搜索 API
 */
@RestController
@RequestMapping("/search-service/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 全文搜索博客和回复
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String type
    ) {
        return ApiResponse.success(searchService.search(q, pageNum, pageSize, type));
    }
}
