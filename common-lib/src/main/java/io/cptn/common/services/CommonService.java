package io.cptn.common.services;

import io.cptn.common.entities.ScriptedStep;
import io.cptn.common.exceptions.WebApplicationException;
import io.cptn.common.pojos.ConfigItem;
import io.cptn.common.web.ListEntitiesParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/* @author: kc, created on 2/7/23 */

@Slf4j
public abstract class CommonService {


    protected Pageable getPageable(ListEntitiesParam param) {
        return PageRequest.of(param.getPage(), param.getSize(),
                Sort.by(param.isSortAsc() ? Sort.Direction.ASC : Sort.Direction.DESC, param.getSortBy()));
    }

    protected String hash(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(value.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
            throw new WebApplicationException("Error hashing value");
        }
    }

    protected List<ConfigItem> getConfig(ScriptedStep step) {
        List<ConfigItem> configItems = step.getConfig();
        if (configItems != null) {
            configItems = configItems.stream().map(configItem -> {
                configItem.setValue("");
                return configItem;
            }).toList();
        } else {
            configItems = List.of();
        }

        return configItems;

    }

    public static class CoreEntities {

        public static final String PIPELINE = "pipeline";
        public static final String SOURCE = "source";
        public static final String DESTINATION = "destination";
        public static final String TRANSFORMATION = "transformation";
        public static final String PIPELINE_SCHEDULE = "pipelineSchedule";

        public static final String EXTRACTOR = "extractor";

        public static final String EXTRACTOR_SCHEDULE = "extractorSchedule";

        private CoreEntities() {
        }
    }
}
