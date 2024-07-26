package com.example.recruitment.controller;

import com.example.recruitment.dto.ApplicationDto;
import com.example.recruitment.dto.RecruitmentDto;
import com.example.recruitment.dto.ResumeDto;
import com.example.recruitment.service.RecruitmentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class RecruitmentController {

  private final RecruitmentService recruitmentService;

  //기업 멤버가 채용 공고를 등록
  @PostMapping("/recruitments")
  public Long postingRecruitment(@RequestBody RecruitmentDto.Request request) {
    return recruitmentService.postRecruitment(request);
  }

  //모집중인 모든 공고 리스트
  @GetMapping("/recruitments")
  public List<RecruitmentDto.Response> getRecruitmentList() {
    return recruitmentService.getRecruitmentList();
  }

  //특정 아이디의 공고 확인
  @GetMapping("/recruitments/{id}")
  public RecruitmentDto.Response getRecruitment(@PathVariable Long id) {
    return recruitmentService.getRecruitment(id);
  }

  //특정 아이디의 공고 정보를 변경
  @PutMapping("/recruitments/{id}")
  public RecruitmentDto.Response getRecruitment(@PathVariable Long id,
      @RequestBody RecruitmentDto.Request request) {
    return recruitmentService.modifyRecruitment(id, request);
  }

  //특정 아이디의 공고를 삭제
  @DeleteMapping("/recruitments/{id}")
  public String deleteRecruitment(@PathVariable Long id, @RequestParam String companyLoginId) {
    recruitmentService.deleteRecruitment(id, companyLoginId);
    return "삭제 완료";
  }

  //지원자가 공고 아이디와 이력서 아이디를 통해 지원 접수
  @PostMapping("/recruitments/{id}/applications")
  public Long applyRecruitment(@PathVariable(name = "id") Long recruitmentId,
      @RequestBody ApplicationDto.Request request) {
    return recruitmentService.apply(recruitmentId, request);
  }

  //기업이 특정 공고에 달린 모든 지원 접수를 확인
  @GetMapping("/recruitments/{id}/applications")
  public List<ResumeDto.Response> getApplicant(@PathVariable(name = "id") Long recruitmentId,
      @RequestParam String companyLoginId) {
    return recruitmentService.getApplicantResume(recruitmentId, companyLoginId);
  }

  //기업이 특정 공고의 모집을 종료함
  @PutMapping("/recruitments/{id}/quit")
  public String quitRecruitment(@PathVariable Long id, @RequestParam String companyLoginId) {
    recruitmentService.quitRecruitment(id, companyLoginId);
    return "모집 종료됨";
  }

  //기업이 모집 종료된 공고 아이디를 넣으면 점수를 계산하여 채용인원만큼 자동으로 선발해준다.
  @PutMapping("/recruitments/{id}/pick")
  public List<ResumeDto.Response> autoPickApplicant(@PathVariable Long id,
      @RequestParam String companyLoginId) {
    return recruitmentService.autoPickRecruitment(id, companyLoginId);
  }

  //지원자가 공고 아이디를 넣어 자신이 합격했는지 여부를 확인한다.
  @GetMapping("/recruitments/{id}/check")
  public String checkPassOrFail(@PathVariable Long id, @RequestParam String memberLoginId) {
    return recruitmentService.checkPassOrFail(id, memberLoginId);
  }

}
