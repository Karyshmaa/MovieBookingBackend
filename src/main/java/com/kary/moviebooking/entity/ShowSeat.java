package com.kary.moviebooking.entity;

import com.kary.moviebooking.enums.SeatStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="show_seats")
public class ShowSeat {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "show_id")
        private Show show;

        @ManyToOne
        @JoinColumn(name = "seat_id")
        private Seat seat;

        @Enumerated(EnumType.STRING)
        private SeatStatus seatStatus;

        private LocalDateTime lockedAt;

        private Long lockedByUserId;

       @ManyToOne
       @JoinColumn(name = "booking_id")
       private Booking booking;

       private Double price;
}

