package com.example.jukeboxes.controllers;

import com.example.jukeboxes.pojo.JukeBoxesBySettingRequest;
import com.example.jukeboxes.pojo.JukeBoxesBySettingResponse;
import com.example.jukeboxes.services.JukeBoxSettingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(JukeBoxesController.class)
public class JukeBoxesControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JukeBoxSettingService service;

    @Test
    public void testThatControllerRequireOnlySettingParameterToPerformSuccessfully() throws Exception
    {
        //prepare
        when(service.getJukeBoxesByModelAndComponent(any(JukeBoxesBySettingRequest.class)))
                .thenReturn(
                        Flux.just(
                                new JukeBoxesBySettingResponse("state-01", Collections.emptyList())
                        )
                );

        final UUID uuid = UUID.randomUUID();
        //act
        MvcResult mvcResult = this.mockMvc.perform(get("/api/" + uuid))
                .andDo(MockMvcResultHandlers.log())
                .andReturn();

        //assert
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(content().string("{\"pagingState\":\"state-01\",\"result\":[]}"));

        ArgumentCaptor<JukeBoxesBySettingRequest> argumentCaptor = ArgumentCaptor.forClass(JukeBoxesBySettingRequest.class);
        verify(service, times(1)).getJukeBoxesByModelAndComponent(argumentCaptor.capture());
        assertEquals(uuid, argumentCaptor.getValue().getSettings());
    }

    @Test
    public void testThatControllerWithoutSettingParameterReturnNotFound() throws Exception
    {
        //prepare
        when(service.getJukeBoxesByModelAndComponent(any(JukeBoxesBySettingRequest.class)))
                .thenReturn(
                        Flux.just(
                                new JukeBoxesBySettingResponse("state-01", Collections.emptyList())
                        )
                );

        //act
        this.mockMvc.perform(get("/api/"))
                .andDo(MockMvcResultHandlers.log())
                .andExpect(status().isNotFound());

        //assert

        verify(service, never()).getJukeBoxesByModelAndComponent(any(JukeBoxesBySettingRequest.class));
    }

    @Test
    public void testThatControllerReturnBadRequestForInvalidOffset() throws Exception
    {
        //prepare
        when(service.getJukeBoxesByModelAndComponent(any(JukeBoxesBySettingRequest.class)))
                .thenReturn(
                        Flux.just(
                                new JukeBoxesBySettingResponse("state-01", Collections.emptyList())
                        )
                );

        final UUID uuid = UUID.randomUUID();
        //act
        this.mockMvc.perform(get("/api/" + uuid + "?model=some-model&offset=invalid-offset"))
                .andDo(MockMvcResultHandlers.log())
                .andExpect(status().isBadRequest());

        //assert

        verify(service, never()).getJukeBoxesByModelAndComponent(any(JukeBoxesBySettingRequest.class));
    }

    @Test
    public void testThatControllerReturnBadRequestForNegativeLimitValue() throws Exception
    {
        //prepare
        when(service.getJukeBoxesByModelAndComponent(any(JukeBoxesBySettingRequest.class)))
                .thenReturn(
                        Flux.just(
                                new JukeBoxesBySettingResponse("state-01", Collections.emptyList())
                        )
                );

        final UUID uuid = UUID.randomUUID();
        //act
        this.mockMvc.perform(get("/api/" + uuid + "?model=some-model&limit=-1"))
                .andDo(MockMvcResultHandlers.log())
                .andExpect(status().isBadRequest());

        //assert
        verify(service, never()).getJukeBoxesByModelAndComponent(any(JukeBoxesBySettingRequest.class));
    }


    @Test
    public void testThatControllerWillFallBackToDefaultValueForAllMissingOptionalParameters() throws Exception
    {
        //prepare
        when(service.getJukeBoxesByModelAndComponent(any(JukeBoxesBySettingRequest.class)))
                .thenReturn(
                        Flux.just(
                                new JukeBoxesBySettingResponse("state-01", Collections.emptyList())
                        )
                );

        final UUID uuid = UUID.randomUUID();
        //act
        MvcResult mvcResult = this.mockMvc.perform(get("/api/" + uuid))
                .andDo(MockMvcResultHandlers.log())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(content().string("{\"pagingState\":\"state-01\",\"result\":[]}"));

        //assert
        ArgumentCaptor<JukeBoxesBySettingRequest> argumentCaptor = ArgumentCaptor.forClass(JukeBoxesBySettingRequest.class);
        verify(service, times(1)).getJukeBoxesByModelAndComponent(argumentCaptor.capture());
        JukeBoxesBySettingRequest captorValue = argumentCaptor.getValue();

        assertEquals("all", captorValue.getModel());
        assertEquals(10, captorValue.getLimit());
        assertEquals("", captorValue.getOffset());
    }

}
