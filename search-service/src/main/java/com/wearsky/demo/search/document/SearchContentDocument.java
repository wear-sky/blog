package com.wearsky.demo.search.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 全文搜索 ES 文档映射
 */
@Data
@Document(indexName = "search-content")
public class SearchContentDocument {

    @Id
    private String id;

    /**
     * 类型："blog" 或 "reply"
     */
    @Field(type = FieldType.Keyword)
    private String type;

    /**
     * 回复所属博客 ID（博客自身为 null）
     */
    @Field(type = FieldType.Long)
    private Long blogId;

    /**
     * 博客标题
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    /**
     * 正文内容
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    /**
     * 作者/回复者 ID
     */
    @Field(type = FieldType.Long)
    private Long authorId;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;
}
