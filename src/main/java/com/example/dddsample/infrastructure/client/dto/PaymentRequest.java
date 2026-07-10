package com.example.dddsample.infrastructure.client.dto;

import java.math.BigDecimal;

public record PaymentRequest(String customerId, BigDecimal amount, String currency) {}
