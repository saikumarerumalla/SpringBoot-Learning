package com.explore.Spring_AI.controller;

import com.explore.Spring_AI.Model.Movie;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MovieController {
    private ChatClient chatClient;

    public MovieController(OpenAiChatModel chatModel){
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    @GetMapping("/api/moviesList")
    public List<String> getMovies(@RequestParam String name){
        List<String> movies = chatClient.prompt()
                .user(us-> us.text("List top 5 movies of {name}").param("name", name))
                .call()
                .entity(new ListOutputConverter(new DefaultConversionService()));

        return movies;
    }

    @GetMapping("/api/movie")
    public Movie getMovie(@RequestParam String name){
        BeanOutputConverter<Movie> converter = new BeanOutputConverter<Movie>(Movie.class);
        Movie movie = chatClient.prompt()
                .user(us-> us.text("Get me the best movie of {name}").param("name", name))
                .call()
                .entity(converter);

        return movie;
    }

    @GetMapping("/api/movies")
    public List<Movie> getMoviesBean(@RequestParam String name){
        BeanOutputConverter<List<Movie>> converter = new BeanOutputConverter<List<Movie>>(new ParameterizedTypeReference<List<Movie>>() {
        });
        List<Movie> movies = chatClient.prompt()
                .user(us-> us.text("Get me top 5 movies of {name}").param("name", name))
                .call()
                .entity(converter);

        return movies;
    }





}
