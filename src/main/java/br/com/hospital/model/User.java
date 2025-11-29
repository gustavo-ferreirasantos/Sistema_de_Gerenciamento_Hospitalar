package br.com.hospital.model;

import br.com.hospital.interfaces.Autenticavel;
import br.com.hospital.repository.PacienteRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class User implements Autenticavel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String password;


    public abstract boolean autenticar(String email, String senha, JpaRepository<? extends User, Long> repository);

    public User(String name, String email, String password) {
        this.nome = nome;
        setEmail(email);
        setPassword(password);
    }

    // Setter personalizado para o e-mail
    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email inválido!");
        }
        this.email = email.trim().toLowerCase();
    }

    // Setter personalizado para a senha
    public void setPassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres!");
        }
        this.password = password;
    }



}







