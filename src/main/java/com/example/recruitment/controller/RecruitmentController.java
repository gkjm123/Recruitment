package com.example.recruitment.controller;

import com.example.recruitment.dto.ApplicationDto;
import com.example.recruitment.dto.RecruitmentDto;
import com.example.recruitment.dto.ResumeDto;
import com.example.recruitment.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    @PostMapping("/recruitments")
    public Long postingRecruitment(@RequestBody RecruitmentDto.Request request) {
        return recruitmentService.postRecruitment(request);
    }

    @GetMapping("/recruitments")
    public List<RecruitmentDto.Response> getRecruitmentList() {
        return recruitmentService.getRecruitmentList();
    }

    @GetMapping("/recruitments/{id}")
    public RecruitmentDto.Response getRecruitment(@PathVariable Long id) {
        return recruitmentService.getRecruitment(id);
    }

    @PutMapping("/recruitments/{id}")
    public RecruitmentDto.Response getRecruitment(@PathVariable Long id, @RequestBody RecruitmentDto.Request request) {
        return recruitmentService.modifyRecruitment(id, request);
    }

    @DeleteMapping("/recruitments/{id}")
    public String deleteRecruitment(@PathVariable Long id, @RequestParam String companyLoginId) {
        recruitmentService.deleteRecruitment(id, companyLoginId);
        return "삭제 완료";
    }

    @PostMapping("/recruitments/{id}/applications")
    public Long applyRecruitment(@PathVariable(name = "id") Long recruitmentId,
                                 @RequestBody ApplicationDto.Request request) {
        return recruitmentService.apply(recruitmentId, request);
    }

    @GetMapping("/recruitments/{id}/applications")
    public List<ResumeDto.Response> getApplicant(@PathVariable(name = "id") Long recruitmentId,
                                                 @RequestParam String companyLoginId) {
        return recruitmentService.getApplicantResume(recruitmentId, companyLoginId);
    }

    @PutMapping("/recruitments/{id}/quit")
    public String quitRecruitment(@PathVariable Long id, @RequestParam String companyLoginId) {
        recruitmentService.quitRecruitment(id, companyLoginId);
        return "모집 종료됨";
    }

    @PutMapping("/recruitments/{id}/pick")
    public List<ResumeDto.Response> autoPickApplicant(@PathVariable Long id, @RequestParam String companyLoginId) {
        return recruitmentService.autoPickRecruitment(id, companyLoginId);
    }

    @GetMapping("/recruitments/{id}/check")
    public String checkPassOrFail(@PathVariable Long id, @RequestParam String memberLoginId) {
        return recruitmentService.checkPassOrFail(id, memberLoginId);
    }

}
