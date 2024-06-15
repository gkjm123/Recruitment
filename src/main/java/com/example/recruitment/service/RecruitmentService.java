package com.example.recruitment.service;

import com.example.recruitment.dto.ApplicationDto;
import com.example.recruitment.dto.Education;
import com.example.recruitment.dto.RecruitmentDto;
import com.example.recruitment.dto.ResumeDto;
import com.example.recruitment.entity.Application;
import com.example.recruitment.entity.CompanyMember;
import com.example.recruitment.entity.Recruitment;
import com.example.recruitment.entity.Resume;
import com.example.recruitment.repository.ApplicationRepository;
import com.example.recruitment.repository.CompanyMemberRepository;
import com.example.recruitment.repository.RecruitmentRepository;
import com.example.recruitment.repository.ResumeRepository;
import com.example.recruitment.type.ApplicationStatus;
import com.example.recruitment.type.RecruitmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RecruitmentService {
    private final RecruitmentRepository recruitmentRepository;
    private final CompanyMemberRepository companyMemberRepository;
    private final ResumeRepository resumeRepository;
    private final ApplicationRepository applicationRepository;

    @Transactional
    public Long postRecruitment(RecruitmentDto.Request request) {
        CompanyMember companyMember = companyMemberRepository.findByLoginId(request.companyMemberLoginId())
                        .orElseThrow( () -> new RuntimeException("기업 정보 없음"));

        Recruitment recruitment = request.toEntity();
        recruitment.setCompanyMember(companyMember);
        recruitment.opening();

        recruitmentRepository.save(recruitment);

        return recruitment.getId();
    }

    @Transactional(readOnly = true)
    public List<RecruitmentDto.Response> getRecruitmentList() {
        List<Recruitment> recruitmentList = recruitmentRepository.findAllByStatus(RecruitmentStatus.OPEN);
        return recruitmentList.stream().map(Recruitment::toDto).toList();
    }

    @Transactional(readOnly = true)
    public RecruitmentDto.Response getRecruitment(Long id) {
        return recruitmentRepository.findById(id).orElseThrow(() -> new RuntimeException("채용공고 정보 없음")).toDto();
    }

    @Transactional
    public RecruitmentDto.Response modifyRecruitment(Long id, RecruitmentDto.Request request) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("채용공고 정보 없음"));

        if (!Objects.equals(recruitment.getCompanyMember().getLoginId(), request.companyMemberLoginId())) {
            throw new RuntimeException("공고를 작성한 기업회원만 수정할수 있습니다.");
        }

        return recruitment.update(request).toDto();
    }

    @Transactional
    public void deleteRecruitment(Long id, String companyLoginId) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("채용공고 정보 없음"));

        if (!Objects.equals(recruitment.getCompanyMember().getLoginId(), companyLoginId)) {
            throw new RuntimeException("공고를 작성한 기업회원만 삭제할수 있습니다.");
        }

        recruitmentRepository.delete(recruitment);
    }

    @Transactional
    public Long apply(Long recruitmentId, ApplicationDto.Request request) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new RuntimeException("채용공고 정보 없음"));

        if(recruitment.getStatus() == RecruitmentStatus.CLOSE) {
            throw new RuntimeException("모집 마감된 채용공고입니다.");
        }

        Resume resume = resumeRepository.findById(request.resumeId())
                .orElseThrow(() -> new RuntimeException("이력서 정보 없음"));

        if (!Objects.equals(resume.getMember().getLoginId(), request.memberLoginId())) {
            System.out.println(resume.getMember().getId());
            System.out.println(request.memberLoginId());
            throw new RuntimeException("이력서를 작성한 개인회원이 아닙니다.");
        }

        Application application = Application.builder()
                .recruitment(recruitment)
                .resume(resume)
                .status(ApplicationStatus.APPLY_FINISHED)
                .build();

        return applicationRepository.save(application).getId();
    }

    @Transactional
    public List<ResumeDto.Response> getApplicantResume(Long recruitmentId, String companyLoginId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new RuntimeException("채용공고 정보 없음"));

        if (!Objects.equals(recruitment.getCompanyMember().getLoginId(), companyLoginId)) {
            throw new RuntimeException("공고를 작성한 기업회원만 이력서를 확인할수 있습니다.");
        }

        List<Application> applications = applicationRepository.findAllByRecruitment_Id(recruitmentId);

        if(applications.isEmpty()) {
            throw new RuntimeException("지원한 이력서가 없습니다.");
        }

        return applications.stream().map(a -> a.getResume().toDto()).toList();
    }

    @Transactional
    public void quitRecruitment(Long id, String companyLoginId) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("채용공고 정보 없음"));

        if (!Objects.equals(recruitment.getCompanyMember().getLoginId(), companyLoginId)) {
            throw new RuntimeException("공고를 작성한 기업회원만 모집 마감 할 수 있습니다.");
        }

        if (recruitment.getStatus() == RecruitmentStatus.CLOSE) {
            throw new RuntimeException("이미 마감된 공고입니다.");
        }

        recruitment.setStatus(RecruitmentStatus.CLOSE);
    }

    @Transactional
    public List<ResumeDto.Response> autoPickRecruitment(Long id, String companyLoginId) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("채용공고 정보 없음"));

        if (!Objects.equals(recruitment.getCompanyMember().getLoginId(), companyLoginId)) {
            throw new RuntimeException("공고를 작성한 기업회원만 자동선발 할 수 있습니다.");
        }

        if (recruitment.getStatus() == RecruitmentStatus.OPEN) {
            throw new RuntimeException("모집 마감후 자동선발 해주세요.");
        }

        List<Application> applications = applicationRepository.findAllByRecruitment_Id(id);

        if(applications.isEmpty()) {
            throw new RuntimeException("지원한 이력서가 없습니다.");
        }

        List<ResumeDto.Response> passMemberList = new ArrayList<>();

        applications.forEach(a -> a.setStatus(ApplicationStatus.FAIL));
        applications.stream().filter(a -> score(a.getResume().toDto()) >= 2)
                .forEach(a -> {
                    a.setStatus(ApplicationStatus.PASS);
                    passMemberList.add(a.getResume().toDto());
                });

        return passMemberList;
    }

    public Integer score(ResumeDto.Response resumeDto) {
        return resumeDto.getEducation().stream().mapToInt(Education::getDegree).sum();
    }

    @Transactional
    public String checkPassOrFail(Long id, String memberLoginId) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("채용공고 정보 없음"));

        Application application = applicationRepository.findByRecruitmentAndResume_Member_LoginId(recruitment, memberLoginId)
                .orElseThrow(() -> new RuntimeException("해당공고에 지원한 이력 없음"));

        if (application.getStatus() == ApplicationStatus.FAIL) {
            return "탈락입니다.";
        }
        else if (application.getStatus() == ApplicationStatus.PASS) {
            return "합격입니다.";
        }
        else {
            return "선발작업 대기중입니다.";
        }
    }
}
