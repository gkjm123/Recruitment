package com.example.recruitment.repository;

import com.example.recruitment.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findAllByMember_LoginId(String memberLoginId);
    Optional<Resume> findByMember_LoginIdAndId(String loginId, Long id);
}
