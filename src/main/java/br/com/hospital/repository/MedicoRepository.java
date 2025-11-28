package br.com.hospital.repository;

import br.com.hospital.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    Optional<Medico> findByEmailIgnoreCase(String email);
    Optional<Medico> findByNome(String nome);
    List<Medico> findByEspecialidade(Medico.Especialidade especialidade);
    Optional<Medico> findByCrm(String crm);
}
