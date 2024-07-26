package com.example.recruitment.repository;

import com.example.recruitment.entity.Recruitment;
import com.example.recruitment.type.RecruitmentStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

  List<Recruitment> findAllByStatus(RecruitmentStatus status);
}
