package com.kary.moviebooking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
    @Table(name = "booking_seats")
    public class BookingSeat {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        //Many seats belong to one booking
        @ManyToOne
        @JoinColumn(name = "booking_id")
        private Booking booking;

        @ManyToOne
        @JoinColumn(name = "show_seat_id")
        private ShowSeat showSeat;

        private LocalDateTime bookedAt;;

}


