package com.zorvyn.finance_data_processing.dto.response;

import com.zorvyn.finance_data_processing.enums.Role;
import com.zorvyn.finance_data_processing.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private Role role;
    private Status status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
