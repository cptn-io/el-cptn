package io.cptn.mgmtsvc.services;

import io.cptn.common.entities.*;
import io.cptn.common.repositories.AppRepository;
import io.cptn.common.services.CommonService;
import io.cptn.common.web.ListEntitiesParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/* @author: kc, created on 4/25/23 */
@Service
@RequiredArgsConstructor
public class AppService extends CommonService {

    private final AppRepository appRepository;

    private final DestinationService destinationService;

    private final TransformationService transformationService;

    public void upsertApp(App app) {
        App existingApp = getAppByKey(app.getKey());
        if (existingApp != null) {
            if (app.getHash().equals(existingApp.getHash())) {
                //no updates for the app
                return;
            }
            existingApp.setConfig(app.getConfig());
            existingApp.setHash(app.getHash());
            existingApp.setName(app.getName());
            existingApp.setOrderIndex(app.getOrderIndex());
            existingApp.setScript(app.getScript());
            existingApp.setType(app.getType());
            existingApp.setLogoUrl(app.getLogoUrl());
            appRepository.save(existingApp);
        } else {
            appRepository.save(app);
        }
    }

    public App getAppById(UUID id) {
        return appRepository.findById(id).orElse(null);
    }

    public App getAppByKey(String key) {
        return appRepository.findByKey(key).orElse(null);
    }

    public List<App> getAll(ListEntitiesParam param) {
        Pageable pageable = PageRequest.of(param.getPage(), param.getSize(),
                Sort.by(Sort.Direction.ASC, "orderIndex").and(Sort.by(Sort.Direction.DESC, "createdAt")));
        return appRepository.findAll(pageable).stream().toList();
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
