package com.elcptn.mgmtsvc.helpers;

import com.google.common.primitives.Ints;
import lombok.Data;
import lombok.NonNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Data
public class ListEntitiesParam {

    private int page;

    private int size;

    private String[] sortBy;

    private boolean sortAsc;

    public ListEntitiesParam(@NonNull HttpServletRequest request) {
        this.page = Optional.ofNullable(Ints.tryParse(Optional.ofNullable(request.getParameter("page"))
                .orElse("0"))).orElse(0);
        this.size = Optional.ofNullable(Ints.tryParse(Optional.ofNullable(request.getParameter("size"))
                .orElse("15"))).orElse(15);
        if (this.size > 100) {
            this.size = 15;
        }

        String sortBy = Optional.ofNullable(request.getParameter("sortBy")).orElse("createdAt");
        this.sortBy = sortBy.split(",");

        this.sortAsc = Boolean.valueOf(request.getParameter("asc"));
    }
}
