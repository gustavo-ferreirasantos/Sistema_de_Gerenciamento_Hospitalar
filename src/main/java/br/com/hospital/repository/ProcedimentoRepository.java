package br.com.hospital.repository;

import br.com.hospital.model.Agendamento;
import br.com.hospital.model.Procedimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcedimentoRepository extends JpaRepository<Procedimento, Long> {

}