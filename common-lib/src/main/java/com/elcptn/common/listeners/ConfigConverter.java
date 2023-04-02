package com.elcptn.common.listeners;

import com.elcptn.common.helpers.CryptoHelper;
import com.elcptn.common.helpers.JsonHelper;
import com.elcptn.common.pojos.ConfigItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/* @author: kc, created on 3/27/23 */
@Component
@Slf4j
@Converter(autoApply = true)
public class ConfigConverter implements AttributeConverter<List<ConfigItem>, JsonNode> {
    private final CryptoHelper cryptoHelper;

    public ConfigConverter(CryptoHelper cryptoHelper) {
        this.cryptoHelper = cryptoHelper;
    }

    @Override
    public JsonNode convertToDatabaseColumn(List<ConfigItem> configItemDtos) {
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
    public List<ConfigItem> convertToEntityAttribute(JsonNode jsonNode) {

        if (jsonNode == null) {
            return new ArrayList<>();
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

            return JsonHelper.getMapper().convertValue(configArray, new TypeReference<List<ConfigItem>>() {
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
