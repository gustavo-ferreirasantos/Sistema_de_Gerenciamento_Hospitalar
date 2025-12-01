package br.com.hospital.model;

// comparação das IDs dos médicos
import java.util.List;
import java.util.Objects;

import br.com.hospital.interfaces.StatusInformavel;
import br.com.hospital.repository.*;
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
public class Medico extends User implements StatusInformavel {

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
    public boolean autenticar(String email, String senha, String crm, JpaRepository<? extends User, Long> repository) {
        if (repository instanceof MedicoRepository medicoRepository) {
            Optional<Medico> p = medicoRepository.findByEmailIgnoreCase(email);
            return p.isPresent()
                    && p.get().getPassword().equals(senha)
                    && p.get().getCrm().equals(crm);
        }
        return false;
    }

    // para o agendamento, não são necessários os repositórios de médico e paciente
    public <T extends Agendamento> void realizarAtendimento(T agendamento,  JpaRepository<T, Long> repository) {

        // se o status do agendamento for "AGENDADO" e o médico for o mesmo, ele é concluído e salvo no repositório
        if (agendamento.getStatus() == StatusAgendamento.AGENDADO){
            // define o atendimento como CONCLUÍDO e salva no repositório
            agendamento.setStatus(StatusAgendamento.CONCLUIDO);
            repository.save(agendamento);
        }

    }

    public <T extends Agendamento> void cancelarAtendimento(T agendamento, JpaRepository<T, Long> repository) {


        // se o status do agendamento for "AGENDADO" e o médico for o mesmo, ele é cancelado
        if (agendamento.getStatus() == StatusAgendamento.AGENDADO){
            agendamento.setStatus(StatusAgendamento.CANCELADO);
            repository.save(agendamento);
        }
    }



    public <T extends User> int status(
            T user,
            String opcao,
            ExameRepository exameRepository,
            ConsultaRepository consultaRepository,
            ProcedimentoRepository procedimentoRepository) {

        int contagem = 0;

        if (user.toString().equals("Medico")) {

            // Quebra EXAME_CONCLUIDO
            String tipo = opcao.split("_")[0];   // EXAME
            String status = opcao.split("_")[1]; // CONCLUIDO

            List<Exame> examesList = exameRepository.findAll();
            List<Consulta> consultasList = consultaRepository.findAll();
            List<Procedimento> procedimentosList = procedimentoRepository.findAll();

            // ----- EXAMES -----
            if (tipo.equals("EXAME")) {
                for (Exame exame : examesList) {
                    if (exame.getMedico() != null &&
                            exame.getMedico().getId().equals(user.getId()) &&
                            exame.getStatus().toString().equals(status)) {

                        contagem++;
                    }
                }
            }

            // ----- CONSULTAS -----
            if (tipo.equals("CONSULTA")) {
                for (Consulta consulta : consultasList) {
                    if (consulta.getMedico() != null &&
                            consulta.getMedico().getId().equals(user.getId()) &&
                            consulta.getStatus().toString().equals(status)) {

                        contagem++;
                    }
                }
            }

            // ----- PROCEDIMENTOS -----
            if (tipo.equals("PROCEDIMENTO")) {
                for (Procedimento procedimento : procedimentosList) {
                    if (procedimento.getMedico() != null &&
                            procedimento.getMedico().getId().equals(user.getId()) &&
                            procedimento.getStatus().toString().equals(status)) {

                        contagem++;
                    }
                }
            }
        }

        return contagem;
    }




    @Override
    public String toString() {
        return "Medico";
    }
}
