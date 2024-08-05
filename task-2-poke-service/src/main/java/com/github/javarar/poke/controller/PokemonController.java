package com.github.javarar.poke.controller;

import com.github.javarar.poke.dto.PokemonData;
import com.github.javarar.poke.dto.ResponseConverter;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Log4j2
@RestController
@AllArgsConstructor
public class PokemonController {
    public static final String POKEMON_URL = "https://pokeapi.co/api/v2/pokemon/{name}";
    private final RestTemplate restTemplate;
    private final ResponseConverter responseConverter;
    private final ExecutorService executorService;

    @PostMapping("pokemon/getAll")
    public List<PokemonData> getAll(@RequestBody List<String> names) {
        return names.stream()
                .map(name -> restTemplate.getForEntity(POKEMON_URL, String.class, name))
                .map(responseConverter::convert)
                .toList();
    }

    @PostMapping("pokemon/getOne")
    public PokemonData getOne(@RequestBody List<String> names) {
        val name = names.stream().findAny()
                .orElseThrow(() -> new RuntimeException("Передан пустой список имён покемонов!"));
        val response = restTemplate.getForEntity(POKEMON_URL, String.class, name);
        return responseConverter.convert(response);
    }

    @PostMapping("pokemon/getAllParallel")
    public List<PokemonData> getAllParallel(@RequestBody List<String> names) {
        return getPokemonFutures(names).stream()
                .map(CompletableFuture::join)
                .toList();
    }

    @PostMapping("pokemon/getFirst")
    public PokemonData getFirst(@RequestBody List<String> names) {
        return (PokemonData) CompletableFuture.anyOf(getPokemonFutures(names).toArray(CompletableFuture[]::new)).join();
    }

    private List<CompletableFuture<PokemonData>> getPokemonFutures(List<String> names) {
        return names.stream()
                .map(name -> CompletableFuture.supplyAsync(() -> {
                            log.info("Получение информации о покемоне {}", name);
                            val response = restTemplate.getForEntity(POKEMON_URL, String.class, name);
                            return responseConverter.convert(response);
                        }, executorService)
                ).toList();
    }
}
