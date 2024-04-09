package com.company.projectmanagement.app;

import com.company.projectmanagement.entity.Project;
import com.company.projectmanagement.entity.ProjectStats;
import com.company.projectmanagement.entity.Task;
import io.jmix.core.DataManager;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Primary
@Component
@Profile("dev")
public class ProjectStatsServiceDev implements ProjectStatsService {
    private final DataManager dataManager;

    public ProjectStatsServiceDev(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public List<ProjectStats> fetchProjectStatistics() {
        List<Project> projects = dataManager.load(Project.class).all().list();

        List<ProjectStats> projectStats = projects.stream().map(project -> {
            ProjectStats stat = dataManager.create(ProjectStats.class);
            stat.setId(project.getId());
            stat.setProjectName(project.getName());
            stat.setTasksCount(project.getTasks().size());

            Integer plannedEffords = project.getTasks().stream().map(Task::getEstimation).reduce(0, Integer::sum);
            Integer actualEffords = getActualEfforts(project.getId());
            stat.setPlannedEfforts(plannedEffords);
            stat.setActualEfforts(actualEffords);
            stat.setDifference(plannedEffords - actualEffords);
            return stat;
        }).collect(Collectors.toList());
        return projectStats;
    }

    public Integer getActualEfforts(UUID projectId){
        return dataManager.loadValue("select SUM(t.timeSpent) from TimeEntry t " +
                "where t.task.project.id = :projectId", Integer.class)
                .parameter("projectId", projectId)
                .one();
    }
}