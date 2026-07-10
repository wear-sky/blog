package com.wearsky.demo.search.service;

import com.wearsky.demo.common.dto.SearchContentDTO;

import java.util.List;
import java.util.Map;

/**
 * 全文搜索服务接口
 */
public interface SearchService {

    /**
     * 保存文档到 ES
     */
    void saveDocument(SearchContentDTO dto);

    /**
     * 删除单个文档
     */
    void deleteDocument(String id, String type);

    /**
     * 批量删除文档
     */
    void batchDeleteDocuments(List<Long> ids, String type);

    /**
     * 全文搜索
     */
    Map<String, Object> search(String keyword, int pageNum, int pageSize, String type);
}
