package br.com.hospital.repository;

import br.com.hospital.model.Procedimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultaRepository extends JpaRepository<Procedimento, Long> {

}