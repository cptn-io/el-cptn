package io.cptn.common.repositories;

import io.cptn.common.entities.SSOProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SSOProfileRepository extends JpaRepository<SSOProfile, UUID> {

    SSOProfile findByClientId(String clientId);

    SSOProfile findFirstBy();
}