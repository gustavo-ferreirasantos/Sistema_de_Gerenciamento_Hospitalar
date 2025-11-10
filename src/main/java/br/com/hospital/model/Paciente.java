package br.com.hospital.model;


import br.com.hospital.repository.AgendamentoRepository;
import br.com.hospital.repository.PacienteRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;


import java.sql.Timestamp;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Paciente extends User {

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


    public boolean agendar(Paciente paciente, Medico medico, Timestamp data, Informacoes informacoes, AgendamentoRepository repository) {
        int tempoNecessario = informacoes.getTempoNecessario();
        int horasTrabalhadas = medico.getHorasTrabalhadas();

        // Verifica se o médico ainda tem disponibilidade
        if (tempoNecessario + horasTrabalhadas <= medico.getCargaHoraria()) {
            medico.setHorasTrabalhadas(horasTrabalhadas + tempoNecessario);

            Agendamento agendamento = switch (informacoes.getTipoAgendamento()) {
                case Exame -> new Exame(data, paciente, medico, Agendamento.StatusAgendamento.AGENDADO);
                case Consulta -> new Consulta(data, paciente, medico, Agendamento.StatusAgendamento.AGENDADO);
                case Procedimento -> new Procedimento(data, paciente, medico, Agendamento.StatusAgendamento.AGENDADO);
                default ->
                        throw new IllegalArgumentException("Tipo de agendamento desconhecido: " + informacoes.getTipoAgendamento());
            };

            // Cria o tipo de agendamento com base no enum dentro de Informacoes
            repository.save(agendamento);
            return true;
        }
        return false;
    }










}
