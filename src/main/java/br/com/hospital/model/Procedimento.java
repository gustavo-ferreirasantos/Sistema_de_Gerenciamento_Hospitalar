package br.com.hospital.model;

import jakarta.persistence.Column;
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
@AllArgsConstructor
@Getter
@Setter
public class Procedimento extends Agendamento {

    @Lob
    private byte[] imagem;
    private String descricao;

    public Procedimento(Timestamp data, Paciente paciente, Medico medico, StatusAgendamento status) {
        super(data, paciente, medico, status);
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
