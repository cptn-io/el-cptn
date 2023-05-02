package io.cptn.common.listeners;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.cptn.common.helpers.JsonHelper;
import io.cptn.common.pojos.Header;
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
public class HeaderConverter implements AttributeConverter<List<Header>, JsonNode> {

    @Override
    public JsonNode convertToDatabaseColumn(List<Header> headers) {
        if (headers == null) {
            return null;
        }
        try {
            return JsonHelper.getMapper().valueToTree(headers);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Header> convertToEntityAttribute(JsonNode jsonNode) {

        if (jsonNode == null) {
            return new ArrayList<>();
        }
        try {
            return JsonHelper.getMapper().convertValue((ArrayNode) jsonNode, new TypeReference<List<Header>>() {
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
