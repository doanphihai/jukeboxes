package com.example.jukeboxes.controllers;

import com.datastax.driver.core.PagingState;
import com.example.jukeboxes.pojo.JukeBoxesBySettingRequest;
import com.example.jukeboxes.pojo.JukeBoxesBySettingResponse;
import com.example.jukeboxes.services.JukeBoxSettingService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
public class JukeBoxesController
{
    private JukeBoxSettingService service;

    @Autowired
    public JukeBoxesController(JukeBoxSettingService service)
    {
        this.service = service;
    }

    @ApiOperation(value = "Get a list of Jukeboxes bases on given setting and model",
            notes = "Note: Jukebox needs to satisfy all required components of setting to support that setting",
            response = JukeBoxesBySettingResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400,
                    message = "Limit value cannot be negative (400)\nCannot validate the request's offset (400)",
                    reference = "The limit value lower than 0\nThe offset value is not a valid paging state, which is returned by API")
    })
    @RequestMapping(value = "/api/{settingId}", method = RequestMethod.GET)
    public Mono<JukeBoxesBySettingResponse> getJukeBoxesByParameters(
            @PathVariable UUID settingId,
            @RequestParam(name = "model", defaultValue = "all") String model,
            @RequestParam(name = "offset", defaultValue = "") String offset,
            @RequestParam(name = "limit", defaultValue = "10") int limit
    )
    {
        validatePagingParameter(offset, limit);

        return service.
                getJukeBoxesByModelAndComponent(new JukeBoxesBySettingRequest(settingId, model, offset, limit))
                .next();
    }

    private void validatePagingParameter(String offset, int limit)
    {
        try
        {
            if (!offset.isEmpty())
            {
                PagingState.fromString(offset);
            }
        }
        catch (Exception e)
        {
            throw new InvalidOffsetException();
        }

        if (limit < 0)
        {
            throw new InvalidLimitParameterException();
        }
    }
}