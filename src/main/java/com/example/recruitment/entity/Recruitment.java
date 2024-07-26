package com.example.recruitment.entity;

import com.example.recruitment.dto.RecruitmentDto;
import com.example.recruitment.type.RecruitmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recruitment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "recruitment_id")
  private Long id;
  private String title;
  private Integer recruitmentCount;
  private LocalDateTime closingDate;
  @Enumerated(EnumType.STRING)
  private RecruitmentStatus status;
  @UpdateTimestamp
  private LocalDateTime modifyDate;
  @CreationTimestamp
  private LocalDateTime postingDate;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_member_id")
  private CompanyMember companyMember;

  @Builder
  public Recruitment(String title,
      Integer recruitmentCount,
      LocalDateTime closingDate) {
    this.title = title;
    this.recruitmentCount = recruitmentCount;
    this.closingDate = closingDate;
  }

  public void opening() {
    this.status = RecruitmentStatus.OPEN;
  }

  public RecruitmentDto.Response toDto() {
    return RecruitmentDto.Response.builder()
        .id(this.id)
        .title(this.title)
        .recruitmentCount(this.recruitmentCount)
        .closingDate(this.closingDate)
        .status(this.status)
        .modifyDate(this.modifyDate)
        .postingDate(this.postingDate)
        .companyMemberId(this.companyMember.getId())
        .companyName(this.companyMember.getCompanyName())
        .build();
  }

  public Recruitment update(RecruitmentDto.Request request) {
    this.title = request.title();
    this.recruitmentCount = request.recruitmentCount();
    this.closingDate = request.closingDate();
    this.status = request.status();

    return this;
  }
}
