package br.com.hospital.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Informacoes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nome;
    private String descricao;
    private int tempoNecessario;

    @Enumerated(EnumType.STRING)
    private TipoAgendamento tipoAgendamento;

    @Enumerated(EnumType.STRING)
    private Medico.Especialidade especialidadeRelacionada;

    public enum TipoAgendamento{
        Exame,
        Consulta,
        Procedimento
    }
}
