package com.example.recruitment.utils;

import com.example.recruitment.dto.Education;
import com.example.recruitment.exception.CustomException;
import com.example.recruitment.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Converter
@RequiredArgsConstructor
public class EducationListJsonConverter implements AttributeConverter<List<Education>, String> {

    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(List<Education> attribute) {
        if (Objects.isNull(attribute)) {
            return Strings.EMPTY;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.CONVERT_ERROR);
        }
    }

    @Override
    public List<Education> convertToEntityAttribute(String dbData) {
        if (Strings.isBlank(dbData)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.CONVERT_ERROR);
        }
    }
}
