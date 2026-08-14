package com.kary.moviebooking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 50)
    private String language;

    private String director;

    private int releaseYear;

    private String genre;

    private int duration;// in minutes

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(columnDefinition = "text")
    private String about;

    @Column(name = "poster_url")
    private String posterUrl;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();  // ✅ auto set, never do this in controller
    }
}