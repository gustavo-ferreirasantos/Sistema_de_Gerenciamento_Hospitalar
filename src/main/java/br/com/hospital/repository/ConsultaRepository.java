package br.com.hospital.repository;

import br.com.hospital.model.Consulta;
import br.com.hospital.model.Paciente;
import br.com.hospital.model.Procedimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    List<Consulta> findByMedicoId(Long id);
    List<Consulta> findByPacienteId(Long id);
}