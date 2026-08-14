package com.kary.moviebooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "screens",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"theater_id", "name"}
        ))
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "total_seats")
    private Integer totalSeats;

    @ManyToOne(optional = false)
    @JoinColumn(name = "theater_id")
    @JsonIgnore
    private Theater theater;

    // ✅ cascade ALL — screen delete hone pe seats bhi delete honge
    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Seat> seats;
}
