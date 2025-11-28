package br.com.hospital.repository;

import br.com.hospital.model.Informacoes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InformacoesRepository extends JpaRepository<Informacoes, Long> {

    Optional<Informacoes> findByNome(String nome);
    List<Informacoes> findByEspecialidadeRelacionada(String especialidade);

    List<Informacoes> findByTipoAgendamento(Informacoes.TipoAgendamento tipoAgendamento);

    @Query("SELECT DISTINCT i.tipoAgendamento FROM Informacoes i")
    List<Informacoes.TipoAgendamento> findDistinctTipos();
}
