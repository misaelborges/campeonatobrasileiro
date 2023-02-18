package br.com.cbf.campeonatobrasileiro.rest;

import br.com.cbf.campeonatobrasileiro.dto.TimeDTO;
import br.com.cbf.campeonatobrasileiro.service.TimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(value = "/times")
public class TimeRestController {

    @Autowired
    private TimeService service;

    @PostMapping
    public ResponseEntity<TimeDTO> cadastrarTime(@RequestBody TimeDTO time) throws Exception {
        return ResponseEntity.ok().body(service.cadastrarTime(time));
    }

    //@ApiOperation(value = "Obtem os dados um time")
    @GetMapping(value = "/{id}")
    public ResponseEntity<TimeDTO> getTimes(@PathVariable Integer id) {
        return ResponseEntity.ok().body(service.obterTime(id));
    }

    @GetMapping
    public ResponseEntity<List<TimeDTO>> listarTimes() {
        return ResponseEntity.ok().body(service.listarTime());
    }
}