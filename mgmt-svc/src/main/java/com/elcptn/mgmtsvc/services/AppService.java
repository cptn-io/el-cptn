package com.elcptn.mgmtsvc.services;

import com.elcptn.common.entities.App;
import com.elcptn.common.pojos.ConfigItem;
import com.elcptn.common.repositories.AppRepository;
import com.elcptn.common.services.CommonService;
import com.elcptn.common.web.ListEntitiesParam;
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

    public App createApp() {
        App app = new App();
        app.setName("New App");
        app.setHash("hash1");
        app.setKey("key1");
        ConfigItem configItem = new ConfigItem();
        configItem.setKey("key1");
        configItem.setValue("value1");
        app.setConfig(List.of(configItem));
        app.setScript("//script goes here");
        return appRepository.save(app);
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
}
