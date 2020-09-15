package com.learning.microservices.services;

import com.learning.microservices.models.CatalogItem;
import com.learning.microservices.models.Movie;
import com.learning.microservices.models.Rating;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MovieInfo {

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(
            fallbackMethod = "getFallbackUserRating",
            // Here we are providing configuration for BULKHEAD PATTERN
            threadPoolKey = "movieInfoPool",    // this helps in creating separate thread-poll piece
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "20"),           // Threads available for waiting
                    @HystrixProperty(name = "maxQueueSize", value = "10")        // Number of request can wait before executing thread
            }
    )
    public CatalogItem getCatalogItem(Rating rating) {
        Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
        return new CatalogItem(movie.getName(), movie.getOverview(), rating.getRating());
    }

    public CatalogItem getFallbackUserRating(Rating rating) {
        return new CatalogItem("Movie name not found", "", rating.getRating());
    }

}
