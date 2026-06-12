package com.wearsky.demo.log.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * 操作日志 ES 文档映射
 */
@Data
@Document(indexName = "operation-log")
public class OperationLogDocument {

    @Id
    private String id;

    /**
     * 操作用户ID
     */
    @Field(type = FieldType.Long)
    private Long userId;

    /**
     * 操作用户名
     */
    @Field(type = FieldType.Keyword)
    private String username;

    /**
     * 所属模块
     */
    @Field(type = FieldType.Keyword)
    private String module;

    /**
     * 操作类型
     */
    @Field(type = FieldType.Keyword)
    private String operation;

    /**
     * 请求方法 (类名.方法名)
     */
    @Field(type = FieldType.Keyword)
    private String method;

    /**
     * HTTP方法
     */
    @Field(type = FieldType.Keyword)
    private String httpMethod;

    /**
     * 请求URL
     */
    @Field(type = FieldType.Keyword)
    private String url;

    /**
     * 请求参数 (仅存储，不索引)
     */
    @Field(type = FieldType.Keyword, index = false)
    private String params;

    /**
     * 客户端IP
     */
    @Field(type = FieldType.Keyword)
    private String ip;

    /**
     * 操作状态 (0成功/1失败)
     */
    @Field(type = FieldType.Integer)
    private Integer status;

    /**
     * 错误信息 (仅存储，不索引)
     */
    @Field(type = FieldType.Keyword, index = false)
    private String errorMsg;

    /**
     * 耗时(ms)
     */
    @Field(type = FieldType.Long)
    private Long duration;

    /**
     * 操作时间
     */
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdAt;
}
