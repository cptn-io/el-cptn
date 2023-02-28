package com.elcptn.mgmtsvc.services;

import com.elcptn.mgmtsvc.entities.Action;
import com.elcptn.mgmtsvc.entities.Source;
import com.elcptn.mgmtsvc.exceptions.BadRequestException;
import com.elcptn.mgmtsvc.helpers.ListEntitiesParam;
import com.elcptn.mgmtsvc.repositories.ActionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/* @author: kc, created on 2/16/23 */
@Service
@RequiredArgsConstructor
public class ActionService extends CommonService {
    private final ActionRepository actionRepository;

    private final SourceService sourceService;

    public Action create(Action action) {
        return save(action);
    }

    public Action update(Action action) {
        return save(action);
    }

    public void delete(Action action) {
        actionRepository.delete(action);
    }

    private Action save(Action action) {
        Set<Source> sources = action.getSources();
        Set<UUID> invalidSources = sourceService.getInvalidSources(sources);
        if (invalidSources.size() > 0) {
            FieldError fieldError = new FieldError("action", "sourceIds", "Invalid references - " + invalidSources);
            throw new BadRequestException("Action could not be associated to selected Sources", List.of(fieldError));
        }

        return actionRepository.save(action);
    }

    public List<Action> getAll(ListEntitiesParam param) {
        Pageable pageable = getPageable(param);
        return actionRepository.findAll(pageable).stream().collect(Collectors.toList());
    }

    public long count() {
        return actionRepository.count();
    }


    public Optional<Action> getById(UUID id) {
        return actionRepository.findById(id);
    }
}
