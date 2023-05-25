package io.cptn.common.helpers;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/* @author: kc, created on 5/18/23 */
public class FilterParser {
    public static List<FilterItem> parse(String[] filters) {

        List<FilterItem> filterItemList = new ArrayList<>();

        if (filters == null) {
            return filterItemList;
        }

        for (String filter : filters) {
            String[] filterItems = filter.split(":");
            if (filterItems.length != 3) {
                continue;
            }
            FilterItem filterItem = new FilterItem();
            filterItem.setField(filterItems[0]);
            filterItem.setOperator(filterItems[1]);
            filterItem.setValue(filterItems[2]);
            filterItemList.add(filterItem);
        }


        return filterItemList;
    }

    public static class FilterItem {

        @Getter
        @Setter
        private String field;
        @Getter
        @Setter
        private String operator;
        @Getter
        @Setter
        private String value;
    }
}
