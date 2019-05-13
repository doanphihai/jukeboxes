package com.example.jukeboxes.repositories;

import com.example.jukeboxes.repositories.data.JukeBox;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface JukeBoxesRepository extends ReactiveCassandraRepository<JukeBox, UUID>
{
    @Query(value = "SELECT * FROM juke_boxes WHERE setting = ?0 AND model = ?1")
    Mono<Slice<JukeBox>> findAllBySettingAndModel(UUID setting, String model, Pageable pageable);

    @Query(value = "SELECT * FROM juke_boxes WHERE setting = ?0")
    Mono<Slice<JukeBox>> findAllBySetting(UUID setting, Pageable pageable);
}
