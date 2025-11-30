package br.com.hospital.repository;

import br.com.hospital.model.Exame;
import br.com.hospital.model.Procedimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExameRepository extends JpaRepository<Exame, Long> {

}