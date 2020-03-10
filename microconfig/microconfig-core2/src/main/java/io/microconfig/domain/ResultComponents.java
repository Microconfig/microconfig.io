package io.microconfig.domain;

import java.util.List;

public interface ResultComponents {
    List<ResultComponent> asList();

    ResultComponent first();

    <T> List<T> save(PropertySerializer<T> serializer);
}