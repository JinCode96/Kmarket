package com.kmarket.repository.member;

import com.kmarket.entity.TermsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermsJpaRepository extends JpaRepository<TermsEntity, Integer> {
}
