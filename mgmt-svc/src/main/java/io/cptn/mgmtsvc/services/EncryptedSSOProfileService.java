package io.cptn.mgmtsvc.services;

import io.cptn.common.entities.SSOProfile;
import io.cptn.common.repositories.SSOProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/* @author: kc, created on 5/9/23 */
@Service
@RequiredArgsConstructor
public class EncryptedSSOProfileService {

    @Cacheable(value = "ssoprofile", key = "'ssoprofile'")
    public SSOProfile getSSOProfileWithCipherSecret(SSOProfileRepository ssoProfileRepository) {
        return ssoProfileRepository.findFirstBy();
    }

}
