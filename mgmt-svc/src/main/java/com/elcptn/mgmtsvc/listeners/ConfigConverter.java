package com.elcptn.mgmtsvc.listeners;

import com.elcptn.mgmtsvc.dto.ConfigItemDto;
import com.elcptn.mgmtsvc.helpers.CryptoHelper;
import com.elcptn.mgmtsvc.helpers.JsonHelper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/* @author: kc, created on 3/27/23 */
@Component
@Slf4j
@Converter(autoApply = true)
public class ConfigConverter implements AttributeConverter<List<ConfigItemDto>, JsonNode> {
    private final CryptoHelper cryptoHelper;

    public ConfigConverter(CryptoHelper cryptoHelper) {
        this.cryptoHelper = cryptoHelper;
    }

    @Override
    public JsonNode convertToDatabaseColumn(List<ConfigItemDto> configItemDtos) {
        try {
            ArrayNode configArray = JsonHelper.getMapper().valueToTree(configItemDtos);
            for (int i = 0; i < configArray.size(); i++) {
                JsonNode node = configArray.get(i);
                if (node.has("secret") && node.get("secret").asBoolean() && node.has("value")) {
                    String value = node.get("value").textValue();
                    ((ObjectNode) node).put("value", cryptoHelper.encrypt(value));
                }
            }
            return configArray;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ConfigItemDto> convertToEntityAttribute(JsonNode jsonNode) {

        if (jsonNode == null) {
            return Lists.newArrayList();
        }

        try {
            ArrayNode configArray = (ArrayNode) jsonNode;
            for (int i = 0; i < configArray.size(); i++) {
                JsonNode node = configArray.get(i);
                if (node.has("secret") && node.get("secret").asBoolean() && node.has("value")) {
                    String value = node.get("value").textValue();
                    ((ObjectNode) node).put("value", cryptoHelper.decrypt(value));
                }
            }

            return JsonHelper.getMapper().convertValue(configArray, new TypeReference<List<ConfigItemDto>>() {
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
