package br.com.hospital.repository;

import br.com.hospital.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

}
