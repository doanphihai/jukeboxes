package com.example.jukeboxes.services;

import com.datastax.driver.core.PagingState;
import com.example.jukeboxes.pojo.JukeBoxesBySettingRequest;
import com.example.jukeboxes.pojo.JukeBoxesBySettingResponse;
import com.example.jukeboxes.repositories.JukeBoxesRepository;
import com.example.jukeboxes.repositories.data.JukeBox;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class JukeBoxesServiceTest
{
    private JukeBoxesRepository repository;

    private JukeBoxSettingService service;
    private final UUID SETTING = UUID.randomUUID();

    public JukeBoxesServiceTest()
    {
        repository = mock(JukeBoxesRepository.class);
        service = new JukeBoxSettingService(repository);
        final Slice<JukeBox> data = new SliceImpl<>(
                Collections.singletonList(
                        new JukeBox(SETTING, "model-0", "box-0", Collections.emptySet())
                )
        );

        final Slice<JukeBox> spyOnSlice = spy(data);
        final CassandraPageRequest mockPageRequest = mock(CassandraPageRequest.class);
        final PagingState mockState = mock(PagingState.class);

        when(mockState.toString()).thenReturn("fake-state");
        when(mockPageRequest.getPagingState()).thenReturn(mockState);
        when(spyOnSlice.getPageable()).thenReturn(mockPageRequest);

        final Mono<Slice<JukeBox>> sliceMono = Mono.just(spyOnSlice);

        when(repository.findAllBySetting(eq(SETTING), any(Pageable.class))).thenReturn(sliceMono);

        when(repository.findAllBySettingAndModel(any(UUID.class), anyString(), any(Pageable.class))).thenReturn(
                sliceMono
        );
    }

    @Test
    public void testThatServiceWillOmitModelFilterIfReceiveAnAllModelValue()
    {
        //prepare
        final JukeBoxesBySettingRequest request = new JukeBoxesBySettingRequest(SETTING, "all",
                "something-state", 10);
        //act
        service.getJukeBoxesByModelAndComponent(request).blockFirst(Duration.ofSeconds(1));

        //assert
        verify(repository, times(1)).findAllBySetting(eq(SETTING), any(Pageable.class));
    }

    @Test
    public void testThatServicePerformSuccessfully()
    {
        //prepare
        UUID uuid = UUID.randomUUID();
        final JukeBoxesBySettingRequest request = new JukeBoxesBySettingRequest(SETTING, "model-1",
                "something-state", 10);
        //act
        JukeBoxesBySettingResponse response = service.
                getJukeBoxesByModelAndComponent(request).blockFirst(Duration.ofSeconds(1));

        //assert
        verify(repository, times(1)).
                findAllBySettingAndModel(eq(SETTING), eq("model-1"), any(Pageable.class));
        assertNotNull(response);
        assertEquals("fake-state", response.getPagingState());
        assertEquals(SETTING, response.getResult().get(0).getSetting());
    }

    @Test
    public void testThatServiceUseDefaultPagingSettingIfOffsetNotExist()
    {
        //prepare
        UUID uuid = UUID.randomUUID();
        final JukeBoxesBySettingRequest request = new JukeBoxesBySettingRequest(SETTING, "model-1",
                "", 10);
        //act
        JukeBoxesBySettingResponse response = service.
                getJukeBoxesByModelAndComponent(request).blockFirst(Duration.ofSeconds(1));

        //assert
        verify(repository, times(1))
                .findAllBySettingAndModel(eq(SETTING), eq("model-1"), eq(CassandraPageRequest.of(0, 10)));

        assertNotNull(response);
        assertEquals("fake-state", response.getPagingState());
        assertEquals(SETTING, response.getResult().get(0).getSetting());
    }

}
