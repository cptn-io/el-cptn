package io.cptn.mgmtsvc.services;

import io.cptn.common.entities.*;
import io.cptn.common.pojos.ConfigItem;
import io.cptn.common.repositories.AppRepository;
import io.cptn.common.services.CommonService;
import io.cptn.common.web.ListEntitiesParam;
import liquibase.util.MD5Util;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/* @author: kc, created on 4/25/23 */
@Service
@RequiredArgsConstructor
public class AppService extends CommonService {

    private final AppRepository appRepository;

    private final DestinationService destinationService;

    private final TransformationService transformationService;

    public App createApp() {
        App app = new App();

        //generate random string
        String key = UUID.randomUUID().toString();
        //get md5 for the key
        String hash = MD5Util.computeMD5(key);
        app.setName(key);
        app.setHash(hash);
        app.setKey(hash);
        ConfigItem configItem = new ConfigItem();
        configItem.setKey("key1");
        configItem.setValue("value1");
        app.setConfig(List.of(configItem));
        app.setScript("//script goes here");
        return appRepository.save(app);
    }

    public void upsertApp(App app) {
        App existingApp = getAppByKey(app.getKey());
        if (existingApp != null) {
            if (app.getHash().equals(existingApp.getHash())) {
                //no updates for the app
                return;
            }
            app.setId(existingApp.getId());
        }
        appRepository.save(app);
    }

    public App getAppById(UUID id) {
        return appRepository.findById(id).orElse(null);
    }

    public App getAppByKey(String key) {
        return appRepository.findByKey(key).orElse(null);
    }

    public List<App> getAll(ListEntitiesParam param) {
        Pageable pageable = getPageable(param);

        return appRepository.findAll(pageable).stream().collect(Collectors.toList());
    }

    public long count() {
        return appRepository.count();
    }

    public ScriptedStep useApp(App app) {
        if (AppType.DESTINATION.equals(app.getType())) {
            Destination destination = new Destination();
            destination.setConfig(app.getConfig());
            destination.setName(app.getName());
            destination.setScript(app.getScript());
            destination.setActive(true);
            return destinationService.create(destination);
        } else if (AppType.TRANSFORMATION.equals(app.getType())) {
            Transformation transformation = new Transformation();
            transformation.setName(app.getName());
            transformation.setScript(app.getScript());
            transformation.setActive(true);
            return transformationService.create(transformation);
        }
        return null;
    }
}
