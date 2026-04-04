package com.zorvyn.finance_data_processing.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;


@Data
@AllArgsConstructor
public class RegisterResponse {
    private UUID id;
    private String email;
    private String role;
}
