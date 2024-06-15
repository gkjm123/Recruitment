package com.example.recruitment.service;

import com.example.recruitment.dto.ResumeDto;
import com.example.recruitment.entity.Member;
import com.example.recruitment.entity.Resume;
import com.example.recruitment.repository.MemberRepository;
import com.example.recruitment.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long postResume(ResumeDto.Request request) {
        Member member = memberRepository.findByLoginId(request.memberLoginId())
                .orElseThrow(() -> new RuntimeException("개인회원 정보 없음"));

        Resume resume = request.toEntity();
        resume.setMember(member);
        resumeRepository.save(resume);

        return resume.getId();
    }

    @Transactional(readOnly = true)
    public List<ResumeDto.Response> getResumeList(String memberLoginId) {
        return resumeRepository.findAllByMember_LoginId(memberLoginId).stream()
                .map(Resume::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ResumeDto.Response getResume(Long id) {
        return resumeRepository.findById(id).orElseThrow(() -> new RuntimeException("이력서 정보 없음")).toDto();
    }

    @Transactional
    public ResumeDto.Response modifyResume(Long id, ResumeDto.Request request) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("이력서 정보 없음"));

        if (!Objects.equals(resume.getMember().getLoginId(), request.memberLoginId())) {
            throw new RuntimeException("이력서를 작성한 개인회원만 수정할수 있습니다.");
        }

        return resume.update(request).toDto();
    }

    @Transactional
    public void deleteResume(Long id, String memberLoginId) {
        Resume resume = resumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("이력서 정보 없음"));

        if (!Objects.equals(resume.getMember().getLoginId(), memberLoginId)) {
            throw new RuntimeException("이력서를 작성한 개인회원만 삭제할수 있습니다.");
        }

        resumeRepository.delete(resume);
    }
}
