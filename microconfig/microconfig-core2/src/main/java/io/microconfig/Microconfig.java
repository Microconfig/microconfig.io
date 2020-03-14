package io.microconfig;

import io.microconfig.domain.ConfigTypes;
import io.microconfig.domain.Environment;
import io.microconfig.domain.Environments;
import io.microconfig.domain.Resolver;
import io.microconfig.domain.impl.configtype.StandardConfigTypes;
import io.microconfig.domain.impl.configtype.YamlDescriptorConfigTypes;
import io.microconfig.domain.impl.environment.ComponentFactory;
import io.microconfig.domain.impl.environment.provider.ComponentFactoryImpl;
import io.microconfig.domain.impl.environment.provider.EnvironmentParserImpl;
import io.microconfig.domain.impl.environment.provider.FileBasedEnvironments;
import io.microconfig.domain.impl.properties.repository.ConfigParserImpl;
import io.microconfig.domain.impl.properties.repository.FileSystemPropertyRepository;
import io.microconfig.domain.impl.properties.resolvers.expression.ExpressionResolver;
import io.microconfig.domain.impl.properties.resolvers.placeholder.PlaceholderResolver;
import io.microconfig.io.formats.FileSystemIo;
import io.microconfig.io.formats.Io;
import io.microconfig.io.fsgraph.CachedFileSystemGraph;
import io.microconfig.io.fsgraph.FileSystemGraph;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.File;

import static io.microconfig.domain.impl.configtype.CompositeConfigTypes.composite;
import static io.microconfig.domain.impl.properties.resolvers.chain.ChainedResolver.chainOf;
import static io.microconfig.io.FileUtils.canonical;
import static io.microconfig.io.formats.factory.ConfigIoServiceFactory.newConfigIoService;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class Microconfig {
    private final File rootDir;
    @Setter
    private Io io = new FileSystemIo();

    public static Microconfig searchConfigsIn(File rootDir) {
        File canonical = canonical(rootDir);
        if (!canonical.exists()) {
            throw new IllegalArgumentException("Root directory doesn't exist: " + rootDir);
        }
        return new Microconfig(canonical);
    }

    public Environment inEnvironment(String name) {
        return environments().withName(name);
    }

    public Resolver resolver() {
        return chainOf(
                new PlaceholderResolver(null),
                new ExpressionResolver()
        );
    }

    private Environments environments() {
        return new FileBasedEnvironments(
                rootDir,
                new EnvironmentParserImpl(io, fileSystemComponentFactory())
        );
    }

    private ComponentFactory fileSystemComponentFactory() {
        return new ComponentFactoryImpl(
                configTypes(),
                fsPropertyRepository()
        );
    }

    private FileSystemPropertyRepository fsPropertyRepository() {
        return new FileSystemPropertyRepository(
                fsGraph(),
                new ConfigParserImpl(newConfigIoService(io))
        );
    }

    private ConfigTypes configTypes() {
        return composite(
                new YamlDescriptorConfigTypes(rootDir, io),
                StandardConfigTypes.asTypes()
        );
    }

    private FileSystemGraph fsGraph() {
        return CachedFileSystemGraph.prepare(rootDir);
    }
}