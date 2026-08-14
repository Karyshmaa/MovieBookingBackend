package com.kary.moviebooking.repository;

import com.kary.moviebooking.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {

    List<Show> findByMovieId(Long movieId);
    List<Show> findByTheaterId(Long theaterId);
    List<Show> findByScreen_Id(Long screenId);
}