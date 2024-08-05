package com.github.javarar.poke.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javarar.poke.dto.PokemonData;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class PokemonControllerTest {

    @Autowired
    private PokemonController pokemonController;
    @MockBean
    private RestTemplate restTemplate;

    private final List<String> NAMES = List.of("pokemon0", "pokemon1", "pokemon2");

    @BeforeEach
    void setUp() {
        val objectMapper = new ObjectMapper();
        val responses = generatePokemonData().stream()
                .map(data -> {
                    try {
                        return objectMapper.writeValueAsString(data);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        when(restTemplate.getForEntity(anyString(), any(), anyString()))
                .thenReturn(ResponseEntity.ok(responses.get(0)))
                .thenReturn(ResponseEntity.ok(responses.get(1)))
                .thenReturn(ResponseEntity.ok(responses.get(2)))
                .thenReturn(ResponseEntity.ok(responses.get(3)))
                .thenReturn(ResponseEntity.ok(responses.get(4)));
    }

    @Test
    void getAll() {
        val response = pokemonController.getAll(NAMES);

        assertEquals("pokemon0", response.get(0).getName());
        assertEquals("pokemon1", response.get(1).getName());
        assertEquals("pokemon2", response.get(2).getName());
    }

    @Test
    void getOne() {
        val response = pokemonController.getOne(NAMES);

        assertEquals("pokemon0", response.getName());
        assertEquals(0, response.getHeight());
        assertEquals(0, response.getWeight());
        assertEquals(2, response.getAbilities().size());
    }

    @Test
    void getAllParallel() {
        val response = pokemonController.getAllParallel(NAMES);

        assertEquals(3, response.size());
    }

    @Test
    void getFirst() {
        val response = pokemonController.getFirst(NAMES);

        assertNotNull(response.getName());
        assertEquals(2, response.getAbilities().size());
    }

    private List<PokemonData> generatePokemonData() {
        return IntStream.range(0, 5)
                .mapToObj(i -> PokemonData.builder()
                        .name(String.format("pokemon%d", i))
                        .height(i)
                        .weight(i)
                        .abilities(List.of("ability1", "ability2"))
                        .build())
                .toList();


    }
}