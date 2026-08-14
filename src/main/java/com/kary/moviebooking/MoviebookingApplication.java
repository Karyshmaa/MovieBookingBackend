package com.kary.moviebooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableAsync
public class MoviebookingApplication {
	public static void main(String[] args) {
		SpringApplication.run(MoviebookingApplication.class, args);
	}
}
//Bidirectional relationship + infinite serialization
//
//There are cleaner solutions like:
//
//@JsonManagedReference / @JsonBackReference
//DTOs (best practice)
//“Because bidirectional relationship caused Jackson to recursively serialize objects again and again”
