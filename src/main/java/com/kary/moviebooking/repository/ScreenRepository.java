package com.kary.moviebooking.repository;

import com.kary.moviebooking.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {

    // To get all screens for a particular theater
    List<Screen> findByTheaterId(Long theaterId);
    boolean existsByTheaterId(Long theaterId);
}