package br.com.hospital.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Consulta extends Agendamento {

    private String diagnostico;
    private boolean retornoNecessario;

    public Consulta(Timestamp data, Paciente paciente, Medico medico, StatusAgendamento status) {
        super(data, paciente, medico, status);
    }



    @Override
    public String getTipo() {
        return "Consulta";
    }

    @Override
    public <T extends Agendamento> void resultado(T agendamento, JpaRepository<T, Long> repository) {
        if(!agendamento.getTipo().equals("Consulta")){
            return;
        }


    }
}