package com.revhire.model;

import java.sql.Date;

public class Job {
    private long jobId;
    private String companyName;
    private String title;
    private String location;
    private String skills;
    private int minExp;
    private String jobType;
    private Long salaryMin;
    private Long salaryMax;
    private Date deadline;

    public long getJobId() { return jobId; }
    public void setJobId(long jobId) { this.jobId = jobId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    public int getMinExp() { return minExp; }
    public void setMinExp(int minExp) { this.minExp = minExp; }
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    public Long getSalaryMin() { return salaryMin; }
    public void setSalaryMin(Long salaryMin) { this.salaryMin = salaryMin; }
    public Long getSalaryMax() { return salaryMax; }
    public void setSalaryMax(Long salaryMax) { this.salaryMax = salaryMax; }
    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }
}
