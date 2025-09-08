package com.eaglebank.api.dto;

import java.time.LocalDateTime;

public record TransactionResponse(String id, String type, Double amount, LocalDateTime timestamp) {
}
