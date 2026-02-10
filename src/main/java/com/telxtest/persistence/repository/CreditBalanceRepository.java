package com.telxtest.persistence.repository;

import com.telxtest.persistence.entity.CreditBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditBalanceRepository extends JpaRepository<CreditBalanceEntity, Long> {
}
