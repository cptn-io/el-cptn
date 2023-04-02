package com.elcptn.common.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;

/* @author: kc, created on 3/27/23 */
public class JsonHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ObjectMapper getMapper() {
        return MAPPER;
    }
}
