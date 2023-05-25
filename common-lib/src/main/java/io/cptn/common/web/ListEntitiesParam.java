package io.cptn.common.web;

import com.google.common.primitives.Ints;
import io.cptn.common.helpers.FilterParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

/* @author: kc, created on 2/7/23 */

@Data
public class ListEntitiesParam {
    private int page;
    private int size;
    private String[] sortBy;
    private boolean sortAsc;

    private List<FilterParser.FilterItem> filters;

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

        String sortOrder = Optional.ofNullable(request.getParameter("sortBy")).orElse("createdAt");
        this.sortBy = sortOrder.split(",");
        this.sortAsc = Boolean.parseBoolean(request.getParameter("asc"));

        String filterVal = Optional.ofNullable(request.getParameter("filters")).orElse("");
        this.filters = FilterParser.parse(filterVal.split(","));
    }
}
