package br.com.cbf.campeonatobrasileiro.reposiroty;

import br.com.cbf.campeonatobrasileiro.entity.Jogo;
import br.com.cbf.campeonatobrasileiro.entity.Time;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JogoRepository extends JpaRepository<Jogo, Integer> {

    List<Jogo> findByTime1AndEncerrado(Time time1, boolean encerrado);
    List<Jogo> findByTime2AndEncerrado(Time time2, boolean encerrado);
}

