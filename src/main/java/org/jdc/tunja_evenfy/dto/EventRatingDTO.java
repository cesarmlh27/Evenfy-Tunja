package org.jdc.tunja_evenfy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRatingDTO {
    @JsonProperty("event_id")
    private UUID eventId;

    @JsonProperty("rating")
    private Integer rating; // 1-5

    @JsonProperty("comment")
    private String comment;
}
