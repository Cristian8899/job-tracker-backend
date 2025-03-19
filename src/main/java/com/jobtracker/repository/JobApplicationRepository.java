package com.jobtracker.repository;

import com.jobtracker.model.JobApplication;
import com.jobtracker.model.JobStatus;
import com.jobtracker.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {

    Page<JobApplication> findByApplicant(User applicant, Pageable pageable);

    Page<JobApplication> findByApplicantAndStatus(User applicant, JobStatus status, Pageable pageable);

    long countByApplicant(User applicant);

    long countByApplicantAndStatus(User applicant, JobStatus status);

    Optional<JobApplication> findByIdAndApplicant(UUID id, User applicant);
}
