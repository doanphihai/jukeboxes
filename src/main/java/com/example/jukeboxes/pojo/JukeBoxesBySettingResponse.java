package com.example.jukeboxes.pojo;

import com.example.jukeboxes.repositories.data.JukeBox;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@ApiModel
public class JukeBoxesBySettingResponse
{
    private String pagingState;
    private List<JukeBox> result;
}
