package com.revhire.service;

import com.revhire.dao.ApplicationDAO;
import com.revhire.dao.CompanyDAO;
import com.revhire.dao.JobDAO;

import java.sql.Date;
import java.util.List;

public class EmployerService {
    private final CompanyDAO companyDAO = new CompanyDAO();
    private final JobDAO jobDAO = new JobDAO();
    private final ApplicationDAO appDAO = new ApplicationDAO();

    public long upsertCompany(long userId, String name, String industry, String size, String desc, String website, String location) {
        return companyDAO.upsertCompany(userId, name, industry, size, desc, website, location);
    }

    public Long companyId(long userId) {
        return companyDAO.getCompanyIdByUser(userId);
    }

    public long postJob(long companyId, String title, String desc, String skills, int minExp, String education,
                        String location, Long salMin, Long salMax, String jobType, Date deadline) {
        return jobDAO.createJob(companyId, title, desc, skills, minExp, education, location, salMin, salMax, jobType, deadline);
    }

    public boolean closeJob(long jobId, long companyId) {
        return jobDAO.setJobStatus(jobId, companyId, "CLOSED");
    }

    public boolean reopenJob(long jobId, long companyId) {
        return jobDAO.setJobStatus(jobId, companyId, "OPEN");
    }

    public boolean deleteJob(long jobId, long companyId) {
        return jobDAO.deleteJob(jobId, companyId);
    }

    public List<String> applicants(long companyId, long jobId, Integer minExp, String skills, Date from, Date to) {
        return appDAO.applicantsForJob(companyId, jobId, minExp, skills, from, to);
    }

    public boolean updateOne(long appId, String status, String comment, long applicantUserId, String notifMsg) {
        return appDAO.updateStatusWithNotification(appId, status, comment, applicantUserId, notifMsg);
    }

    public int bulk(long[] appIds, String status, String comment) {
        return appDAO.bulkUpdateStatus(appIds, status, comment);
    }
}
