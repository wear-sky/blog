package com.wearsky.demo.log.service;

import com.wearsky.demo.common.dto.OperationLogDTO;
import com.wearsky.demo.log.repository.OperationLogRepository;
import com.wearsky.demo.log.service.impl.OperationLogServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OperationLogServiceImplTest {

    @InjectMocks
    private OperationLogServiceImpl operationLogService;

    @Mock
    private OperationLogRepository repository;

    @Test
    void saveLog_BelowThreshold_ShouldNotFlush() {
        OperationLogDTO dto = OperationLogDTO.builder()
                .module("test")
                .operation("test op")
                .build();

        operationLogService.saveLog(dto);

        verify(repository, never()).saveAll(any());
    }

    @Test
    void saveLog_AtThreshold_ShouldFlush() {
        for (int i = 0; i < 100; i++) {
            OperationLogDTO dto = OperationLogDTO.builder()
                    .module("test")
                    .operation("op" + i)
                    .build();
            operationLogService.saveLog(dto);
        }

        verify(repository).saveAll(anyList());
    }
}
