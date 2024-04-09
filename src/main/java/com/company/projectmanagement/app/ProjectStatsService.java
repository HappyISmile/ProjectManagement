package com.company.projectmanagement.app;

import com.company.projectmanagement.entity.ProjectStats;

import java.util.List;

public interface ProjectStatsService {
    List<ProjectStats> fetchProjectStatistics();
}
