package com.wearsky.demo.log.repository;

import com.wearsky.demo.log.document.OperationLogDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * 操作日志 ES Repository
 */
@Repository
public interface OperationLogRepository extends ElasticsearchRepository<OperationLogDocument, String> {

}
