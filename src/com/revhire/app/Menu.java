package com.revhire.app;

import com.revhire.dao.*;
import com.revhire.exceptionhandler.ExceptionHandler;
import com.revhire.model.*;
import com.revhire.service.*;
import com.revhire.util.InputUtil;

import java.util.List;

public class Menu {

    private final AuthService auth = new AuthService();
    private final JobSeekerService js = new JobSeekerService();
    private final EmployerService emp = new EmployerService();
    private final ResumeDAO resumeDAO = new ResumeDAO();
    private final NotificationDAO notifDAO = new NotificationDAO();
    private final ProfileService profileService = new ProfileService();

    public void start() {
        while (true) {
            try {
                System.out.println("\nWelcome To RevHire - Platform to achieve your goals");
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
            long id = auth.register(
                    InputUtil.s("Full Name"),
                    InputUtil.email("Email"),
                    InputUtil.password("Password"),
                    role,
                    InputUtil.s("Security Question"),
                    InputUtil.s("Security Answer")
            );
            System.out.println("Registered USER_ID=" + id);
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void login() {
        try {
            User u = auth.login(
                    InputUtil.email("Email"),
                    InputUtil.password("Password")
            );

            if (u == null) {
                System.out.println("Login failed");
                return;
            }

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
            boolean ok = auth.reset(
                    email,
                    InputUtil.s("Answer"),
                    InputUtil.password("New Password")
            );

            System.out.println(ok ? "Password updated" : "Reset failed");
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void jobSeekerMenu(User u) {
        while (true) {
            try {
                System.out.println("\n----- JobSeeker -----");
                System.out.println("Profile completion: " +
                        profileService.completionPercent(u.getUserId()) + "%");

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
                System.out.println("\n---- Resume ----");
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
                } else {
                    resumeDAO.upsertResume(
                            u.getUserId(),
                            InputUtil.s("Objective"),
                            InputUtil.s("Education"),
                            InputUtil.s("Experience"),
                            InputUtil.s("Skills"),
                            InputUtil.s("Projects"),
                            InputUtil.s("Certifications")
                    );
                    System.out.println("Resume updated");
                }
            } catch (Exception e) {
                ExceptionHandler.handle(e);
            }
        }
    }

    private void searchJobs() {
        try {
            List<Job> jobs = js.search(
                    InputUtil.s("Job role/title (empty ok)"),
                    InputUtil.s("Location (empty ok)"),
                    null,
                    InputUtil.s("Company name (empty ok)"),
                    null,
                    InputUtil.s("Job type (empty ok)")
            );

            if (jobs.isEmpty()) {
                System.out.println("No jobs found");
                return;
            }

            for (Job j : jobs) {
                System.out.println("JOB_ID=" + j.getJobId() + " | " +
                        j.getCompanyName() + " | " +
                        j.getTitle() + " | " +
                        j.getLocation());
            }
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void applyJob(User u) {
        try {
            boolean ok = js.apply(
                    InputUtil.l("JOB_ID"),
                    u.getUserId(),
                    InputUtil.s("Cover letter (empty ok)")
            );
            System.out.println(ok ? "Applied" : "Apply failed");
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void myApps(User u) {
        try {
            List<String> list = js.myApplications(u.getUserId());
            if (list.isEmpty()) System.out.println("No applications");
            else {
                for (String item : list) {
                    System.out.println(item);
                }
            }
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void withdraw(User u) {
        try {
            boolean ok = js.withdraw(
                    InputUtil.l("JOB_ID to withdraw"),
                    u.getUserId(),
                    InputUtil.s("Reason (empty ok)")
            );
            System.out.println(ok ? "Withdrawn" : "Withdraw failed");
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

            for (String s : list) {
                System.out.println(s);
            }

            long id = InputUtil.l("Enter NOTIF_ID to mark read (0 skip)");
            if (id != 0)
                System.out.println(
                        notifDAO.markRead(id, u.getUserId())
                                ? "Marked read" : "Failed"
                );
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void changePassword(User u) {
        try {
            boolean ok = auth.changePassword(
                    u.getUserId(),
                    InputUtil.password("Current password"),
                    InputUtil.password("New password")
            );
            System.out.println(ok ? "Password changed" : "Wrong password");
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void employerMenu(User u) {
        Long companyId = emp.companyId(u.getUserId());
        if (companyId == null) {
            System.out.println("Create company profile first");
            createOrUpdateCompany(u);
        }

        while (true) {
            try {
                System.out.println("\n---- Employer ----");
                System.out.println("1 Company Profile");
                System.out.println("2 Post Job");
                System.out.println("3 Close Job");
                System.out.println("4 Reopen Job");
                System.out.println("5 Delete Job");
                System.out.println("6 View Applicants");
                System.out.println("7 Update Applicant Status");
                System.out.println("8 Bulk Status Update");
                System.out.println("9 Change Password");
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
            long id = emp.upsertCompany(
                    u.getUserId(),
                    InputUtil.s("Company name"),
                    InputUtil.s("Industry"),
                    InputUtil.s("Size range"),
                    InputUtil.s("Description"),
                    InputUtil.s("Website"),
                    InputUtil.s("Location")
            );
            System.out.println("Company saved COMPANY_ID=" + id);
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

            long jobId = emp.postJob(
                    cid,
                    InputUtil.s("Title"),
                    InputUtil.s("Description"),
                    InputUtil.s("Skills"),
                    InputUtil.i("Min exp years"),
                    InputUtil.s("Education"),
                    InputUtil.s("Location"),
                    null,
                    null,
                    InputUtil.s("Job type"),
                    null
            );
            System.out.println("Posted JOB_ID=" + jobId);
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void jobStatus(User u, String status) {
        try {
            Long cid = emp.companyId(u.getUserId());
            long jobId = InputUtil.l("JOB_ID");

            boolean ok = "OPEN".equals(status)
                    ? emp.reopenJob(jobId, cid)
                    : emp.closeJob(jobId, cid);

            System.out.println(ok ? "Updated" : "Failed");
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void deleteJob(User u) {
        try {
            Long cid = emp.companyId(u.getUserId());
            boolean ok = emp.deleteJob(InputUtil.l("JOB_ID"), cid);
            System.out.println(ok ? "Deleted" : "Failed");
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void viewApplicants(User u) {
        try {
            List<String> list = emp.applicants(
                    emp.companyId(u.getUserId()),
                    InputUtil.l("JOB_ID"),
                    null,
                    InputUtil.s("Skill keyword filter"),
                    null,
                    null
            );
            if (list.isEmpty()) System.out.println("No applicants");
            else {
                for (String s : list) {
                    System.out.println(s);
                }
            }
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void updateApplicant(User u) {
        try {
            boolean ok = emp.updateOne(
                    InputUtil.l("APP_ID"),
                    InputUtil.statusShortlistReject("Status"),
                    InputUtil.s("Comment"),
                    InputUtil.l("Applicant USER_ID"),
                    "Application status updated"
            );
            System.out.println(ok ? "Updated" : "Failed");
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }

    private void bulkUpdate() {
        try {
            String[] parts = InputUtil.s("APP_IDs comma separated").split(",");
            long[] ids = new long[parts.length];
            for (int i = 0; i < parts.length; i++)
                ids[i] = Long.parseLong(parts[i].trim());

            int updated = emp.bulk(
                    ids,
                    InputUtil.statusShortlistReject("Status"),
                    InputUtil.s("Comment")
            );
            System.out.println("Updated count=" + updated);
        } catch (Exception e) {
            ExceptionHandler.handle(e);
        }
    }
}
