package com.ProjetoIndividual.sprint1;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/pokemon")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class PokemonController {

    private final JdbcTemplate jdbcTemplate;

    public PokemonController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/tipos")
    public ResponseEntity<List<Tipo>> getTipos() {
        String sql = "SELECT * FROM tipo ORDER BY nome";
        List<Tipo> tipos = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Tipo.class));
        return ResponseEntity.status(200).body(tipos);
    }

    @GetMapping("/ataques")
    public ResponseEntity<List<Ataque>> getAtaques() {
        String sql = "SELECT * FROM ataque";
        List<Ataque> ataques = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Ataque.class));
        return ResponseEntity.status(200).body(ataques);
    }

    @GetMapping("/pokemons")
    public ResponseEntity<List<Pokemon>> getPokemons() {
        String sql = "SELECT * FROM pokemon WHERE time_id = 1";
        List<Pokemon> pokemons = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Pokemon.class));
        return ResponseEntity.status(200).body(pokemons);
    }

    @PostMapping("/pokemons/criar")
    public ResponseEntity<?> addPokemon(@RequestBody Pokemon pokemon,
                                        @RequestParam List<Integer> ataqueIds) {

        if (pokemon.getNome() == null || pokemon.getNome().trim().length() < 2 || pokemon.getNome().length() > 20) {
            return ResponseEntity.status(400).body(new Erro("Nome inválido"));
        }
        if (pokemon.getNivel() < 1 || pokemon.getNivel() > 100) {
            return ResponseEntity.status(400).body(new Erro("Nível deve estar entre 1 e 100"));
        }
        if (pokemon.getDataCaptura() == null || pokemon.getDataCaptura().after(new java.util.Date())) {
            return ResponseEntity.status(400).body(new Erro("Data de captura inválida"));
        }
        if (!existeTipoPorId(pokemon.getTipo_id())) {
            return ResponseEntity.status(400).body(new Erro("Tipo inexistente"));
        }
        if (ataqueIds == null || ataqueIds.isEmpty() || ataqueIds.size() > 4) {
            return ResponseEntity.status(400).body(new Erro("Ataques inválidos"));
        }
        Set<Integer> ataquesUnicos = new HashSet<>(ataqueIds);
        if (ataquesUnicos.size() != ataqueIds.size()) {
            return ResponseEntity.status(400).body(new Erro("Ataques duplicados"));
        }
        for (Integer ataqueId : ataqueIds) {
            if (!existeAtaquePorId(ataqueId)) {
                return ResponseEntity.status(400).body(new Erro("Ataque inexistente: " + ataqueId));
            }
        }


        String sql = "INSERT INTO pokemon (nome, nivel, data_captura, shiny, tipo_id, time_id) VALUES (?, ?, ?, ?, ?, 1)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, pokemon.getNome());
            ps.setInt(2, pokemon.getNivel());
            ps.setDate(3, new Date(pokemon.getDataCaptura().getTime()));
            ps.setBoolean(4, pokemon.isShiny());
            ps.setInt(5, pokemon.getTipo_id());
            return ps;
        }, keyHolder);

        Integer idGerado = keyHolder.getKeyAs(Integer.class);
        pokemon.setId(idGerado);


        for (Integer ataqueId : ataqueIds) {
            String sqlAtaque = "INSERT INTO pokemon_ataque (pokemon_id, ataque_id) VALUES (?, ?)";
            jdbcTemplate.update(sqlAtaque, idGerado, ataqueId);
        }

        return ResponseEntity.status(201).body(pokemon);
    }

    @DeleteMapping("/pokemons/{id}")
    public ResponseEntity<Void> deletePokemon(@PathVariable Integer id) {
        if (!existePokemonPorId(id)) {
            return ResponseEntity.status(404).build();
        }
        String sql = "DELETE FROM pokemon WHERE id = ?";
        jdbcTemplate.update(sql, id);
        return ResponseEntity.status(204).build();
    }

    @PutMapping("/time/atualizarNome")
    public ResponseEntity<?> atualizarNomeTime(@RequestBody Time time) {
        if (time.getNome() == null || time.getNome().trim().length() < 2 || time.getNome().trim().length() > 50) {
            return ResponseEntity.status(400).body(new Erro("Nome inválido"));
        }

        Integer id = 1;

        String sql = "UPDATE time SET nome = ? WHERE id = ?";
        jdbcTemplate.update(sql, time.getNome(), id);
        time.setId(id);

        return ResponseEntity.status(200).body(time);
    }
    private Boolean existePokemonPorId(Integer id) {
        String sql = "SELECT COUNT(*) FROM pokemon WHERE id = ?";
        Integer countId = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return countId == 1;
    }

    private Boolean existeTipoPorId(Integer id) {
        String sql = "SELECT COUNT(*) FROM tipo WHERE id = ?";
        Integer countId = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return countId == 1;
    }

    private Boolean existeAtaquePorId(Integer id) {
        String sql = "SELECT COUNT(*) FROM ataque WHERE id = ?";
        Integer countId = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return countId == 1;
    }

    public static class Erro {
        private String mensagem;
        public Erro(String mensagem) { this.mensagem = mensagem; }
        public String getMensagem() { return mensagem; }
    }
}
