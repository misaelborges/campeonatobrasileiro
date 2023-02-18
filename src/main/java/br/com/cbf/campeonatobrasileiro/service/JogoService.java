package br.com.cbf.campeonatobrasileiro.service;

import br.com.cbf.campeonatobrasileiro.dto.ClassificaoDTO;
import br.com.cbf.campeonatobrasileiro.dto.ClassificaoTimeDTO;
import br.com.cbf.campeonatobrasileiro.dto.JogoDTO;
import br.com.cbf.campeonatobrasileiro.dto.JogoFinalizadoDTO;
import br.com.cbf.campeonatobrasileiro.entity.Jogo;
import br.com.cbf.campeonatobrasileiro.entity.Time;
import br.com.cbf.campeonatobrasileiro.reposiroty.JogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class JogoService {

    @Autowired
    JogoRepository repository;

    @Autowired
    TimeService service;
    public void gerarJogos(LocalDateTime primeiraRodada) {
        final List<Time> times = service.findAll();
        List<Time> all1 = new ArrayList<>();
        List<Time> all2 = new ArrayList<>();
        all1.addAll(times);
        all2.addAll(times);

        repository.deleteAll();;

        List<Jogo> jogos = new ArrayList<>();

        int t = times.size();
        int m = times.size() / 2;
        LocalDateTime dataJogo = primeiraRodada;
        Integer rodada = 0;
        for (int i = 0; i < t - 1; i++) {
            rodada = i + 1;
            for (int j = 0; j < m; j++) {
                //Teste para ajustar o mando de campo
                Time time1;
                Time time2;
                if (j % 2 == 1 || i % 2 == 1 && j == 0) {
                    time1 = times.get(t - j - 1);
                    time2 = times.get(j);
                } else {
                    time1 = times.get(j);
                    time2 = times.get(t - j - 1);
                }
                if (time1 == null) {
                    System.out.println("Time 1 null");
                }
                jogos.add(gerarJogo(dataJogo, rodada, time1, time2));
                dataJogo = dataJogo.plusDays(7);
            }
            //Girar os times no sentido horário, mantendo o primeiro nolugar
            times.add(1, times.remove(times.size() - 1));
        }

        jogos.forEach(jogo -> System.out.println(jogo));

        repository.saveAll(jogos);

        List<Jogo> jogos2 = new ArrayList<>();

        jogos.forEach(jogo -> {
            Time time1 = jogo.getTime2();
            Time time2 = jogo.getTime1();
            jogos2.add(gerarJogo(jogo.getData().plusDays(7 * jogos.size()), jogo.getRodada() + jogos.size(), time1, time2));
        });
        repository.saveAll(jogos2);
    }

    private Jogo gerarJogo(LocalDateTime dataJogo, Integer rodada, Time time1, Time time2){
        Jogo jogo = new Jogo();
        jogo.setTime1(time1);
        jogo.setTime2(time2);
        jogo.setRodada(rodada);
        jogo.setData(dataJogo);
        jogo.setEncerrado(false);
        jogo.setGolsTime1(0);
        jogo.setGolsTime2(0);
        jogo.setPublicoPagante(0);
        return jogo;
    }

    private JogoDTO toDto(Jogo entity) {
        JogoDTO dto = new JogoDTO();
        dto.setId(entity.getId());
        dto.setData(entity.getData());
        dto.setEncerrado(entity.getEncerrado());
        dto.setGolsTime1(entity.getGolsTime1());
        dto.setGolsTime2(entity.getGolsTime2());
        dto.setPublicoPagante(entity.getPublicoPagante());
        dto.setRodada(entity.getRodada());
        dto.setTime1(service.toDto(entity.getTime1()));
        dto.setTime2(service.toDto(entity.getTime2()));
        return dto;
    }

    public List<JogoDTO> listarJogos() {
        return repository.findAll().stream().map(entity -> toDto(entity)).collect(Collectors.toList());
    }

    public JogoDTO finalizar(Integer id, JogoFinalizadoDTO jogoDTO) throws Exception {
        Optional<Jogo> optionalJogo = repository.findById(id);
        if (optionalJogo.isPresent()) {
            final Jogo jogo = optionalJogo.get();
            jogo.setGolsTime1(jogoDTO.getGolsTime1());
            jogo.setGolsTime2(jogoDTO.getGolsTime1());
            jogo.setEncerrado(true);
            jogo.setPublicoPagante(jogoDTO.getPublicoPagante());
            return toDto(repository.save(jogo));
        } else {
            throw new Exception("Jogo não existe");
        }
    }

    public ClassificaoDTO obterClassificacao() {
        ClassificaoDTO classificaoDTO = new ClassificaoDTO();
        final List<Time> times = service.findAll();

        times.forEach(time -> {
            final List<Jogo> jogosMandante = repository.findByTime1AndEncerrado(time, true);
            final List<Jogo> jogosVisitande = repository.findByTime2AndEncerrado(time, true);
            AtomicReference<Integer> vitorias = new AtomicReference<>(0);
            AtomicReference<Integer> empates = new AtomicReference<>(0);
            AtomicReference<Integer> derrotas = new AtomicReference<>(0);
            AtomicReference<Integer> golsSofridos = new AtomicReference<>(0);
            AtomicReference<Integer> golsMarcados = new AtomicReference<>(0);

            jogosMandante.forEach(jogo -> {
                if (jogo.getGolsTime1() > jogo.getGolsTime2()) {
                    vitorias.getAndSet(vitorias.get() + 1);
                } else if (jogo.getGolsTime1() < jogo.getGolsTime2()) {
                    derrotas.getAndSet(derrotas.get() + 1);
                } else {
                    empates.getAndSet(empates.get() + 1);
                }
                golsMarcados.updateAndGet(v -> v + jogo.getGolsTime1());
                golsSofridos.updateAndGet(v -> v + jogo.getGolsTime1());
            });

            jogosVisitande.forEach(jogo -> {
                if (jogo.getGolsTime2() > jogo.getGolsTime1()) {
                    vitorias.getAndSet(vitorias.get() + 1);
                } else if (jogo.getGolsTime2() < jogo.getGolsTime1()) {
                    derrotas.getAndSet(derrotas.get() + 1);
                } else {
                    empates.getAndSet(empates.get() + 1);
                }
                golsMarcados.updateAndGet(v -> v + jogo.getGolsTime1());
                golsSofridos.updateAndGet(v -> v + jogo.getGolsTime1());
            });

            ClassificaoTimeDTO classificaoTimeDTO = new ClassificaoTimeDTO();
            classificaoTimeDTO.setIdTime(time.getId());
            classificaoTimeDTO.setTimes(time.getNome());
            classificaoTimeDTO.setPontos((vitorias.get()*3) + empates.get());
            classificaoTimeDTO.setDerrotas(derrotas.get());
            classificaoTimeDTO.setEmpates(empates.get());
            classificaoTimeDTO.setVitorias(vitorias.get());
            classificaoTimeDTO.setGolsMarcados(golsMarcados.get());
            classificaoTimeDTO.setGolsSofridos(golsSofridos.get());
            classificaoTimeDTO.setJogos(derrotas.get() + empates.get() + vitorias.get());
            classificaoDTO.getTimes().add(classificaoTimeDTO);
        });

        Collections.sort(classificaoDTO.getTimes(), Collections.reverseOrder());
        int posicao = 0;
        for (ClassificaoTimeDTO time: classificaoDTO.getTimes()) {
            time.setPosicao(posicao++);
        }
        return classificaoDTO;
    }

    public JogoDTO obterJogo(Integer id) {
        return  toDto(repository.findById(id).get());
    }
}