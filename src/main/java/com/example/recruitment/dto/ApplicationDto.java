package com.example.recruitment.dto;

public class ApplicationDto {
    public record Request(
            String memberLoginId,
            Long resumeId
    ) {}
}