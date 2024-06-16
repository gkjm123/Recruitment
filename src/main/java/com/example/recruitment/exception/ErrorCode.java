package com.example.recruitment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    RECRUITMENT_NOT_FOUND("채용공고를 찾을수 없습니다."),
    RESUME_NOT_FOUND("이력서를 찾을수 없습니다."),
    COMPANY_NOT_FOUND("기업회원 정보를 찾을수 없습니다."),
    MEMBER_NOT_FOUND("개인회원 정보를 찾을수 없습니다."),
    COMPANY_NOT_MATCH("기업회원 정보가 일치하지 않습니다."),
    MEMBER_NOT_MATCH("개인회원 정보가 일치하지 않습니다."),
    QUITED_RECRUITMENT("채용공고의 모집이 종료되었습니다."),
    NO_RECRUITMENT("등록된 이력서가 없습니다."),
    NOT_QUITED_RECRUITMENT("아직 종료되지 않은 공고입니다."),
    CONVERT_ERROR("JSON 타입변환 오류");

    private final String message;

}
