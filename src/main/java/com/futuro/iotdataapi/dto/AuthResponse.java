package com.futuro.iotdataapi.dto;

public record AuthResponse(String username, String message, String accessToken, boolean success) {}
