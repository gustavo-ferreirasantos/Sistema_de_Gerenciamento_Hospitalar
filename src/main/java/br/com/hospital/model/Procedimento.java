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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public <T extends Agendamento> void resultado(T agendamento, JpaRepository<T, Long> repository) {}

    public <T extends Agendamento> void resultado(T agendamento, JpaRepository<T, Long> repository, String remedios, String diagnostico, String riscos_observacoes) {
        if (agendamento instanceof Procedimento procedimento) {
            procedimento.setRemedios(remedios);
            procedimento.setDiagnostico(diagnostico);
            procedimento.setRiscos_observacoes(riscos_observacoes);
        }
        repository.save(agendamento);

    }

}
