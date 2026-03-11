package org.jdc.tunja_evenfy.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 150)
    private String placeName;

    @Column(nullable = false)
    private String address;

    private Double latitude;
    private Double longitude;
}
