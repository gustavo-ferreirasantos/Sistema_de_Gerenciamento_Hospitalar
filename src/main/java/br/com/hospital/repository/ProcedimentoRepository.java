package br.com.hospital.repository;

import br.com.hospital.model.Agendamento;
import br.com.hospital.model.Procedimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcedimentoRepository extends JpaRepository<Procedimento, Long> {
    List<Procedimento> findByMedicoId(Long id);
    List<Procedimento> findByPacienteId(Long id);
}