package com.telxtest.persistence.repository;

import com.telxtest.persistence.entity.CallRecordEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CallRecordRepository extends JpaRepository<CallRecordEntity, Long> {
    List<CallRecordEntity> findAllByOrderByStartedAtDesc();
}
