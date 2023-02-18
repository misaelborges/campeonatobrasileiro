package br.com.cbf.campeonatobrasileiro.dto;

import lombok.Data;

@Data
public class ClassificaoTimeDTO implements Comparable<ClassificaoTimeDTO> {
    private String times;
    private Integer idTime;
    private Integer posicao;
    private Integer pontos;
    private Integer jogos;
    private Integer vitorias;
    private Integer empates;
    private Integer derrotas;
    private Integer golsMarcados;
    private Integer golsSofridos;

    @Override
    public int compareTo(ClassificaoTimeDTO classificaoTimeDTO) {
        return this.getPontos().compareTo(classificaoTimeDTO.getPontos());
    }
}
