package com.example.recruitment.entity;

import com.example.recruitment.dto.Education;
import com.example.recruitment.dto.RecruitmentDto;
import com.example.recruitment.dto.ResumeDto;
import com.example.recruitment.type.RecruitmentStatus;
import com.example.recruitment.type.ResumeStatus;
import com.example.recruitment.utils.EducationListJsonConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_id")
    private Long id;
    private String title;
    private String description;

    private Integer workingYear;

    @Convert(converter = EducationListJsonConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<Education> education;

    @Enumerated(EnumType.STRING)
    private ResumeStatus status;

    @UpdateTimestamp
    private LocalDateTime modifyDate;
    @CreationTimestamp
    private LocalDateTime postingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Resume(String title,
                  String description,
                  Integer workingYear,
                  List<Education> education,
                  ResumeStatus status) {

        this.title = title;
        this.description = description;
        this.workingYear = workingYear;
        this.education = education;
        this.status = status;
    }


    public ResumeDto.Response toDto() {
        return ResumeDto.Response.builder()
                .id(this.id)
                .title(this.title)
                .description(this.description)
                .workingYear(this.workingYear)
                .education(this.education)
                .status(this.status)
                .modifyDate(this.modifyDate)
                .postingDate(this.postingDate)
                .memberId(this.member.getId())
                .memberName(this.member.getName())
                .build();
    }

    public Resume update(ResumeDto.Request request) {
        this.title = request.title();
        this.description = request.description();
        this.workingYear = request.workingYear();
        this.education = request.education();
        this.status = request.status();

        return this;
    }

}
