package com.revhire.service;

import com.revhire.dao.ApplicationDAO;
import com.revhire.dao.JobDAO;
import com.revhire.model.Job;

import java.util.List;

public class JobSeekerService {
    private final JobDAO jobDAO = new JobDAO();
    private final ApplicationDAO appDAO = new ApplicationDAO();

    public List<Job> search(String title, String location, Integer exp, String company, Long salMin, String jobType) {
        return jobDAO.searchJobs(title, location, exp, company, salMin, jobType);
    }

    public boolean apply(long jobId, long userId, String coverLetter) {
        return appDAO.apply(jobId, userId, coverLetter);
    }

    public boolean withdraw(long jobId, long userId, String reason) {
        return appDAO.withdraw(jobId, userId, reason);
    }

    public List<String> myApplications(long userId) {
        return appDAO.myApplications(userId);
    }
}
