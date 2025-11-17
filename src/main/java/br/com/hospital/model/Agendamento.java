package br.com.hospital.model;

import br.com.hospital.model.Medico;
import br.com.hospital.model.Paciente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)

public abstract class Agendamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Timestamp data;

    @ManyToOne
    private Paciente paciente;

    @ManyToOne
    private Medico medico;

    @Enumerated(EnumType.STRING)
    private StatusAgendamento status;

    public enum StatusAgendamento {
        AGENDADO, CANCELADO, CONCLUIDO
    }

    // Construtor padrão
    public Agendamento() {}

    // Construtor com todos os dados
    public Agendamento(Timestamp data, Paciente paciente, Medico medico, StatusAgendamento status) {
        this.data = data;
        this.paciente = paciente;
        this.medico = medico;
        this.status = status;
    }

}
