package com.wearsky.demo.log.service;

import com.wearsky.demo.common.dto.OperationLogDTO;

/**
 * 操作日志服务接口
 */
public interface OperationLogService {

    /**
     * 保存操作日志
     */
    void saveLog(OperationLogDTO dto);
}
