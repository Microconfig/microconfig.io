package io.microconfig.core.service.io.ioservice.properties;

import io.microconfig.core.service.io.ioservice.ConfigIoService;
import io.microconfig.core.service.io.ioservice.ConfigReader;
import io.microconfig.core.service.io.ioservice.ConfigWriter;
import io.microconfig.utils.reader.Io;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public class PropertiesConfigIoService implements ConfigIoService {
    private final Io fileReader;

    @Override
    public ConfigReader read(File file) {
        return new PropertiesReader(file, fileReader);
    }

    @Override
    public ConfigWriter writeTo(File file) {
        return new PropertiesWriter(file);
    }
}