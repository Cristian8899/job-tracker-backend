package com.jobtracker.service;

import com.jobtracker.model.JobApplication;
import com.jobtracker.model.JobStatus;
import com.jobtracker.model.Tag;
import com.jobtracker.model.User;
import com.jobtracker.repository.JobApplicationRepository;
import com.jobtracker.repository.TagRepository;
import com.jobtracker.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    public JobApplicationService(JobApplicationRepository jobApplicationRepository, UserRepository userRepository, TagRepository tagRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }

    /** ✅ Get all job applications for a specific user (with filters, pagination, sorting) */
    public Map<String, Object> getUserJobApplications(UUID userId, int page, int pageSize, String status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Sorting by application date (latest first)
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "applicationDate"));
        Page<JobApplication> jobApplications;

        // ✅ Convert String status to JobStatus Enum
        JobStatus jobStatus = null;
        if (status != null && !status.isEmpty()) {
            try {
                jobStatus = JobStatus.valueOf(status.toUpperCase());  // Convert String to Enum
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid status value: " + status);
            }
        }

        if (jobStatus != null) {
            jobApplications = jobApplicationRepository.findByApplicantAndStatus(user, jobStatus, pageRequest);
        } else {
            jobApplications = jobApplicationRepository.findByApplicant(user, pageRequest);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("jobApplications", jobApplications.getContent());
        response.put("count", jobApplications.getTotalElements());

        return response;
    }

    /** ✅ Get a single job application by ID */
    public Optional<JobApplication> getUserJobApplication(UUID userId, UUID jobId) {
        return jobApplicationRepository.findByIdAndApplicant(jobId, userRepository.findById(userId).orElse(null));
    }

    /** ✅ Create a new job application */
    @Transactional
    public JobApplication createJobApplication(UUID userId, JobApplication jobApplication) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        jobApplication.setApplicant(user);

        // Validate existing tags
        List<UUID> tagIds = jobApplication.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toList());
        List<Tag> existingTags = tagRepository.findAllById(tagIds);

        if (existingTags.size() != tagIds.size()) {
            throw new RuntimeException("One or more tags do not exist.");
        }

        jobApplication.setTags(existingTags);
        return jobApplicationRepository.save(jobApplication);
    }

    /** ✅ Update a job application */
    @Transactional
    public Optional<JobApplication> updateJobApplication(UUID userId, UUID jobId, JobApplication updatedJob) {
        Optional<JobApplication> jobApplicationOptional = jobApplicationRepository.findByIdAndApplicant(
                jobId, userRepository.findById(userId).orElse(null)
        );

        if (jobApplicationOptional.isEmpty()) {
            return Optional.empty();
        }

        JobApplication existingJob = jobApplicationOptional.get();
        existingJob.setCompanyName(updatedJob.getCompanyName());
        existingJob.setJobTitle(updatedJob.getJobTitle());
        existingJob.setJobPostingURL(updatedJob.getJobPostingURL());
        existingJob.setStatus(updatedJob.getStatus());
        existingJob.setSalaryRange(updatedJob.getSalaryRange());
        existingJob.setJobLocation(updatedJob.getJobLocation());
        existingJob.setNotes(updatedJob.getNotes());

        // Validate & update tags
        List<UUID> tagIds = updatedJob.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toList());
        List<Tag> existingTags = tagRepository.findAllById(tagIds);

        if (existingTags.size() != tagIds.size()) {
            throw new RuntimeException("One or more tags do not exist.");
        }

        existingJob.setTags(existingTags);
        return Optional.of(jobApplicationRepository.save(existingJob));
    }

    /** ✅ Delete a job application */
    public boolean deleteJobApplication(UUID userId, UUID jobId) {
        Optional<JobApplication> jobApplicationOptional = jobApplicationRepository.findByIdAndApplicant(
                jobId, userRepository.findById(userId).orElse(null)
        );

        if (jobApplicationOptional.isEmpty()) {
            return false;
        }

        jobApplicationRepository.delete(jobApplicationOptional.get());
        return true;
    }
}
