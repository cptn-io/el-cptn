package io.cptn.mgmtsvc.services;

import io.cptn.common.entities.Settings;
import io.cptn.common.repositories.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/* @author: kc, created on 5/1/23 */
@Service
@RequiredArgsConstructor
public class SettingsService {

    private final SettingsRepository settingsRepository;

    public Settings upsert(String key, String value, Boolean systemManaged) {
        return settingsRepository.findById(key)
                .map(settings -> {
                    settings.setValue(value);
                    settings.setSystemManaged(systemManaged);
                    return settingsRepository.save(settings);
                })
                .orElseGet(() -> settingsRepository.save(new Settings(key, value, systemManaged)));
    }

    public Settings get(String key) {
        return settingsRepository.findById(key).orElse(null);
    }

    public void delete(String key) {
        settingsRepository.deleteById(key);
    }
}
