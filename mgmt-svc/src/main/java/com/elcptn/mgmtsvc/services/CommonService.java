package com.elcptn.mgmtsvc.services;

import com.elcptn.mgmtsvc.helpers.ListEntitiesParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/* @author: kc, created on 2/7/23 */

public abstract class CommonService {
    protected Pageable getPageable(ListEntitiesParam param) {
        return PageRequest.of(param.getPage(), param.getSize(),
                Sort.by(param.isSortAsc() ? Sort.Direction.ASC : Sort.Direction.DESC, param.getSortBy()));
    }
}
