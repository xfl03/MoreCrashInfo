package me.xfl03.morecrashinfo.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GradlePlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        //Is not root project
        if (!project.getRootProject().equals(project)) {
            return;
        }

        //Only apply to root project
        project.getTasks().create("remap", RemapTask.class, task -> task.rootProject = project.getRootProject());
    }
}
