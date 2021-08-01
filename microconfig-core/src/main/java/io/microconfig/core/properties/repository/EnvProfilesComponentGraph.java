package io.microconfig.core.properties.repository;

import io.microconfig.core.configtypes.ConfigType;
import io.microconfig.core.environments.EnvironmentRepository;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static io.microconfig.utils.CollectionUtils.join;
import static io.microconfig.utils.CollectionUtils.minus;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class EnvProfilesComponentGraph implements ComponentGraph {
    private final ComponentGraph delegate;
    private final EnvironmentRepository environmentRepository;

    @Override
    public List<ConfigFile> getConfigFilesOf(String component, String environment, ConfigType configType) {
        List<ConfigFile> standard = delegate.getConfigFilesOf(component, environment, configType);
        List<ConfigFile> profiles = getConfigsFromProfiles(component, environment, configType);
        return join(profiles, minus(standard, profiles));
    }

    private List<ConfigFile> getConfigsFromProfiles(String component, String environment, ConfigType configType) {
        return environmentRepository.getOrCreateByName(environment)
                .getProfiles()
                .stream()
                .flatMap(p -> getConfigFilesOf(component, p, configType).stream())
                .collect(toList());
    }

    @Override
    public Optional<File> getFolderOf(String component) {
        return delegate.getFolderOf(component);
    }
}