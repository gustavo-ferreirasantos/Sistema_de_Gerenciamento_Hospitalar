package br.com.hospital.model;

import br.com.hospital.repository.AgendamentoRepository;
import br.com.hospital.repository.MedicoRepository;
import br.com.hospital.repository.PacienteRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Medico extends User{

    private String crm;
    @Enumerated(EnumType.STRING)
    private Especialidade especialidade;
    private int cargaHoraria;
    private int horasTrabalhadas = 0;


    public enum Especialidade{
        CLINICO_GERAL,
        CARDIOLOGISTA,
        PEDIATRA,
        GINECOLOGISTA,
        ORTOPEDISTA,
        DERMATOLOGISTA,
    }





    @Override
    public boolean autenticar(String email, String senha, JpaRepository<? extends User, Long> repository) {
        if (repository instanceof MedicoRepository medicoRepository) {
            Optional<Medico> p = medicoRepository.findByEmailIgnoreCase(email);
            return p.isPresent() && p.get().getPassword().equals(senha);
        }
        return false;
    }

    public boolean realizarAtendimento(AgendamentoRepository agendamentoRepository, PacienteRepository pacienteRepository, MedicoRepository medicoRepository) {
        return false;
    }

    public String Resultados(AgendamentoRepository agendamentoRepository){
        return null;
    }



}
