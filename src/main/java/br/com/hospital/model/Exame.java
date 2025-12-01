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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Exame extends Agendamento {

    @Lob
    private byte[] laudo;

    public Exame(LocalDateTime data, Paciente paciente, Medico medico, Informacoes informacoes ,StatusAgendamento status) {
        super(data, paciente, medico, informacoes, status);
    }

    @Override
    public String getTipo() {
        return "Exame";
    }

    @Override
    public <T extends Agendamento> void resultado(T agendamento, JpaRepository<T, Long> repository) {

    }

    public <T extends Agendamento> void resultado(T agendamento, JpaRepository<T, Long> repository, MultipartFile file) throws IOException {
        if (agendamento instanceof Exame exame) {
            exame.laudo = file.getBytes();
        }
        repository.save(agendamento);

    }
}
