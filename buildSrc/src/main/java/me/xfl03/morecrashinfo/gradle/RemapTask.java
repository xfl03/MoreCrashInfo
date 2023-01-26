package me.xfl03.morecrashinfo.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.util.Arrays;

public class RemapTask extends DefaultTask {
    public Project rootProject;
    public JarRemapper remapper = new JarRemapper();

    @TaskAction
    public void run() {
        File loaderLibs = rootProject.file("2Loader/build/libs");
        File[] files = loaderLibs.listFiles();
        if (files == null) {
            System.err.println("Cannot find build libs.");
            return;
        }
        Arrays.stream(files).forEach(this::remap);
    }

    private void remap(File inFile) {
        File rootLibs = rootProject.file("build/libs");
        remapper.processJar(inFile.toPath(), rootLibs.toPath().resolve(inFile.getName()));
    }
}
