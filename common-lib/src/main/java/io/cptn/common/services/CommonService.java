package io.cptn.common.services;

import io.cptn.common.exceptions.WebApplicationException;
import io.cptn.common.web.ListEntitiesParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
}
