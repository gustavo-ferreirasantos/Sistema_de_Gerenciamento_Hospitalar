package br.com.hospital.controller;


import br.com.hospital.model.*;
import br.com.hospital.repository.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;

import java.util.Optional;

@Controller
public class ApplicationController {

    @Autowired
    private AgendamentoRepository agendamentoRepository;
    @Autowired
    private ProcedimentoRepository procedimentoRepository;
    @Autowired
    private ConsultaRepository consultaRepository;
    @Autowired
    private ExameRepository exameRepository;
    @Autowired
    private InformacoesRepository informacoesRepository;
    @Autowired
    private PacienteRepository pacienteRepository;
    @Autowired
    private MedicoRepository medicoRepository;
    @Autowired
    private AdminRepository adminRepository;



    private final Paciente pacienteService = new Paciente();
    private final Admin adminService = new Admin();

    @GetMapping("/")
    public ModelAndView home() {
        return new ModelAndView("home");
    }

    @GetMapping("/login")
    public ModelAndView loginForm() {
        return new ModelAndView("login");
    }

    @GetMapping("/loginAdmin")
    public ModelAndView login_admin() {
        return new ModelAndView("loginAdmin");
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
    public String loginAdmin(
            @RequestParam("email") String email,
            @RequestParam("password") String password) {

        if(adminService.autenticar(email, password, adminRepository)) {
            return "redirect:/dashboardAdmin";
        }else{
            return "redirect:/loginAdmin?error";
        }
    }



    @GetMapping("/registro")
    public ModelAndView registroForm() {
        ModelAndView mv = new ModelAndView("registro");
        mv.addObject("paciente", new Paciente());
        return mv;
    }

    @PostMapping("/registro")
    public String registro(Paciente paciente) {
        // verifica a duplicidade de CPFs
        if (!adminService.adicionarPaciente(pacienteRepository, paciente)){
            return "redirect:/registro?error";
        }
        return "redirect:/login";
    }

    @GetMapping("/registroMedico")
    public ModelAndView registroMedicoForm() {
        ModelAndView mv = new ModelAndView("registroMedico");
        mv.addObject("medico", new Medico());
        return mv;
    }

    @PostMapping("/registroMedico")
    public String registroMedico(@ModelAttribute Medico medico, @RequestParam("fotoUpload") MultipartFile foto) {
        try {
            if (!foto.isEmpty()) {
                medico.setFoto(foto.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        medicoRepository.save(medico);
        return "redirect:/dashboardAdmin";
    }


    @GetMapping("/imagem/medico/{id}")
    public void mostrarFotoMedico(
            @PathVariable Long id,
            HttpServletResponse response) throws Exception {

        Medico medico = medicoRepository.findById(id).orElse(null);

        if (medico != null && medico.getFoto() != null) {
            response.setContentType("image/jpeg");
            response.getOutputStream().write(medico.getFoto());
            response.getOutputStream().close();
        }
    }

    @GetMapping("/imagem/paciente/{id}")
    public void mostrarFotoPaciente(
            @PathVariable Long id,
            HttpServletResponse response) throws Exception {
        Paciente paciente = pacienteRepository.findById(id).orElse(null);

        if (paciente != null && paciente.getFoto() != null) {
            response.setContentType("image/jpeg");
            response.getOutputStream().write(paciente.getFoto());
            response.getOutputStream().close();
        }
    }


    @GetMapping("/cadastroServico")
    public ModelAndView cadastroServicoForm() {
        ModelAndView mv = new ModelAndView("cadastroServicos");
        mv.addObject("informacao", new Informacoes());
        return mv;
    }


    @GetMapping("/cadastroServico/{id}")
    public ModelAndView editarServico(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView("cadastroServicos");
        mv.addObject("informacao", informacoesRepository.findById(id).orElse(new Informacoes()));
        return mv;
    }


    @PostMapping("/cadastroServico")
    public String cadastroServico(@ModelAttribute Informacoes info) {
        informacoesRepository.save(info);
        return "redirect:/dashboardAdmin";
    }


    @GetMapping("/dashboardAdmin")
    public ModelAndView list() {
        ModelAndView mv = new ModelAndView("dashboardAdmin");
        mv.addObject("pacientes", pacienteRepository.findAll());
        mv.addObject("medicos", medicoRepository.findAll());
        mv.addObject("informacoes", informacoesRepository.findAll());
        return mv;
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView("registro");
        Optional<Paciente> clientFind = pacienteRepository.findById(id);
        clientFind.ifPresent(client -> mv.addObject("paciente", client));
        return mv;
    }

    @GetMapping("/edit/medico/{id}")
    public ModelAndView editMedico(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView("registroMedico");
        Optional<Medico> medicoFind = medicoRepository.findById(id);
        medicoFind.ifPresent(medico -> mv.addObject("medico", medico));
        return mv;
    }

    @GetMapping("/delete/paciente/{id}")
    public String deletePaciente(@PathVariable("id") Long id) {
        pacienteRepository.deleteById(id);
        return "redirect:/dashboardAdmin";
    }

    @GetMapping("/delete/medico/{id}")
    public String deleteMedico(@PathVariable("id") Long id) {
        medicoRepository.deleteById(id);
        return "redirect:/dashboardAdmin";
    }

    @GetMapping("/delete/servico/{id}")
    public String deleteServico(@PathVariable("id") Long id) {
        informacoesRepository.deleteById(id);
        return "redirect:/dashboardAdmin";
    }


    @GetMapping("/dashboard/{id}")
    public ModelAndView dashboard(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView("dashboard");
        mv.addObject("pacienteId", id);
        mv.addObject("tiposAtendimento", informacoesRepository.findDistinctTipos());
        mv.addObject( "paciente", pacienteRepository.findById(id).get());
        return mv;
    }

    @GetMapping("/painelMedico")
    public ModelAndView painel() {
        ModelAndView mv = new ModelAndView("painelMedico");
        mv.addObject("paciente", pacienteRepository.findAll());
        return mv;
    }

    @PostMapping("/salvarProcedimento")
    public String salvarProcedimento(@RequestParam("imagem") MultipartFile imagemFile) throws Exception {

        Procedimento p = new Procedimento();

        if (!imagemFile.isEmpty()) {
            p.setImagem(imagemFile.getBytes());
        }

        procedimentoRepository.save(p);

        return "redirect:/procedimentos";
    }

   @GetMapping("procedimentos")
   public ModelAndView listProcedimento() {
        return new ModelAndView("procedimentos");
   }


    @GetMapping("/imagem/procedimento/{id}")
    public void mostrarImagem(@PathVariable long id, HttpServletResponse response) throws Exception {
        Procedimento p = procedimentoRepository.findById(id).orElse(null);

        if (p != null && p.getImagem() != null) {
            response.setContentType("image/jpeg");
            response.getOutputStream().write(p.getImagem());
            response.getOutputStream().close();
        }
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




    @GetMapping("/agendar/{idPaciente}/1")
    public ModelAndView agendarEtapa1(
            @PathVariable Long idPaciente,
            @RequestParam(required = false) String tipoAtendimento
    ) {
        ModelAndView mv = new ModelAndView("agendar");

        mv.addObject("pacienteId", idPaciente);
        mv.addObject("tiposAtendimento", informacoesRepository.findDistinctTipos());
        mv.addObject("tipoSelecionado", tipoAtendimento);

        if (tipoAtendimento != null) {
            mv.addObject("tiposEspecificos",
                    informacoesRepository.findByEspecialidadeRelacionada(tipoAtendimento));
        }

        return mv;
    }



    @GetMapping("/agendar/{idPaciente}/2")
    public ModelAndView agendarEtapa2(
            @PathVariable Long idPaciente,
            @RequestParam String tipoAtendimento,
            @RequestParam(required = false) String tipoEspecifico
    ) {
        ModelAndView mv = new ModelAndView("agendar");

        mv.addObject("pacienteId", idPaciente);
        mv.addObject("tipoSelecionado", tipoAtendimento);
        mv.addObject("tiposEspecificos",
                informacoesRepository.findByEspecialidadeRelacionada(tipoAtendimento));

        if (tipoEspecifico != null) {
            mv.addObject("tipoEspecificoSelecionado", tipoEspecifico);
            mv.addObject("medicos", medicoRepository.findByEspecialidade(tipoAtendimento));
        }

        return mv;
    }



    @PostMapping("/agendar")
    public String finalizarAgendamento(
            @RequestParam Long pacienteId,
            @RequestParam String tipoAtendimento,
            @RequestParam String tipoEspecifico,
            @RequestParam String medico,
            @RequestParam String data,
            @RequestParam String hora,
            RedirectAttributes redirect
    ) {
        try {
            Paciente p = pacienteRepository.findById(pacienteId).orElseThrow();
            Medico m = medicoRepository.findByNome(medico).orElseThrow();
            Informacoes info = informacoesRepository.findByNome(tipoEspecifico).orElseThrow();

            Timestamp ts = Timestamp.valueOf(data + " " + hora + ":00");

            boolean sucesso = switch (tipoAtendimento) {
                case "Exame" -> pacienteService.agendar(p, m, ts, info, exameRepository);
                case "Procedimento" -> pacienteService.agendar(p, m, ts, info, procedimentoRepository);
                case "Consulta" -> pacienteService.agendar(p, m, ts, info, consultaRepository);
                default -> throw new RuntimeException("Tipo inválido");
            };

            return "redirect:/dashboard/" + pacienteId;

        } catch (Exception e) {
            redirect.addFlashAttribute("erro", "Erro: " + e.getMessage());
            return "redirect:/dashboard/" + pacienteId;
        }
    }



}
