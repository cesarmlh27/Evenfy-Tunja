package org.jdc.tunja_evenfy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class EventDTO {
    private UUID id;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede exceder 150 caracteres")
    private String title;

    private String description;

    @NotNull(message = "La fecha del evento es obligatoria")
    @JsonProperty("event_date")
    private LocalDateTime eventDate;

    private String location;
    private String category;

    @JsonProperty("category_id")
    private UUID categoryId;

    @JsonProperty("location_id")
    private UUID locationId;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("is_free")
    private Boolean isFree;

    @JsonProperty("ticket_purchase_url")
    private String ticketPurchaseUrl;

    @JsonProperty("info_url")
    private String infoUrl;

    @JsonProperty("max_capacity")
    private Integer maxCapacity;

    @JsonProperty("created_by")
    private UUID createdBy;

    @JsonProperty("organizer_name")
    private String organizerName;

    @JsonProperty("attendee_count")
    private Long attendeeCount;

    @JsonProperty("average_rating")
    private Double averageRating;

    @JsonProperty("rating_count")
    private Integer ratingCount;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
