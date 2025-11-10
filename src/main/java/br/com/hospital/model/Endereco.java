package br.com.hospital.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Endereco {
    private String localidade;
    private String logradouro;
    private String bairro;
    private String estado;
    private int number;
    private String cep;
}
