package br.com.hospital.interfaces;

import br.com.hospital.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Agendavel {

    <T extends Agendamento> void resultado(T agendamento, JpaRepository<T, Long> repository);
    //void status();
}
