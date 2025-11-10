package br.com.hospital.interfaces;

import br.com.hospital.model.User;
import br.com.hospital.repository.PacienteRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Autenticavel {
    boolean autenticar(String email, String senha, JpaRepository<? extends User, Long> repository);
}
