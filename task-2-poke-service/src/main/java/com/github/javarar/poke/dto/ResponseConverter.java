package com.github.javarar.poke.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@AllArgsConstructor
public class ResponseConverter {

    private final ObjectMapper mapper;

    public PokemonData convert(ResponseEntity<String> response) {
        try {
            val root = mapper.readTree(response.getBody());
            val abilities = new ArrayList<String>();

            for (JsonNode item : root.path("abilities")) {
                abilities.add(item.path("ability").path("name").asText());
            }

            return PokemonData.builder()
                    .name(root.path("name").asText())
                    .height(root.path("height").asInt())
                    .weight(root.path("weight").asInt())
                    .abilities(abilities)
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
