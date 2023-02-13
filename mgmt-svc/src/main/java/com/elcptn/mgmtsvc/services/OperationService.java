package com.elcptn.mgmtsvc.services;

import com.elcptn.mgmtsvc.entities.Operation;
import com.elcptn.mgmtsvc.helpers.ListEntitiesParam;
import com.elcptn.mgmtsvc.repositories.OperationRepository;
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

    public void create(Operation operation) {
        operationRepository.save(operation);
    }

    public void update(Operation operation) {
        operationRepository.save(operation);
    }

    public Optional<Operation> getById(String id) {
        return operationRepository.findById(UUID.fromString(id));
    }

    public List<Operation> getAll(String appId, ListEntitiesParam param) {
        Pageable pageable = getPageable(param);
        return operationRepository.findAllByApp(UUID.fromString(appId), pageable).stream().collect(Collectors.toList());
    }
}
