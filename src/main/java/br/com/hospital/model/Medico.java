package br.com.hospital.model;

// comparação das IDs dos médicos
import java.util.Objects;
import br.com.hospital.repository.AgendamentoRepository;
import br.com.hospital.repository.MedicoRepository;
import br.com.hospital.repository.PacienteRepository;
import br.com.hospital.model.Agendamento;
import br.com.hospital.model.Agendamento.StatusAgendamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Medico extends User{

    private String crm;
    private String cpf;
    @Enumerated(EnumType.STRING)
    private Especialidade especialidade;
    private float cargaHoraria;
    @Embedded
    Endereco endereco;
    @Lob
    private byte[] foto;
    private float horasTrabalhadas = 0;


    public enum Especialidade{
        CLINICO_GERAL,
        CARDIOLOGISTA,
        PEDIATRA,
        GINECOLOGISTA,
        ORTOPEDISTA,
        DERMATOLOGISTA,
    }



    public boolean disponivel(Medico medico, Informacoes informacoes){
        if(!(medico.getCargaHoraria() == medico.getHorasTrabalhadas())){
            if((medico.getHorasTrabalhadas() + informacoes.getTempoNecessario()/60) <= medico.getCargaHoraria()){
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean autenticar(String email, String senha, JpaRepository<? extends User, Long> repository) {
        if (repository instanceof MedicoRepository medicoRepository) {
            Optional<Medico> p = medicoRepository.findByEmailIgnoreCase(email);
            return p.isPresent() && p.get().getPassword().equals(senha);
        }
        return false;
    }

    // para o agendamento, não são necessários os repositórios de médico e paciente
    public boolean realizarAtendimento(Agendamento agendamento, AgendamentoRepository agendamentoRepository) {
        // verifica, primeiro, se o agendamento existe para que o atendimento seja realizado
        if (agendamento == null){
            return false;
        }

        // verifica se o médico do agendamento é o mesmo da classe
        if (!Objects.equals(agendamento.getMedico().getId(), this.getId())){
            return false;
        }

        // se o status do agendamento for "AGENDADO" e o médico for o mesmo, ele é concluído e salvo no repositório
        if (agendamento.getStatus() == StatusAgendamento.AGENDADO){
            // define o atendimento como CONCLUÍDO e salva no repositório
            agendamento.setStatus(StatusAgendamento.CONCLUIDO);
            agendamentoRepository.save(agendamento);

            return true;
        }
        
        // caso não tenha sido possível realizar o atendimento
        return false;
    }

    public String Resultados(AgendamentoRepository agendamentoRepository){
        return null;
    }



}
