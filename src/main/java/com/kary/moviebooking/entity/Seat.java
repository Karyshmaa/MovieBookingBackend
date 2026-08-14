package com.kary.moviebooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kary.moviebooking.enums.SeatType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "seats",
      uniqueConstraints = @UniqueConstraint(
        columnNames = {"screen_id", "rowNumber", "seatNumber"}
        ))
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_row")
    private String rowNumber;   // A, B, C

    @Column(nullable = false)
    private int seatNumber;     // 1, 2, 3

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType;  // REGULAR, PREMIUM, RECLINER

    @ManyToOne
    @JoinColumn(name = "screen_id", nullable = false)
    @JsonIgnore
    private Screen screen;
}