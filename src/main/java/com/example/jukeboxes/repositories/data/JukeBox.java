package com.example.jukeboxes.repositories.data;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Set;
import java.util.UUID;

@Data
@Table(value = "juke_boxes")
public final class JukeBox
{
    @PrimaryKeyColumn(name = "setting", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private final UUID setting;

    @PrimaryKeyColumn(name = "model", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private final String model;

    @PrimaryKeyColumn(name = "id", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private final String id;

    @Column(value = "components")
    private final Set<String> components;

}
