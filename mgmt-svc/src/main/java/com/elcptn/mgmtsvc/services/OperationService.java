package com.elcptn.mgmtsvc.services;

import com.elcptn.mgmtsvc.entities.App;
import com.elcptn.mgmtsvc.entities.Operation;
import com.elcptn.mgmtsvc.exceptions.BadRequestException;
import com.elcptn.mgmtsvc.helpers.ListEntitiesParam;
import com.elcptn.mgmtsvc.repositories.OperationRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/* @author: kc, created on 2/12/23 */
@Service
@RequiredArgsConstructor
public class OperationService extends CommonService {
    private final OperationRepository operationRepository;

    private final AppService appService;

    public Operation create(@NonNull Operation operation) {

        UUID appId = Optional.ofNullable(operation.getApp().getId()).orElseThrow(() -> new BadRequestException("appId" +
                " is required"));

        Optional<App> appOptional = appService.getById(appId.toString());
        if (appOptional.isEmpty()) {
            throw new BadRequestException("App not found with passed ID");
        }

        return operationRepository.save(operation);
    }

    public Operation update(Operation operation) {
        return operationRepository.save(operation);
    }

    public Optional<Operation> getById(String id) {
        return operationRepository.findById(UUID.fromString(id));
    }

    public List<Operation> getAll(String appId, ListEntitiesParam param) {
        Pageable pageable = getPageable(param);
        return operationRepository.findAllByApp(UUID.fromString(appId), pageable).stream().collect(Collectors.toList());
    }
}
