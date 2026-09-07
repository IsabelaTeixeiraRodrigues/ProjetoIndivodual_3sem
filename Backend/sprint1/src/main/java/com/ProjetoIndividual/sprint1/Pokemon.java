package com.ProjetoIndividual.sprint1;

import java.util.Date;

public class Pokemon {
    private Integer id;
    private String nome;
    private Integer nivel;
    private Date dataCaptura;
    private boolean shiny;
    private Integer tipo_id;
    private Integer time_id;

    public Pokemon(){

    }

    public Pokemon(Date dataCaptura, Integer id, Integer nivel, String nome, boolean shiny, Integer time_id, Integer tipo_id) {
        this.dataCaptura = dataCaptura;
        this.id = id;
        this.nivel = nivel;
        this.nome = nome;
        this.shiny = shiny;
        this.time_id = time_id;
        this.tipo_id = tipo_id;
    }

    public Date getDataCaptura() {
        return dataCaptura;
    }

    public void setDataCaptura(Date dataCaptura) {
        this.dataCaptura = dataCaptura;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isShiny() {
        return shiny;
    }

    public void setShiny(boolean shiny) {
        this.shiny = shiny;
    }

    public Integer getTime_id() {
        return time_id;
    }

    public void setTime_id(Integer time_id) {
        this.time_id = time_id;
    }

    public Integer getTipo_id() {
        return tipo_id;
    }

    public void setTipo_id(Integer tipo_id) {
        this.tipo_id = tipo_id;
    }
}
