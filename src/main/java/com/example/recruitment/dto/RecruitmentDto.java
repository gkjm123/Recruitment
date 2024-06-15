package com.example.recruitment.dto;

import com.example.recruitment.entity.Recruitment;
import com.example.recruitment.type.RecruitmentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class RecruitmentDto {
    public record Request(
        String title,
        Integer recruitmentCount,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime closingDate,
        String companyMemberLoginId,
        RecruitmentStatus status
    ) {
        public Recruitment toEntity() {
            return Recruitment.builder()
                    .title(title)
                    .recruitmentCount(recruitmentCount)
                    .closingDate(closingDate)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class Response {
        private Long id;
        private String title;
        private Integer recruitmentCount;
        private LocalDateTime closingDate;
        private RecruitmentStatus status;
        private LocalDateTime modifyDate;
        private LocalDateTime postingDate;
        private Long companyMemberId;
        private String companyName;

    }


}
