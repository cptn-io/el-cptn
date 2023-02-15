package com.elcptn.mgmtsvc.services;

import com.elcptn.mgmtsvc.entities.App;
import com.elcptn.mgmtsvc.helpers.ListEntitiesParam;
import com.elcptn.mgmtsvc.repositories.AppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/* @author: kc, created on 2/9/23 */
@Service
@RequiredArgsConstructor
public class AppService extends CommonService {
    private final AppRepository appRepository;

    public App create(App app) {
        return appRepository.save(app);

    }

    public Optional<App> getById(UUID id) {
        return appRepository.findById(id);
    }


    public List<App> getAll(ListEntitiesParam param) {
        Pageable pageable = getPageable(param);
        return appRepository.findAll(pageable).stream().collect(Collectors.toList());
    }

    public long count() {
        return appRepository.count();
    }

    public App update(App app) {
        return appRepository.save(app);
    }

    public void delete(App app) {
        appRepository.delete(app);
    }
}
