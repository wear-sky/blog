package com.wearsky.demo.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.wearsky.demo.common.dto.SearchContentDTO;
import com.wearsky.demo.search.document.SearchContentDocument;
import com.wearsky.demo.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * 全文搜索服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private static final String INDEX_NAME = "search-content";

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public void saveDocument(SearchContentDTO dto) {
        SearchContentDocument doc = new SearchContentDocument();
        doc.setId(String.valueOf(dto.getId()));
        doc.setType(dto.getType());
        doc.setBlogId(dto.getBlogId());
        doc.setTitle(dto.getTitle());
        doc.setContent(dto.getContent());
        doc.setAuthorId(dto.getAuthorId());
        doc.setCreatedAt(dto.getCreatedAt());

        try {
            elasticsearchClient.index(i -> i
                    .index(INDEX_NAME)
                    .id(doc.getId())
                    .document(doc)
            );
            log.debug("文档索引成功: type={}, id={}", dto.getType(), dto.getId());
        } catch (IOException e) {
            throw new RuntimeException("ES 索引文档失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteDocument(String id, String type) {
        try {
            elasticsearchClient.delete(d -> d
                    .index(INDEX_NAME)
                    .id(id)
            );
            log.debug("文档删除成功: type={}, id={}", type, id);
        } catch (IOException e) {
            log.error("ES 删除文档失败: type={}, id={}, error={}", type, id, e.getMessage());
        }
    }

    @Override
    public void batchDeleteDocuments(List<Long> ids, String type) {
        try {
            List<String> idStrings = ids.stream().map(String::valueOf).toList();
            elasticsearchClient.deleteByQuery(d -> d
                    .index(INDEX_NAME)
                    .query(q -> q.terms(t -> t
                            .field("_id")
                            .terms(tv -> tv.value(idStrings.stream()
                                    .map(co.elastic.clients.elasticsearch._types.FieldValue::of)
                                    .toList()))
                    ))
            );
            log.debug("批量文档删除成功: type={}, count={}", type, ids.size());
        } catch (IOException e) {
            log.error("ES 批量删除文档失败: type={}, count={}, error={}", type, ids.size(), e.getMessage());
        }
    }

    @Override
    public Map<String, Object> search(String keyword, int pageNum, int pageSize, String type) {
        try {
            SearchRequest.Builder builder = new SearchRequest.Builder()
                    .index(INDEX_NAME)
                    .from((pageNum - 1) * pageSize)
                    .size(pageSize)
                    .query(q -> q.bool(b -> {
                        // multi_match 搜索 title 和 content
                        b.must(m -> m.multiMatch(mm -> mm
                                .query(keyword)
                                .fields("title", "content")
                        ));
                        // 按类型过滤
                        if (type != null && !type.isEmpty()) {
                            b.filter(f -> f.term(t -> t.field("type").value(type)));
                        }
                        return b;
                    }))
                    // 高亮配置
                    .highlight(h -> h
                            .preTags("<em>")
                            .postTags("</em>")
                            .fields("title", hf -> hf.fragmentSize(200).numberOfFragments(1))
                            .fields("content", hf -> hf.fragmentSize(200).numberOfFragments(1))
                    )
                    .sort(s -> s.score(sc -> sc.order(SortOrder.Desc)));

            SearchResponse<SearchContentDocument> response = elasticsearchClient.search(builder.build(), SearchContentDocument.class);

            List<Map<String, Object>> list = new ArrayList<>();
            for (Hit<SearchContentDocument> hit : response.hits().hits()) {
                SearchContentDocument doc = hit.source();
                if (doc == null) continue;

                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", doc.getId());
                item.put("type", doc.getType());
                item.put("blogId", doc.getBlogId());
                item.put("authorId", doc.getAuthorId());
                item.put("createdAt", doc.getCreatedAt());

                // 优先使用高亮结果
                Map<String, List<String>> highlights = hit.highlight();
                item.put("title", highlights.containsKey("title")
                        ? highlights.get("title").get(0)
                        : doc.getTitle());
                item.put("content", highlights.containsKey("content")
                        ? highlights.get("content").get(0)
                        : doc.getContent());

                list.add(item);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("total", response.hits().total() != null ? response.hits().total().value() : 0);
            result.put("list", list);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("ES 搜索失败: " + e.getMessage(), e);
        }
    }
}
