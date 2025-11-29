package br.com.hospital.model;

import br.com.hospital.model.Agendamento;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Exame extends Agendamento {

    @Lob
    private byte[] exame;

    public Exame(Timestamp data, Paciente paciente, Medico medico, StatusAgendamento status) {
        super(data, paciente, medico, status);
    }

    @Override
    public String getTipo() {
        return "Exame";
    }

    @Override
    public <T extends Agendamento> void resultado(T agendamento, JpaRepository<T, Long> repository) {
        if(!agendamento.getTipo().equals("Exame")){
            return;
        }

    }
}
