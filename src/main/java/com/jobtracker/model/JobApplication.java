package com.jobtracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "job_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String jobTitle;

    private String jobPostingURL;

    @Temporal(TemporalType.TIMESTAMP)
    private Date applicationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.APPLIED;

    private String salaryRange;
    private String jobLocation;
    private String notes;

    @ManyToOne
    @JoinColumn(name = "applicant_id", nullable = false)
    @JsonIgnoreProperties({"jobApplications", "password", "token", "expiry"})
    private User applicant;

    @ManyToMany
    @JoinTable(
            name = "job_application_tags",
            joinColumns = @JoinColumn(name = "job_application_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @JsonIgnoreProperties("jobApplications")
    private Set<Tag> tags = new HashSet<>();


    @JsonProperty("applicantId")
    public UUID getApplicantId() {
        return this.applicant != null ? this.applicant.getId() : null;
    }

    public List<Tag> getTags() {
        return new ArrayList<>(tags);
    }

    public void setTags(List<Tag> tags) {
        this.tags = new HashSet<>(tags);
    }

    @PrePersist
    protected void onCreate() {
        if (applicationDate == null) {
            applicationDate = new Date();
        }
    }
}
