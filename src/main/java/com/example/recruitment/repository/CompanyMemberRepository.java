package com.example.recruitment.repository;

import com.example.recruitment.entity.CompanyMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyMemberRepository extends JpaRepository<CompanyMember, Long> {

  Optional<CompanyMember> findByLoginId(String loginId);
}
