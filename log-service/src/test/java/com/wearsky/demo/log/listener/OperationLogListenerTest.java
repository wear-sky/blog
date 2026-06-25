package com.wearsky.demo.log.listener;

import com.wearsky.demo.common.dto.OperationLogDTO;
import com.wearsky.demo.log.service.OperationLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OperationLogListenerTest {

    @InjectMocks
    private OperationLogListener listener;

    @Mock
    private OperationLogService operationLogService;

    @Test
    void listenOperationLog_ShouldDelegateToService() {
        OperationLogDTO dto = OperationLogDTO.builder()
                .module("test")
                .operation("test op")
                .build();

        listener.listenOperationLog(dto);

        verify(operationLogService).saveLog(dto);
    }
}
