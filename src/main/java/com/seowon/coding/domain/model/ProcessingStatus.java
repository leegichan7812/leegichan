package com.seowon.coding.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String jobId;

    private int total;
    private int processed;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime updatedAt;

    public enum Status {
        RUNNING, COMPLETED, FAILED
    }

    public void markRunning(int total) {
        this.total = total;
        this.status = Status.RUNNING;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateProgress(int processed, int total) {
        this.processed = processed;
        this.total = total;
        this.updatedAt = LocalDateTime.now();
    }

    public void markCompleted() {
        this.status = Status.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.status = Status.FAILED;
        this.updatedAt = LocalDateTime.now();
    }
}
