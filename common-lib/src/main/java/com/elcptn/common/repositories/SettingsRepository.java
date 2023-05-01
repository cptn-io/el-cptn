package com.elcptn.common.repositories;

import com.elcptn.common.entities.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, String> {
}