package com.example.recruitment.dto;

import com.example.recruitment.entity.Resume;
import com.example.recruitment.type.ResumeStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class ResumeDto {
    public record Request(
            String title,
            String description,
            Integer workingYear,
            List<Education> education,
            ResumeStatus status,
            String memberLoginId
    ) {
        public Resume toEntity() {
            return Resume.builder()
                    .title(title)
                    .description(description)
                    .workingYear(workingYear)
                    .education(education)
                    .status(status)
                    .build();
        }
    }

    @Builder
    @Getter
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private Integer workingYear;
        private List<Education> education;
        private ResumeStatus status;
        private LocalDateTime modifyDate;
        private LocalDateTime postingDate;
        private Long memberId;
        private String memberName;

    }


}
