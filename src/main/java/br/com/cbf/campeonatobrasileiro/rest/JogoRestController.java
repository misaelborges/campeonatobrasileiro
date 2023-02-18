package br.com.cbf.campeonatobrasileiro.rest;

import br.com.cbf.campeonatobrasileiro.dto.ClassificaoDTO;
import br.com.cbf.campeonatobrasileiro.dto.JogoDTO;
import br.com.cbf.campeonatobrasileiro.dto.JogoFinalizadoDTO;
import br.com.cbf.campeonatobrasileiro.service.JogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/jogos")
public class JogoRestController {

    @Autowired
    private JogoService service;
    @PostMapping(value = "/gerar-jogos")
    public ResponseEntity<Void> gerarJogos() {
        service.gerarJogos(LocalDateTime.now());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<JogoDTO>> obterJogos() {
        return ResponseEntity.ok().body(service.listarJogos());
    }

    @PostMapping(value = "finalizar/{id}")
    public ResponseEntity<JogoDTO> finalizar(@PathVariable Integer id, @RequestBody JogoFinalizadoDTO jogoDTO) throws Exception {
        return ResponseEntity.ok().body(service.finalizar(id, jogoDTO));
    }

    @GetMapping(value = "/classificacao")
    public ResponseEntity<ClassificaoDTO> classificacao() {
        return ResponseEntity.ok().body(service.obterClassificacao());
    }

    @GetMapping(value = "/jogo/{id}")
    public ResponseEntity<JogoDTO> obterJogo(@PathVariable Integer id) {
        return ResponseEntity.ok().body(service.obterJogo(id));
    }
}
