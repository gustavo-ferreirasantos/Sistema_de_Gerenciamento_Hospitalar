package br.com.hospital.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@DiscriminatorValue("Procedimento")
@Getter
@Setter
public class Procedimento extends Agendamento {

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imagem;


    public Procedimento() {
        super();
    }


    public Procedimento(Timestamp data, Paciente paciente, Medico medico, StatusAgendamento status) {
        super(data, paciente, medico, status);
    }
}
