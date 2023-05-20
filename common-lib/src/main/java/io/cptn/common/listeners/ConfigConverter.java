package io.cptn.common.listeners;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cptn.common.exceptions.BadRequestException;
import io.cptn.common.helpers.CryptoHelper;
import io.cptn.common.helpers.JsonHelper;
import io.cptn.common.pojos.ConfigItem;
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

    private static final String SECRET_ATTRIBUTE = "secret";

    private static final String VALUE_ATTRIBUTE = "value";

    private final CryptoHelper cryptoHelper;

    public ConfigConverter(CryptoHelper cryptoHelper) {
        this.cryptoHelper = cryptoHelper;
    }

    @Override
    public JsonNode convertToDatabaseColumn(List<ConfigItem> configItemDtos) {
        if (configItemDtos == null) {
            return null;
        }
        try {
            ArrayNode configArray = JsonHelper.getMapper().valueToTree(configItemDtos);
            for (int i = 0; i < configArray.size(); i++) {
                JsonNode node = configArray.get(i);
                if (node.has(SECRET_ATTRIBUTE) && node.get(SECRET_ATTRIBUTE).asBoolean() && node.has(VALUE_ATTRIBUTE)) {
                    String value = node.get(VALUE_ATTRIBUTE).textValue();
                    ((ObjectNode) node).put(VALUE_ATTRIBUTE, cryptoHelper.encrypt(value));
                }
            }
            return configArray;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException("Unable to convert config to JSON");
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
                if (node.has(SECRET_ATTRIBUTE) && node.get(SECRET_ATTRIBUTE).asBoolean() && node.has(VALUE_ATTRIBUTE)) {
                    String value = node.get(VALUE_ATTRIBUTE).textValue();
                    ((ObjectNode) node).put(VALUE_ATTRIBUTE, cryptoHelper.decrypt(value));
                }
            }

            return JsonHelper.getMapper().convertValue(configArray, new TypeReference<List<ConfigItem>>() {
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BadRequestException("Unable to convert config from JSON");
        }
    }
}
