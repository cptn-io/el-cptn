package com.elcptn.mgmtsvc.exceptions.models;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* @author: kc, created on 2/7/23 */

@Data
@RequiredArgsConstructor
public class AppError {

    @NonNull
    private String message;

    private Map<String, List<String>> fieldErrors = new HashMap<>();
}
