package br.com.hospital.model;


import br.com.hospital.interfaces.StatusInformavel;
import br.com.hospital.repository.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Paciente extends User implements StatusInformavel {

    @Embedded
    private Endereco endereco;
    private String cpf;


    public Paciente(int id, String name, String email, String password, Endereco endereco, String cpf) {
        super(name, email, password);
        this.endereco = endereco;
        setCpf(cpf);
    }


    //Exemplo de importância do encapsulamento: Fazer checagem de CPF
    public void setCpf(String cpf) {
        try {
            if (validateCpf(cpf)) {
                this.cpf = formatCpf(cpf);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Cpf inválido");
        }
    }

    //Isso torna impossível colocar CPFs que não existem
    public static boolean validateCpf(String cpf) {
        if (cpf == null) return false;

        // Mantém apenas dígitos
        String digits = cpf.replaceAll("\\D", "");
        if (digits.length() != 11) return false;

        // Rejeita sequências repetidas tipo "00000000000"
        if (digits.chars().distinct().count() == 1) return false;

        // Calcula 1º dígito verificador (pesos 10..2)
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int n = digits.charAt(i) - '0';
            sum += n * (10 - i);
        }
        int d1 = 11 - (sum % 11);
        if (d1 >= 10) d1 = 0;

        // Calcula 2º dígito verificador (pesos 11..2)
        sum = 0;
        for (int i = 0; i < 10; i++) {
            int n = digits.charAt(i) - '0';
            sum += n * (11 - i);
        }
        int d2 = 11 - (sum % 11);
        if (d2 >= 10) d2 = 0;

        // Confere com os dígitos do CPF
        return (digits.charAt(9) - '0') == d1 && (digits.charAt(10) - '0') == d2;
    }

    // Formata o Cpf no formato: 000.000.000-00
    public static String formatCpf(String cpf) {
        // Mantém apenas dígitos
        String digits = cpf.replaceAll("\\D", "");
        if (digits.length() != 11) return cpf; // retorna como está se não tiver 11 dígitos
        //Retorna o CPF no formato XXX.XXX.XXX-XX
        return digits.substring(0, 3) + "." +
                digits.substring(3, 6) + "." +
                digits.substring(6, 9) + "-" +
                digits.substring(9);
    }


    // Autentica o login do paciente, usando o email e senha
    @Override
    public boolean autenticar(String email, String senha, JpaRepository<? extends User, Long> repository) {
        if (repository instanceof PacienteRepository pacienteRepository) {
            Optional<Paciente> p = pacienteRepository.findByEmailIgnoreCase(email);
            return p.isPresent() && p.get().getPassword().equals(senha);
        }
        return false;
    }

    public Paciente buscarPaciente(PacienteRepository repository, Long id) {
        Optional<Paciente> p = repository.findById(id);
        return p.isPresent() ? p.get() : null;
    }

    public <T extends User> int status(
            T user,
            String opcao,
            ExameRepository exameRepository,
            ConsultaRepository consultaRepository,
            ProcedimentoRepository procedimentoRepository) {

        int contagem = 0;

        if(user.toString().equals("Paciente")){

            // Listas carregadas dos repositórios
            List<Exame> examesList = exameRepository.findAll();
            List<Consulta> consultasList = consultaRepository.findAll();
            List<Procedimento> procedimentosList = procedimentoRepository.findAll();

            // ----- EXAMES -----
            for (Exame exame : examesList) {
                if (exame.getPaciente() != null &&
                        exame.getPaciente().getId().equals(user.getId()) &&
                        exame.getStatus().toString().equals(opcao)) {
                    contagem++;
                }
            }

            // ----- CONSULTAS -----
            for (Consulta consulta : consultasList) {
                if (consulta.getPaciente() != null &&
                        consulta.getPaciente().getId().equals(user.getId()) &&
                        consulta.getStatus().toString().equals(opcao)) {

                    contagem++;
                }
            }

            // ----- PROCEDIMENTOS -----
            for (Procedimento procedimento : procedimentosList) {
                if (procedimento.getPaciente() != null &&
                        procedimento.getPaciente().getId().equals(user.getId()) &&
                        procedimento.getStatus().toString().equals(opcao)) {

                    contagem++;
                }
            }
        }


        return contagem;
    }


    public <T extends Agendamento> boolean agendar(
            Paciente paciente,
            Medico medico,
            LocalDateTime data,
            Informacoes informacoes,
            JpaRepository<T, Long> repository) {

        // Disponibilidade
        if (!medico.disponivel(medico, informacoes)) {
            return false;
        }

        medico.setHorasTrabalhadas(
                medico.getHorasTrabalhadas() + informacoes.getTempoNecessario()/60
        );

        T novoAgendamento = switch (informacoes.getTipoAgendamento()) {
            case Consulta -> (T) new Consulta(data, paciente, medico, informacoes, Agendamento.StatusAgendamento.AGENDADO);

            case Exame -> (T) new Exame(data, paciente, medico, informacoes, Agendamento.StatusAgendamento.AGENDADO);

            case Procedimento -> (T) new Procedimento(data, paciente, medico, informacoes, Agendamento.StatusAgendamento.AGENDADO);
        };

        repository.save(novoAgendamento);
        return true;
    }


    @Override
    public String toString() {
        return "Paciente";
    }
}
