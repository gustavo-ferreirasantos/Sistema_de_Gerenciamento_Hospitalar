package br.com.hospital.repository;

import br.com.hospital.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByEmailIgnoreCase(String email);

    // verifica se já existe algum paciente com o mesmo cpf no repositório
    boolean existsByCpf(String cpf);
}
