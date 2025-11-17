package br.com.hospital.controller;


import br.com.hospital.DTO.Autenticacao;
import br.com.hospital.model.Admin;
import br.com.hospital.model.Informacoes;
import br.com.hospital.model.Medico;
import br.com.hospital.model.Paciente;
import br.com.hospital.repository.AgendamentoRepository;
import br.com.hospital.repository.InformacoesRepository;
import br.com.hospital.repository.MedicoRepository;
import br.com.hospital.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.sql.Timestamp;

import java.util.Optional;

@Controller
public class ApplicationController {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private InformacoesRepository informacoesRepository;

    private final Paciente pacienteService = new Paciente();
    private final Admin adminService = new Admin();

    @GetMapping("/")
    public ModelAndView home() {
        return new ModelAndView("home");
    }

    @GetMapping("/login")
    public ModelAndView loginForm() {
        ModelAndView mv = new ModelAndView("login");
        return mv;
    }

    @GetMapping("/loginAdmin")
    public ModelAndView login_admin() {
        ModelAndView mv = new ModelAndView("login_admin");
        mv.addObject("autenticacao", new Autenticacao());
        return mv;
    }

    @PostMapping("/login")
    public String autenticarLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes) {

        if(pacienteService.autenticar(email, password, pacienteRepository)) {
            Long id = pacienteRepository.findByEmailIgnoreCase(email).get().getId();
            return "redirect:/dashboard/" + id;
        }else{
            return "redirect:/login?error";
        }
    }

    @PostMapping("/loginAdmin")
    public String login_admin(@ModelAttribute Autenticacao autenticacao) {
        Paciente service = new Paciente();
        if(autenticacao.getEmail().equals("admin@gmail.com")) {
            return "redirect:/list";
        }else {
            return "redirect:/login/admin?error";
        }
    }

    @GetMapping("/register")
    public ModelAndView registerForm() {
        ModelAndView mv = new ModelAndView("register");
        mv.addObject("paciente", new Paciente());
        return mv;
    }

    @PostMapping("/register")
    public String register(Paciente paciente) {
        // verifica a duplicidade de CPFs
        if (pacienteRepository.existsByCpf(paciente.getCpf())){
            redirectAttributes.addFlashAttribute("erro", "CPF já cadastrado.");
            return "redirect:/register?error";
        }
        
        adminService.adicionarPaciente(pacienteRepository, paciente);
        return "redirect:/lista";
    }

    @GetMapping("/registroMedico")
    public ModelAndView registroMedicoForm() {
        ModelAndView mv = new ModelAndView("registroMedico");
        mv.addObject("medico", new Medico());
        return mv;
    }

    @PostMapping("/registroMedico")
    public String registroMedico(@ModelAttribute Medico medico) {
        medicoRepository.save(medico);
        return "redirect:/listaMedicos";
    }

    @GetMapping("/list")
    public ModelAndView list() {
        ModelAndView mv = new ModelAndView("lista");
        mv.addObject("pacientes", pacienteRepository.findAll());
        return mv;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView("register");
        Optional<Paciente> clientFind = pacienteRepository.findById(id);
        clientFind.ifPresent(client -> mv.addObject("paciente", client));
        return mv;
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        pacienteRepository.deleteById(id);
        return "redirect:/list";
    }

    @GetMapping("/dashboard/{id}")
    public ModelAndView dashboard(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView("dashboard");
        mv.addObject("pacienteId", id);
        mv.addObject("tiposAtendimento", informacoesRepository.findDistinctTipos());
        mv.addObject( "paciente", pacienteRepository.findById(id).get());
        return mv;
    }

    @GetMapping("/painel")
    public ModelAndView painel() {
        ModelAndView mv = new ModelAndView("painelMedico");
        mv.addObject("paciente", pacienteRepository.findAll());
        return mv;
    }




    @GetMapping("/agendar/{idPaciente}/tipo")
    public ModelAndView selecionarTipoAtendimento(
            @PathVariable("idPaciente") Long idPaciente,
            @RequestParam("tipoAtendimento") String tipoAtendimento) {

        ModelAndView mv = new ModelAndView("dashboard");

        Optional<Paciente> pacienteOpt = pacienteRepository.findById(idPaciente);
        if (pacienteOpt.isEmpty()) {
            mv.setViewName("redirect:/login?erro=paciente_nao_encontrado");
            return mv;
        }
        mv.addObject("mostrarAgendar", true);


        mv.addObject("tipoSelecionado", tipoAtendimento);
        // Carrega os médicos disponíveis para o tipo escolhido
        mv.addObject("medicos", medicoRepository.findByEspecialidade(tipoAtendimento));



        // Carrega as subcategorias ou tipos específicos (ex: tipo de exame)
        mv.addObject("tiposEspecificos", informacoesRepository.findByEspecialidadeRelacionada(tipoAtendimento));

        return mv;
    }




    @PostMapping("/agendar")
    public String agendar(
            @RequestParam("pacienteId") int pacienteId,
            @RequestParam("tipoAtendimento") String tipoAtendimento,
            @RequestParam("tipoEspecifico") String tipoEspecifico,
            @RequestParam("medico") String nomeMedico,
            @RequestParam("data") String data,
            @RequestParam("hora") String hora,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Paciente paciente = pacienteRepository.findById((long) pacienteId).orElseThrow();
            Medico medico = medicoRepository.findByNome(nomeMedico).orElseThrow();

            // Busca o tipo de informação (Exame, Consulta, Procedimento)
            Informacoes informacoes = informacoesRepository.findByNome(tipoEspecifico).orElseThrow();

            // Monta a data (aqui você pode ajustar o formato conforme necessário)
            Timestamp dataCompleta = java.sql.Timestamp.valueOf(data + " " + hora + ":00");

            boolean sucesso = pacienteService.agendar(paciente, medico, dataCompleta, informacoes, agendamentoRepository);

            return "redirect:/dashboard/" + pacienteId;

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("erro", "❌ Erro ao realizar agendamento: " + e.getMessage());
            return "redirect:/dashboard/" + pacienteId;
        }
    }


}
