package com.revhire.service;

import com.revhire.dao.ResumeDAO;

public class ProfileService {
    private final ResumeDAO resumeDAO = new ResumeDAO();

    public int completionPercent(long userId) {
        String[] r = resumeDAO.getResume(userId);
        if (r == null) return 0;
        int total = 6;
        int done = 0;
        for (int i = 0; i < r.length; i++) {
            if (r[i] != null && r[i].trim().length() > 0) done++;
        }
        return (done * 100) / total;
    }
}
