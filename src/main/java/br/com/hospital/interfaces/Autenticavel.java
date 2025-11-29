package br.com.hospital.interfaces;

import br.com.hospital.model.User;
import br.com.hospital.repository.PacienteRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Autenticavel {

    // Para Paciente e Admin
    default boolean autenticar(String email, String senha, JpaRepository<? extends User, Long> repository){
        throw new UnsupportedOperationException("Este usuário não usa login sem CRM");
    }

    // Para Médico (com CRM)
    default boolean autenticar(String email, String senha, String crm, JpaRepository<? extends User, Long> repository){
        throw new UnsupportedOperationException("Este usuário não possui CRM");
    }

}

