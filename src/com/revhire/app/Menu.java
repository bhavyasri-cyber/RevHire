package com.revhire.app;

import com.revhire.dao.*;
import com.revhire.exceptionhandler.ExceptionHandler;
import com.revhire.model.*;
import com.revhire.service.*;
import com.revhire.util.InputUtil;
import org.apache.log4j.Logger;

import java.sql.Date;
import java.util.List;

public class Menu {
    private static final Logger log = Logger.getLogger(Menu.class);
    private final AuthService auth = new AuthService();
    private final JobSeekerService js = new JobSeekerService();
    private final EmployerService emp = new EmployerService();
    private final ResumeDAO resumeDAO = new ResumeDAO();
    private final NotificationDAO notifDAO = new NotificationDAO();
    private final ProfileService profileService = new ProfileService();

    public void start() {
        while (true) {
            try {
                System.out.println("\nWelcome To RevHire -Platform to achieve your goals");
                System.out.println("1 Register JobSeeker");
                System.out.println("2 Register Employer");
                System.out.println("3 Login");
                System.out.println("4 Forgot Password");
                System.out.println("0 Exit");
                int ch = InputUtil.i("Choose");
                if (ch == 0) return;
                if (ch == 1) register("JOBSEEKER");
                else if (ch == 2) register("EMPLOYER");
                else if (ch == 3) login();
                else if (ch == 4) forgot();
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        }
    }

    private void register(String role) {
        try {
            String name = InputUtil.s("Full Name");
            String email = InputUtil.email("Email");
            String pass = InputUtil.password("Password");
            String q = InputUtil.s("Security Question");
            String a = InputUtil.s("Security Answer");
            long id = auth.register(name, email, pass, role, q, a);
            System.out.println("Registered USER_ID=" + id);
            log.info("Registered " + role + " " + email);
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void login() {
        try {
            String email = InputUtil.email("Email");
            String pass = InputUtil.password("Password");
            User u = auth.login(email, pass);
            if (u == null) {
                System.out.println("Login failed");
                return;
            }
            log.info("Login " + u.getEmail() + " " + u.getRole());
            if ("JOBSEEKER".equals(u.getRole())) jobSeekerMenu(u);
            else employerMenu(u);
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void forgot() {
        try {
            String email = InputUtil.email("Email");
            String q = auth.securityQ(email);
            if (q == null) {
                System.out.println("No user found");
                return;
            }
            System.out.println("Security Question: " + q);
            String a = InputUtil.s("Answer");
            String np = InputUtil.password("New Password");
            boolean ok = auth.reset(email, a, np);
            System.out.println(ok ? "Password updated" : "Reset failed");
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void jobSeekerMenu(User u) {
        while (true) {
            try {
                System.out.println("\n-----JobSeeker ----");
                System.out.println("Profile completion: " + profileService.completionPercent(u.getUserId()) + "%");
                System.out.println("1 Manage Resume");
                System.out.println("2 Search Jobs");
                System.out.println("3 Apply Job");
                System.out.println("4 My Applications");
                System.out.println("5 Withdraw Application");
                System.out.println("6 Notifications");
                System.out.println("7 Change Password");
                System.out.println("0 Logout");
                int ch = InputUtil.i("Choose");
                if (ch == 0) return;
                if (ch == 1) resumeMenu(u);
                else if (ch == 2) searchJobs();
                else if (ch == 3) applyJob(u);
                else if (ch == 4) myApps(u);
                else if (ch == 5) withdraw(u);
                else if (ch == 6) notifications(u);
                else if (ch == 7) changePassword(u);
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        }
    }

    private void resumeMenu(User u) {
        while (true) {
            try {
                System.out.println("\n----Resume ----");
                System.out.println("1 View Resume");
                System.out.println("2 Update Resume");
                System.out.println("0 Back");
                int ch = InputUtil.i("Choose");
                if (ch == 0) return;
                if (ch == 1) {
                    String[] r = resumeDAO.getResume(u.getUserId());
                    if (r == null) System.out.println("No resume yet");
                    else {
                        System.out.println("Objective: " + r[0]);
                        System.out.println("Education: " + r[1]);
                        System.out.println("Experience: " + r[2]);
                        System.out.println("Skills: " + r[3]);
                        System.out.println("Projects: " + r[4]);
                        System.out.println("Certifications: " + r[5]);
                    }
                } else if (ch == 2) {
                    String obj = InputUtil.s("Objective");
                    String edu = InputUtil.s("Education");
                    String exp = InputUtil.s("Experience");
                    String skills = InputUtil.s("Skills");
                    String proj = InputUtil.s("Projects");
                    String cert = InputUtil.s("Certifications");
                    resumeDAO.upsertResume(u.getUserId(), obj, edu, exp, skills, proj, cert);
                    System.out.println("Resume updated");
                    log.info("Resume updated " + u.getEmail());
                }
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        }
    }

    private void searchJobs() {
        try {
            String title = InputUtil.s("Job role/title (empty ok)");
            String loc = InputUtil.s("Location (empty ok)");
            String expS = InputUtil.s("Your experience years (empty ok)");
            Integer exp = (expS.length() == 0) ? null : Integer.valueOf(expS);
            String company = InputUtil.s("Company name (empty ok)");
            String salS = InputUtil.s("Min salary (empty ok)");
            Long salMin = (salS.length() == 0) ? null : Long.valueOf(salS);
            String jobType = InputUtil.s("Job type (empty ok)");
            List<Job> jobs = js.search(title, loc, exp, company, salMin, jobType);
            if (jobs.isEmpty()) {
                System.out.println("No jobs found");
                return;
            }
            for (int i = 0; i < jobs.size(); i++) {
                Job j = jobs.get(i);
                System.out.println("JOB_ID=" + j.getJobId() + " | " + j.getCompanyName() + " | " + j.getTitle() +
                        " | " + j.getLocation() + " | exp<=" + j.getMinExp() +
                        " | " + j.getJobType() + " | skills=" + j.getSkills());
            }
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void applyJob(User u) {
        try {
            long jobId = InputUtil.l("JOB_ID");
            String cl = InputUtil.s("Cover letter (empty ok)");
            boolean ok = js.apply(jobId, u.getUserId(), cl.length() == 0 ? null : cl);
            System.out.println(ok ? "Applied" : "Apply failed (already applied?)");
            if (ok) log.info("Applied user=" + u.getEmail() + " jobId=" + jobId);
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void myApps(User u) {
        try {
            List<String> list = js.myApplications(u.getUserId());
            if (list.isEmpty()) System.out.println("No applications");
            else for (int i = 0; i < list.size(); i++) System.out.println(list.get(i));
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void withdraw(User u) {
        try {
            long jobId = InputUtil.l("JOB_ID to withdraw");
            String reason = InputUtil.s("Reason (empty ok)");
            boolean ok = js.withdraw(jobId, u.getUserId(), reason);
            System.out.println(ok ? "Withdrawn" : "Withdraw failed");
            if (ok) log.info("Withdraw user=" + u.getEmail() + " jobId=" + jobId);
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void notifications(User u) {
        try {
            List<String> list = notifDAO.unread(u.getUserId());
            if (list.isEmpty()) {
                System.out.println("No notifications");
                return;
            }
            for (int i = 0; i < list.size(); i++) System.out.println(list.get(i));
            long id = InputUtil.l("Enter NOTIF_ID to mark read (0 skip)");
            if (id != 0) {
                boolean ok = notifDAO.markRead(id, u.getUserId());
                System.out.println(ok ? "Marked read" : "Failed");
            }
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void changePassword(User u) {
        try {
            String cur = InputUtil.password("Current password");
            String np = InputUtil.password("New password");
            boolean ok = auth.changePassword(u.getUserId(), cur, np);
            System.out.println(ok ? "Password changed" : "Wrong current password");
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void employerMenu(User u) {
        Long companyId = emp.companyId(u.getUserId());
        if (companyId == null) {
            System.out.println("Create company profile first");
            createOrUpdateCompany(u);
            companyId = emp.companyId(u.getUserId());
        }

        while (true) {
            try {
                System.out.println("\n---- Employer ---");
                System.out.println("1 Company Profile (create/update)");
                System.out.println("2 Post Job");
                System.out.println("3 Close Job");
                System.out.println("4 Reopen Job");
                System.out.println("5 Delete Job");
                System.out.println("6 View Applicants (filter)");
                System.out.println("7 Update Applicant Status (SHORTLIST/REJECT)");
                System.out.println("8 Bulk Status Update");
                System.out.println("7 Change Password");
                System.out.println("0 Logout");
                int ch = InputUtil.i("Choose");
                if (ch == 0) return;

                if (ch == 1) createOrUpdateCompany(u);
                else if (ch == 2) postJob(u);
                else if (ch == 3) jobStatus(u, "CLOSED");
                else if (ch == 4) jobStatus(u, "OPEN");
                else if (ch == 5) deleteJob(u);
                else if (ch == 6) viewApplicants(u);
                else if (ch == 7) updateApplicant(u);
                else if (ch == 8) bulkUpdate();
                else if (ch == 9) changePassword(u);

            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        }
    }

    private void createOrUpdateCompany(User u) {
        try {
            String name = InputUtil.s("Company name");
            String industry = InputUtil.s("Industry");
            String size = InputUtil.s("Size range");
            String desc = InputUtil.s("Description");
            String web = InputUtil.s("Website");
            String loc = InputUtil.s("Location");
            long id = emp.upsertCompany(u.getUserId(), name, industry, size, desc, web, loc);
            System.out.println("Company saved COMPANY_ID=" + id);
            log.info("Company upsert " + u.getEmail());
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void postJob(User u) {
        try {
            Long cid = emp.companyId(u.getUserId());
            if (cid == null) {
                System.out.println("Create company first");
                return;
            }
            String title = InputUtil.s("Title");
            String desc = InputUtil.s("Description");
            String skills = InputUtil.s("Skills");
            int minExp = InputUtil.i("Min exp years");
            String edu = InputUtil.s("Education");
            String loc = InputUtil.s("Location");
            String s1 = InputUtil.s("Salary min (empty ok)");
            String s2 = InputUtil.s("Salary max (empty ok)");
            Long salMin = s1.length() == 0 ? null : Long.valueOf(s1);
            Long salMax = s2.length() == 0 ? null : Long.valueOf(s2);
            String jobType = InputUtil.s("Job type");
            String d = InputUtil.dateEmptyOk("Deadline yyyy-mm-dd (empty ok)");
            Date dl = d.length() == 0 ? null : Date.valueOf(d);
            long jobId = emp.postJob(cid.longValue(), title, desc, skills, minExp, edu, loc, salMin, salMax, jobType, dl);
            System.out.println("Posted JOB_ID=" + jobId);
            log.info("Job posted employer=" + u.getEmail() + " jobId=" + jobId);
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void jobStatus(User u, String status) {
        try {
            Long cid = emp.companyId(u.getUserId());
            long jobId = InputUtil.l("JOB_ID");
            boolean ok = "OPEN".equals(status) ? emp.reopenJob(jobId, cid.longValue()) : emp.closeJob(jobId, cid.longValue());
            System.out.println(ok ? "Updated" : "Failed");
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void deleteJob(User u) {
        try {
            Long cid = emp.companyId(u.getUserId());
            long jobId = InputUtil.l("JOB_ID");
            boolean ok = emp.deleteJob(jobId, cid.longValue());
            System.out.println(ok ? "Deleted" : "Failed");
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void viewApplicants(User u) {
        try {
            Long cid = emp.companyId(u.getUserId());
            long jobId = InputUtil.l("JOB_ID");
            String expS = InputUtil.s("Min exp filter (empty ok)");
            Integer exp = expS.length() == 0 ? null : Integer.valueOf(expS);
            String skills = InputUtil.s("Skill keyword filter (empty ok)");
            String fromS = InputUtil.dateEmptyOk("From date yyyy-mm-dd (empty ok)");
            String toS = InputUtil.dateEmptyOk("To date yyyy-mm-dd (empty ok)");
            Date from = fromS.length() == 0 ? null : Date.valueOf(fromS);
            Date to = toS.length() == 0 ? null : Date.valueOf(toS);
            List<String> list = emp.applicants(cid.longValue(), jobId, exp, skills, from, to);
            if (list.isEmpty()) System.out.println("No applicants");
            else for (int i = 0; i < list.size(); i++) System.out.println(list.get(i));
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void updateApplicant(User u) {
        try {
            long appId = InputUtil.l("APP_ID");
            long applicantUserId = InputUtil.l("Applicant USER_ID");
            String status = InputUtil.statusShortlistReject("Status SHORTLISTED/REJECTED");
            String comment = InputUtil.s("Comment");
            String notif = "Application " + status + ". " + comment;
            boolean ok = emp.updateOne(appId, status, comment, applicantUserId, notif);
            System.out.println(ok ? "Updated" : "Failed");
            if (ok) log.info("App status updated appId=" + appId + " status=" + status);
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void bulkUpdate() {
        try {
            String ids = InputUtil.s("APP_IDs comma separated");
            String status = InputUtil.statusShortlistReject("Status SHORTLISTED/REJECTED");
            String comment = InputUtil.s("Comment");
            String[] parts = ids.split(",");
            long[] a = new long[parts.length];
            for (int i = 0; i < parts.length; i++) a[i] = Long.parseLong(parts[i].trim());
            int updated = emp.bulk(a, status, comment);
            System.out.println("Updated count=" + updated);
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }
}
