package com.github.javarar.poke.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PokemonData {
    private String name;
    private int height;
    private int weight;
    private List<String> abilities;
}
