package com.kary.moviebooking.repository;

import com.kary.moviebooking.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterRepository extends JpaRepository<Theater, Long> {
}