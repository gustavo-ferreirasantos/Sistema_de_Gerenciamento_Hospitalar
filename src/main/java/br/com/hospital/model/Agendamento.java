package br.com.hospital.model;

import br.com.hospital.interfaces.Agendavel;
import br.com.hospital.model.Medico;
import br.com.hospital.model.Paciente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class Agendamento implements Agendavel {
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

    public abstract <T extends Agendamento>  void resultado(T agendamento, JpaRepository<T, Long> repository);


    public abstract String getTipo();

    // Construtor com todos os dados
    public Agendamento(Timestamp data, Paciente paciente, Medico medico, StatusAgendamento status) {
        this.data = data;
        this.paciente = paciente;
        this.medico = medico;
        this.status = status;
    }

}
