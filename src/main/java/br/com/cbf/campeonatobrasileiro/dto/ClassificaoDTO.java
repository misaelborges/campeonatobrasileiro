package br.com.cbf.campeonatobrasileiro.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ClassificaoDTO {

    private List<ClassificaoTimeDTO> times = new ArrayList<>();

}
