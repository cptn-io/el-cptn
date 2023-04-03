package com.elcptn.common.web;

import com.google.common.primitives.Ints;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.NonNull;

import java.util.Optional;

/* @author: kc, created on 2/7/23 */

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

        if (this.page < 0) {
            this.page = 0;
        }

        if (this.size <= 0 || this.size > 100) {
            this.size = 15;
        }

        String sortBy = Optional.ofNullable(request.getParameter("sortBy")).orElse("createdAt");
        this.sortBy = sortBy.split(",");
        this.sortAsc = Boolean.parseBoolean(request.getParameter("asc"));
    }
}
