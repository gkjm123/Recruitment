package com.example.recruitment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Education {
    private String school;
    private Integer degree; //고등학교:0, 학사:1, 석사:2, 박사:3
}