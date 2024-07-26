package com.example.recruitment.service;

import com.example.recruitment.dto.ResumeDto;
import com.example.recruitment.entity.Member;
import com.example.recruitment.entity.Resume;
import com.example.recruitment.exception.CustomException;
import com.example.recruitment.exception.ErrorCode;
import com.example.recruitment.repository.MemberRepository;
import com.example.recruitment.repository.ResumeRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResumeService {

  private final ResumeRepository resumeRepository;
  private final MemberRepository memberRepository;

  @Transactional
  public Long postResume(ResumeDto.Request request) {
    Member member = memberRepository.findByLoginId(request.memberLoginId())
        .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

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
    return resumeRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND)).toDto();
  }

  @Transactional
  public ResumeDto.Response modifyResume(Long id, ResumeDto.Request request) {
    Resume resume = resumeRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));

    if (!Objects.equals(resume.getMember().getLoginId(), request.memberLoginId())) {
      throw new CustomException(ErrorCode.MEMBER_NOT_MATCH);
    }

    return resume.update(request).toDto();
  }

  @Transactional
  public void deleteResume(Long id, String memberLoginId) {
    Resume resume = resumeRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));

    if (!Objects.equals(resume.getMember().getLoginId(), memberLoginId)) {
      throw new CustomException(ErrorCode.MEMBER_NOT_MATCH);
    }

    resumeRepository.delete(resume);
  }
}
