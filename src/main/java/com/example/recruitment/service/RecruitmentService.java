package com.example.recruitment.service;

import com.example.recruitment.dto.ApplicationDto;
import com.example.recruitment.dto.Education;
import com.example.recruitment.dto.RecruitmentDto;
import com.example.recruitment.dto.ResumeDto;
import com.example.recruitment.entity.Application;
import com.example.recruitment.entity.CompanyMember;
import com.example.recruitment.entity.Recruitment;
import com.example.recruitment.entity.Resume;
import com.example.recruitment.exception.CustomException;
import com.example.recruitment.exception.ErrorCode;
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
import java.util.Comparator;
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
                        .orElseThrow( () -> new CustomException(ErrorCode.COMPANY_NOT_FOUND));

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
        return recruitmentRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.RECRUITMENT_NOT_FOUND)).toDto();
    }

    @Transactional
    public RecruitmentDto.Response modifyRecruitment(Long id, RecruitmentDto.Request request) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.RECRUITMENT_NOT_FOUND));

        if (!Objects.equals(recruitment.getCompanyMember().getLoginId(), request.companyMemberLoginId())) {
            throw new CustomException(ErrorCode.COMPANY_NOT_MATCH);
        }

        return recruitment.update(request).toDto();
    }

    @Transactional
    public void deleteRecruitment(Long id, String companyLoginId) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.RECRUITMENT_NOT_FOUND));

        if (!Objects.equals(recruitment.getCompanyMember().getLoginId(), companyLoginId)) {
            throw new CustomException(ErrorCode.COMPANY_NOT_MATCH);
        }

        recruitmentRepository.delete(recruitment);
    }

    @Transactional
    public Long apply(Long recruitmentId, ApplicationDto.Request request) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECRUITMENT_NOT_FOUND));

        if(recruitment.getStatus() == RecruitmentStatus.CLOSE) {
            throw new CustomException(ErrorCode.QUITED_RECRUITMENT);
        }

        Resume resume = resumeRepository.findById(request.resumeId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));

        if (!Objects.equals(resume.getMember().getLoginId(), request.memberLoginId())) {
            System.out.println(resume.getMember().getId());
            System.out.println(request.memberLoginId());
            throw new CustomException(ErrorCode.MEMBER_NOT_MATCH);
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
                .orElseThrow(() -> new CustomException(ErrorCode.RECRUITMENT_NOT_FOUND));

        if (!Objects.equals(recruitment.getCompanyMember().getLoginId(), companyLoginId)) {
            throw new CustomException(ErrorCode.COMPANY_NOT_MATCH);
        }

        List<Application> applications = applicationRepository.findAllByRecruitment_Id(recruitmentId);

        if(applications.isEmpty()) {
            throw new CustomException(ErrorCode.NO_RECRUITMENT);
        }

        return applications.stream().map(a -> a.getResume().toDto()).toList();
    }

    @Transactional
    public void quitRecruitment(Long id, String companyLoginId) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.RECRUITMENT_NOT_FOUND));

        if (!Objects.equals(recruitment.getCompanyMember().getLoginId(), companyLoginId)) {
            throw new CustomException(ErrorCode.COMPANY_NOT_MATCH);
        }

        if (recruitment.getStatus() == RecruitmentStatus.CLOSE) {
            throw new CustomException(ErrorCode.QUITED_RECRUITMENT);
        }

        recruitment.setStatus(RecruitmentStatus.CLOSE);
    }

    @Transactional
    public List<ResumeDto.Response> autoPickRecruitment(Long id, String companyLoginId) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.RECRUITMENT_NOT_FOUND));

        if (!Objects.equals(recruitment.getCompanyMember().getLoginId(), companyLoginId)) {
            throw new CustomException(ErrorCode.COMPANY_NOT_MATCH);
        }

        if (recruitment.getStatus() == RecruitmentStatus.OPEN) {
            throw new CustomException(ErrorCode.NOT_QUITED_RECRUITMENT);
        }

        List<Application> applications = applicationRepository.findAllByRecruitment_Id(id);

        if(applications.isEmpty()) {
            throw new CustomException(ErrorCode.NO_RECRUITMENT);
        }

        List<ResumeDto.Response> passMemberList = new ArrayList<>();

        //우선 모든 지원자 상태를 FAIL 로 바꾸고
        applications.forEach(a -> a.setStatus(ApplicationStatus.FAIL));

        //score 함수를 통해 지원자의 점수를 계산해서 높은 순으로 정렬하고 공고의 채용인원만큼 잘라서 PASS 상태로 바꿔준다.
        applications.stream()
                .sorted(Comparator.comparing((Application a) -> score(a.getResume().toDto()), Comparator.reverseOrder()))
                .limit(recruitment.getRecruitmentCount())
                .forEach(a -> {
                    a.setStatus(ApplicationStatus.PASS);
                    passMemberList.add(a.getResume().toDto());
                });

        return passMemberList;
    }

    //학력과 경력을 통해 점수를 산출
    public Integer score(ResumeDto.Response resumeDto) {
        int degreePoint = resumeDto.getEducation().stream().mapToInt(Education::getDegree).sum();
        int expPoint = resumeDto.getWorkingYear();
        return degreePoint + expPoint;
    }

    @Transactional
    public String checkPassOrFail(Long id, String memberLoginId) {
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.RECRUITMENT_NOT_FOUND));

        Application application = applicationRepository.findByRecruitmentAndResume_Member_LoginId(recruitment, memberLoginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_RECRUITMENT));

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
