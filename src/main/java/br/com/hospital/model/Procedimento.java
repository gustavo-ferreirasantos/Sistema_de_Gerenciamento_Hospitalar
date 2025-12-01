package br.com.hospital.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Procedimento extends Agendamento {

    private String remedios;
    private String diagnostico;
    private String riscos_observacoes;

    public Procedimento(LocalDateTime data, Paciente paciente, Medico medico, Informacoes informacoes, StatusAgendamento status) {
        super(data, paciente, medico, informacoes, status);
    }


    @Override
    public String getTipo() {
        return "Procedimento";
    }

    @Override
    public <T extends Agendamento> void resultado(T agendamento, JpaRepository<T, Long> repository) {
        if(!agendamento.getTipo().equals("Procedimento")){
            return;
        }

    }
}
