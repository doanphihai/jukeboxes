package com.example.jukeboxes.services;

import com.datastax.driver.core.PagingState;
import com.example.jukeboxes.pojo.JukeBoxesBySettingRequest;
import com.example.jukeboxes.pojo.JukeBoxesBySettingResponse;
import com.example.jukeboxes.repositories.JukeBoxesRepository;
import com.example.jukeboxes.repositories.data.JukeBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public
class JukeBoxSettingService
{
    private static final String ALL_MODELS = "all";
    private final Logger LOGGER = LoggerFactory.getLogger(JukeBoxSettingService.class);
    private JukeBoxesRepository repository;

    @Autowired
    JukeBoxSettingService(JukeBoxesRepository repository)
    {
        this.repository = repository;
    }

    public Flux<JukeBoxesBySettingResponse> getJukeBoxesByModelAndComponent(JukeBoxesBySettingRequest jukeBoxesBySettingRequest)
    {
        int limit = jukeBoxesBySettingRequest.getLimit() > 0 ? jukeBoxesBySettingRequest.getLimit() : 10;
        final CassandraPageRequest defState = CassandraPageRequest.of(0, limit);
        final String offset = jukeBoxesBySettingRequest.getOffset();

        CassandraPageRequest pageRequest;
        if (offset.isEmpty())
        {
            pageRequest = defState;
        }
        else
        {
            PagingState pagingStateFromString = makePagingStateFromString(offset);
            if (pagingStateFromString != null)
            {
                pageRequest = CassandraPageRequest.of(defState, makePagingStateFromString(offset));
            }
            else
            {
                pageRequest = defState;
            }
        }

        LOGGER.info("find all jukeboxes with setting {} and model {} start with offset {} and page size {}",
                jukeBoxesBySettingRequest.getSettings(),
                jukeBoxesBySettingRequest.getModel(),
                offset,
                limit
        );

        return callRepository(jukeBoxesBySettingRequest, pageRequest);

    }

    /**
     * Make sure service won't receive any invalid pagination state
     * This method is now not necessary due to the check on the controller
     * but it's still useful later
     *
     * @param offset
     * @return
     */
    private PagingState makePagingStateFromString(String offset)
    {
        try
        {
            return PagingState.fromString(offset);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private Flux<JukeBoxesBySettingResponse> callRepository(JukeBoxesBySettingRequest jukeBoxesBySettingRequest,
                                                            CassandraPageRequest pageRequest)
    {
        if (jukeBoxesBySettingRequest.getModel().equalsIgnoreCase(ALL_MODELS))
        {
            return toResponse(repository.findAllBySetting(
                    jukeBoxesBySettingRequest.getSettings(),
                    pageRequest
            ));
        }
        else
        {
            return toResponse(repository.findAllBySettingAndModel(
                    jukeBoxesBySettingRequest.getSettings(),
                    jukeBoxesBySettingRequest.getModel(),
                    pageRequest
            ));
        }
    }

    /**
     * Create a comprehensible response for the controller
     *
     * @param sliceMono
     * @return
     */
    private Flux<JukeBoxesBySettingResponse> toResponse(Mono<Slice<JukeBox>> sliceMono)
    {
        return sliceMono
                .map(slice ->
                {
                    final PagingState pagingState = ((CassandraPageRequest) slice.getPageable()).getPagingState();

                    return new JukeBoxesBySettingResponse(
                            pagingState != null ? pagingState.toString() : null,
                            slice.getContent()
                    );
                })
                .flatMapMany(Flux::just);
    }

}
