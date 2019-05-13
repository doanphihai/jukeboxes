package com.example.jukeboxes.pojo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@ApiModel
public class JukeBoxesBySettingRequest
{
    private final UUID settings;
    private final String model;
    private final String offset;
    private final int limit;
}
