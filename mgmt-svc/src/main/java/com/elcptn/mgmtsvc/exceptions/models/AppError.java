package com.elcptn.mgmtsvc.exceptions.models;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class AppError {
    
    @NonNull
    private String message;

    private Map<String, List<String>> fieldErrors = new HashMap<>();
}
