package com.wearsky.demo.log.config;

import org.springframework.context.annotation.Configuration;

/**
 * Elasticsearch 配置
 * Spring Boot 自动配置会根据 application.yaml 中的 spring.elasticsearch.uris 创建客户端
 * 此类保留用于后续扩展（如自定义 ObjectMapper、索引策略等）
 */
@Configuration
public class EsConfig {

}
