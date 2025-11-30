package br.com.hospital.interfaces;

import br.com.hospital.model.User;
import br.com.hospital.repository.ConsultaRepository;
import br.com.hospital.repository.ExameRepository;
import br.com.hospital.repository.ProcedimentoRepository;

public interface StatusInformavel {
    <T extends User> int status(T usuario, String opcao, ExameRepository exameRepository, ConsultaRepository consultaRepository, ProcedimentoRepository procedimentoRepository);
}
