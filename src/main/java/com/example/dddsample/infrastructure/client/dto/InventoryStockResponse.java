package com.example.dddsample.infrastructure.client.dto;

public record InventoryStockResponse(String productId, boolean available, int stock) {}
