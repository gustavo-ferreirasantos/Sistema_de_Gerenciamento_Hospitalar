package br.com.hospital.model;

import br.com.hospital.repository.AdminRepository;
import br.com.hospital.repository.MedicoRepository;
import br.com.hospital.repository.PacienteRepository;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Admin extends User {
    private String cargo;
    private String nivelAcesso;

    public Admin(String name, String email, String password, String cargo, String nivelAcesso) {
        super(name, email, password);
        this.cargo = cargo;
        this.nivelAcesso = nivelAcesso;
    }

    @Override
    public boolean autenticar(String email, String senha, JpaRepository<? extends User, Long> repository) {
        if (repository instanceof AdminRepository adminRepository) {
            Optional<Admin> p = adminRepository.findByEmailIgnoreCase(email);
            return p.isPresent() && p.get().getPassword().equals(senha);
        }
        return false;
    }


    public boolean adicionarPaciente(PacienteRepository pacienteRepository, Paciente paciente) {
        if (pacienteRepository.existsByCpf(paciente.getCpf())){
            //redirectAttributes.addFlashAttribute("erro", "CPF já cadastrado.");
            return false;
        }
        pacienteRepository.save(paciente);
        return true;

    }

    public void adicionarMedico(MedicoRepository medicoRepository, Medico medico) {
        medicoRepository.save(medico);
    }

    public void modficarSenha(String senha, AdminRepository adminRepository) {
        adminRepository.findById(1L).get().setPassword(senha);
    }

}

























