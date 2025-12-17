package com.seowon.coding.domain.repository;

import com.seowon.coding.domain.model.ProcessingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessingStatusRepository extends JpaRepository<ProcessingStatus, Long> {
    Optional<ProcessingStatus> findByJobId(String jobId);
}
