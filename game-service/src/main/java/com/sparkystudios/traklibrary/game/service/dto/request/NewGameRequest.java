package com.sparkystudios.traklibrary.game.service.dto.request;

import com.sparkystudios.traklibrary.game.domain.GameMode;
import com.sparkystudios.traklibrary.game.service.dto.AgeRatingDto;
import com.sparkystudios.traklibrary.game.service.dto.DownloadableContentDto;
import com.sparkystudios.traklibrary.game.service.dto.GameReleaseDateDto;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;

@Data
public class NewGameRequest {

    @NotEmpty(message = "{game.validation.title.not-empty}")
    private String title;

    @Size(max = 4096, message = "{game.validation.description.size}")
    private String description;

    private Set<AgeRatingDto> ageRatings = new TreeSet<>();

    private Set<GameMode> gameModes = EnumSet.noneOf(GameMode.class);

    private Long franchiseId;

    private Set<GameReleaseDateDto> releaseDates = new TreeSet<>();

    private Set<DownloadableContentDto> downloadableContents = new TreeSet<>();
}
