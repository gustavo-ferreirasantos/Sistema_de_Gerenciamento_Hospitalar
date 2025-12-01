package br.com.hospital.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Consulta extends Agendamento {

    private String diagnostico;
    private boolean retornoNecessario;

    public Consulta(LocalDateTime data, Paciente paciente, Medico medico, Informacoes informacoes, StatusAgendamento status) {
        super(data, paciente, medico, informacoes, status);
    }




    @Override
    public String getTipo() {
        return "Consulta";
    }


    @Override
    public <T extends Agendamento> void resultado(T agendamento, JpaRepository<T, Long> repository) {}

    public <T extends Agendamento> void resultado(T agendamento, JpaRepository<T, Long> repository, String diagnostico, boolean retornoNecessario) {
        if (agendamento instanceof Consulta consulta) {
            consulta.retornoNecessario = retornoNecessario;
            consulta.diagnostico = diagnostico;
        }
        repository.save(agendamento);
    }
}