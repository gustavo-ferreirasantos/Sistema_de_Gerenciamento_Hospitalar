package br.com.hospital.repository;

import br.com.hospital.model.Exame;
import br.com.hospital.model.Procedimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExameRepository extends JpaRepository<Exame, Long> {
    List<Exame> findByMedicoId(Long id);
    List<Exame> findByPacienteId(Long id);
}