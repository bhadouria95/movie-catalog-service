package com.learning.microservices.resources;

//import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
//import org.springframework.web.reactive.function.client.WebClient;

import com.learning.microservices.models.CatalogItem;
import com.learning.microservices.models.Movie;
import com.learning.microservices.models.UserRating;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogService {
	
	// We can do api call using rest template
	@Autowired
	private RestTemplate restTemplate;
	
	// @Autowired
	// private WebClient.Builder webClientBuilder;
	
	@RequestMapping("/{userId}")
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {
		
		UserRating ratings = restTemplate.getForObject("http://movie-rating-service/ratingsdata/users/" + userId, UserRating.class);
				
		return ratings.getUserRating().stream()
				.map(rating -> {
					Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
					/*
					 * Movie movie = webClientBuilder.build() .get()
					 * .uri("http://localhost:8082/movies/" + rating.getMovieId()) .retrieve()
					 * .bodyToMono(Movie.class) .block();
					 */
					return new CatalogItem(movie.getName(), "Desc", rating.getRating());
				})
				.collect(Collectors.toList());
		// return Collections.singletonList(new CatalogItem("Transformers", "Test", 4));
		
	}

}
