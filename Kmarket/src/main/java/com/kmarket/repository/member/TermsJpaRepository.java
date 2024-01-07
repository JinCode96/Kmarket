package com.kmarket.repository.member;

import com.kmarket.domain.Terms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermsJpaRepository extends JpaRepository<Terms, Integer> {
}
