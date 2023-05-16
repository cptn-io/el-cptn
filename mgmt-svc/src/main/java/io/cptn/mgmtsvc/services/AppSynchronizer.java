package io.cptn.mgmtsvc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import io.cptn.common.entities.App;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.util.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* @author: kc, created on 4/27/23 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AppSynchronizer {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private final AppService appService;

    @Value("${app.repo.url:https://github.com/cptn-io/apps.git}")
    private String repoUrl;


    @Value("${app.repo.localpath:/tmp/apps}")
    private String localPath;

    @Value("${app.repo.branch:main}")
    private String branch;

    private static App readJsonFile(File file) throws IOException {
        return objectMapper.readValue(file, App.class);
    }

    private List<App> processAppContent() {
        List<App> appPojos = Lists.newArrayList();

        File folder = new File(localPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
            if (files != null) {
                List<File> fileList = Arrays.asList(files);
                Collections.sort(fileList, Collections.reverseOrder());
                for (File file : fileList) {
                    try {
                        App pojo = readJsonFile(file);
                        if (pojo != null) {
                            appPojos.add(pojo);
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }

        return appPojos;
    }

    private void cloneRepository() {
        try (Git git = Git.cloneRepository()
                .setURI(repoUrl).setBranch(branch)
                .setDirectory(new File(localPath))
                .call()) {
            log.info("Repository cloned successfully!");
        } catch (GitAPIException e) {
            log.error("Error cloning repository: " + e.getMessage(), e);
        }
    }

    public void syncWithRepository() {
        try {
            File localLocation = new File(localPath);
            if (localLocation.exists()) {
                FileUtils.delete(localLocation, FileUtils.RECURSIVE);
            }
        } catch (IOException e) {
            log.error("Error deleting local repository: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
        this.cloneRepository();
        List<App> appContent = this.processAppContent();
        appContent.forEach(appService::upsertApp);
    }
}
