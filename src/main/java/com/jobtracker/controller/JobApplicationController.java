package com.jobtracker.controller;

import com.jobtracker.model.JobApplication;
import com.jobtracker.model.Tag;
import com.jobtracker.service.JobApplicationService;
import com.jobtracker.service.TagService;
import com.jobtracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{uid}/job-applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;
    private final UserService userService;
    private final TagService tagService;

    public JobApplicationController(JobApplicationService jobApplicationService, UserService userService, TagService tagService) {
        this.jobApplicationService = jobApplicationService;
        this.userService = userService;
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<?> getOwnJobApplications(@PathVariable UUID uid,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "10") int pageSize,
                                                   @RequestParam(required = false) String status) {
        return ResponseEntity.ok(jobApplicationService.getUserJobApplications(uid, page, pageSize, status));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<?> getOwnJobApplication(@PathVariable UUID uid, @PathVariable UUID jobId) {
        Optional<JobApplication> jobApplication = jobApplicationService.getUserJobApplication(uid, jobId);
        return jobApplication.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> postOwnJobApplication(@PathVariable UUID uid, @RequestBody JobApplication jobApplication) {
        if (jobApplication.getCompanyName() == null || jobApplication.getJobTitle() == null) {
            return ResponseEntity.badRequest().body("Company Name and Job Title are required.");
        }

        List<Tag> validTags = tagService.validateTags(jobApplication.getTags());
        jobApplication.setTags(validTags);

        JobApplication savedJob = jobApplicationService.createJobApplication(uid, jobApplication);
        return ResponseEntity.ok(savedJob);
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<?> putOwnJobApplication(@PathVariable UUID uid, @PathVariable UUID jobId, @RequestBody JobApplication jobApplication) {
        Optional<JobApplication> updatedJob = jobApplicationService.updateJobApplication(uid, jobId, jobApplication);
        return updatedJob.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<?> deleteOwnJobApplication(@PathVariable UUID uid, @PathVariable UUID jobId) {
        boolean deleted = jobApplicationService.deleteJobApplication(uid, jobId);
        return deleted ? ResponseEntity.ok("Job application deleted successfully!") : ResponseEntity.notFound().build();
    }
}
