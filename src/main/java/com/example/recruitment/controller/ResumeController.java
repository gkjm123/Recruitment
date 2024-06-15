package com.example.recruitment.controller;

import com.example.recruitment.dto.ResumeDto;
import com.example.recruitment.dto.ResumeDto;
import com.example.recruitment.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping("/resumes")
    public Long postingResume(@RequestBody ResumeDto.Request request) {
        return resumeService.postResume(request);
    }

    @GetMapping("/resumes")
    public List<ResumeDto.Response> getResumeList(@RequestParam String memberLoginId) {
        return resumeService.getResumeList(memberLoginId);
    }

    @GetMapping("/resumes/{id}")
    public ResumeDto.Response getResume(@PathVariable Long id) {
        return resumeService.getResume(id);
    }

    @PutMapping("/resumes/{id}")
    public ResumeDto.Response getResume(@PathVariable Long id, @RequestBody ResumeDto.Request request) {
        return resumeService.modifyResume(id, request);
    }

    @DeleteMapping("/resumes/{id}")
    public String deleteResume(@PathVariable Long id, @RequestParam String memberLoginId) {
        resumeService.deleteResume(id, memberLoginId);
        return "삭제 완료";
    }
}
