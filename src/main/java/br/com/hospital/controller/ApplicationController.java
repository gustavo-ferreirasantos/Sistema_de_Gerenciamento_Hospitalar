package br.com.hospital.controller;

import br.com.hospital.model.*;
import br.com.hospital.repository.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final Medico medicoService = new Medico();
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
            Model model) {

        if (pacienteService.autenticar(email, password, pacienteRepository)) {
            Long id = pacienteRepository.findByEmailIgnoreCase(email).get().getId();
            return "redirect:/dashboard/" + id;
        } else {
            return "redirect:/login?error";
        }
    }

    @PostMapping("/loginMedico")
    public String autenticarLoginMedico(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("crm") String crm,
            Model model) {

        if (medicoService.autenticar(email, password, crm, medicoRepository)) {
            Long id = medicoRepository.findByEmailIgnoreCase(email).get().getId();
            return "redirect:/painel/" + id;
        } else {
            return "redirect:/login?error";
        }
    }

    @PostMapping("/loginAdmin")
    public String autenticarLoginAdmin(
            @RequestParam("email") String email,
            @RequestParam("password") String password) {

        if (adminService.autenticar(email, password, adminRepository)) {
            return "redirect:/dashboardAdmin";
        } else {
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
        if (!adminService.adicionarPaciente(pacienteRepository, paciente)) {
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


    @GetMapping("/painel/{id}")
    public ModelAndView painel(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView("painelMedico");
        mv.addObject("medico", medicoRepository.findById(id).get());
        mv.addObject("CONSULTA_CANCELADO", medicoService.status(medicoRepository.findById(id).get(), "CONSULTA_CANCELADO", exameRepository, consultaRepository, procedimentoRepository));
        mv.addObject("CONSULTA_AGENDADO", medicoService.status(medicoRepository.findById(id).get(), "CONSULTA_AGENDADO", exameRepository, consultaRepository, procedimentoRepository));
        mv.addObject("CONSULTA_CONCLUIDO", medicoService.status(medicoRepository.findById(id).get(), "CONSULTA_CONCLUIDO", exameRepository, consultaRepository, procedimentoRepository));
        mv.addObject("EXAME_CANCELADO", medicoService.status(medicoRepository.findById(id).get(), "EXAME_CANCELADO", exameRepository, consultaRepository, procedimentoRepository));
        mv.addObject("EXAME_AGENDADO", medicoService.status(medicoRepository.findById(id).get(), "EXAME_AGENDADO", exameRepository, consultaRepository, procedimentoRepository));
        mv.addObject("EXAME_CONCLUIDO", medicoService.status(medicoRepository.findById(id).get(), "EXAME_CONCLUIDO", exameRepository, consultaRepository, procedimentoRepository));
        mv.addObject("PROCEDIMENTO_CANCELADO", medicoService.status(medicoRepository.findById(id).get(), "PROCEDIMENTO_CANCELADO", exameRepository, consultaRepository, procedimentoRepository));
        mv.addObject("PROCEDIMENTO_AGENDADO", medicoService.status(medicoRepository.findById(id).get(), "PROCEDIMENTO_AGENDADO", exameRepository, consultaRepository, procedimentoRepository));
        mv.addObject("PROCEDIMENTO_CONCLUIDO", medicoService.status(medicoRepository.findById(id).get(), "PROCEDIMENTO_CONCLUIDO", exameRepository, consultaRepository, procedimentoRepository));
        mv.addObject("consultasMedico", consultaRepository.findByMedicoId(id));
        mv.addObject("examesMedico", exameRepository.findByMedicoId(id));
        mv.addObject("procedimentosMedico", procedimentoRepository.findByMedicoId(id));
        return mv;
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

//    @GetMapping("/imagem/exame/{id}")
//    public void mostrarFotoExame(
//            @PathVariable Long id,
//            HttpServletResponse response) throws Exception {
//        Exame exame = exameRepository.findById(id).orElse(null);
//
//        if (exame != null && exame.getLaudo() != null) {
//            response.setContentType("image/jpeg");
//            response.getOutputStream().write(exame.getLaudo());
//            response.getOutputStream().close();
//        }
//    }

    @GetMapping("/imagem/exame/{id}")
    public void mostrarLaudoExame(
            @PathVariable Long id,
            HttpServletResponse response) throws Exception {

        Exame exame = exameRepository.findById(id).orElse(null);

        if (exame != null && exame.getLaudo() != null) {
            response.setContentType("application/pdf");
            response.getOutputStream().write(exame.getLaudo());
            response.getOutputStream().close();
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
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
        mv.addObject("consultas", consultaRepository.findAll());
        mv.addObject("exames", exameRepository.findAll());
        mv.addObject("procedimentos", procedimentoRepository.findAll());
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

    @GetMapping("/dashboard/{pacienteId}")
    public ModelAndView dashboard(@PathVariable("pacienteId") Long pacienteId) {
        ModelAndView mv = new ModelAndView("dashboard");
        mv.addObject("paciente", pacienteRepository.findById(pacienteId).get());
        mv.addObject("AGENDADO", pacienteService.status(pacienteRepository.findById(pacienteId).get(), "AGENDADO", exameRepository, consultaRepository, procedimentoRepository));
        mv.addObject("CONCLUIDO", pacienteService.status(pacienteRepository.findById(pacienteId).get(), "CONCLUIDO", exameRepository, consultaRepository, procedimentoRepository));
        mv.addObject("exames", exameRepository.findByPacienteId(pacienteId));
        mv.addObject("consultas", consultaRepository.findByPacienteId(pacienteId));
        mv.addObject("procedimentos", procedimentoRepository.findByPacienteId(pacienteId));
        return mv;
    }



    @GetMapping("/agendamento/{pacienteId}/tipo")
    public ModelAndView agendamentoTipo(@PathVariable Long pacienteId) {
        ModelAndView mv = new ModelAndView("agendamento-tipo");
        mv.addObject("pacienteId", pacienteId);
        return mv;
    }

    @GetMapping("/agendamento/{pacienteId}/especialidade")
    public ModelAndView agendamentoEspecialidade(@PathVariable Long pacienteId, @RequestParam String tipo) {
        ModelAndView mv = new ModelAndView("agendamento-especialidade");
        mv.addObject("pacienteId", pacienteId);
        mv.addObject("tipo", tipo);
        Informacoes.TipoAgendamento tipoEnum = Informacoes.TipoAgendamento.valueOf(tipo);
        mv.addObject("informacoes", informacoesRepository.findByTipoAgendamento(tipoEnum));
        return mv;
    }

    @GetMapping("/agendamento/{pacienteId}/medico")
    public ModelAndView agendamentoMedico(
            @PathVariable Long pacienteId,
            @RequestParam String tipo,
            @RequestParam String tipoEspecifico,
            @RequestParam Medico.Especialidade especialidade) {
        ModelAndView mv = new ModelAndView("agendamento-medico");
        mv.addObject("pacienteId", pacienteId);
        mv.addObject("tipo", tipo);
        mv.addObject("tipoEspecifico", tipoEspecifico);
        mv.addObject("medicos", medicoRepository.findByEspecialidade(especialidade));
        return mv;
    }



    @PostMapping("/agendar/{pacienteId}")
    public String finalizarAgendamento(
            @PathVariable Long pacienteId,
            @RequestParam String tipoAtendimento,
            @RequestParam String tipoEspecifico,
            @RequestParam String medico,
            @RequestParam String data,   // formato esperado: yyyy-MM-dd
            @RequestParam String hora,   // formato esperado: HH:mm
            RedirectAttributes redirect) {

        try {
            Paciente p = pacienteRepository.findById(pacienteId).orElseThrow();
            Medico m = medicoRepository.findByNome(medico).orElseThrow();
            Informacoes info = informacoesRepository.findByNome(tipoEspecifico).orElseThrow();

            // ---- CORREÇÃO DO TEMPO ----
            LocalDate date = LocalDate.parse(data);
            LocalTime time = LocalTime.parse(hora);
            LocalDateTime dataHora = LocalDateTime.of(date, time);

            switch (tipoAtendimento) {
                case "Exame" ->
                        pacienteService.agendar(p, m, dataHora, info, exameRepository);

                case "Procedimento" ->
                        pacienteService.agendar(p, m, dataHora, info, procedimentoRepository);

                case "Consulta" ->
                        pacienteService.agendar(p, m, dataHora, info, consultaRepository);

                default -> throw new RuntimeException("Tipo inválido");
            }

            return "redirect:/dashboard/" + pacienteId;

        } catch (Exception e) {
            redirect.addFlashAttribute("erro", "Erro: " + e.getMessage());
            return "redirect:/dashboard/" + pacienteId;
        }
    }



    @GetMapping("/atendimento/consulta/{id}")
    public ModelAndView atendimentoConsulta(@PathVariable Long id){
        ModelAndView mv = new ModelAndView("atendimentoConsulta");
        mv.addObject("agendamento", consultaRepository.findById(id).get());
        return mv;
    }

    @GetMapping("/atendimento/exame/{id}")
    public ModelAndView atendimentoExame(@PathVariable Long id){
        ModelAndView mv = new ModelAndView("atendimentoExame");
        mv.addObject("agendamento", exameRepository.findById(id).get());
        return mv;

    }

    @GetMapping("/atendimento/procedimento/{id}")
    public ModelAndView atendimentoProcedimento(@PathVariable Long id) {
        ModelAndView mv = new ModelAndView("atendimentoProcedimento");
        mv.addObject("agendamento", procedimentoRepository.findById(id).get());
        return mv;
    }


    @PostMapping("salvar/consulta/{id}")
    public String salvarConsulta(@PathVariable Long id, @RequestParam String diagnostico, @RequestParam(required = false) Boolean retornoNecessario) {
        Consulta c = consultaRepository.findById(id).get();
        medicoService.realizarAtendimento(c, consultaRepository);
        c.setDiagnostico(diagnostico);
        c.setRetornoNecessario(retornoNecessario);
        consultaRepository.save(c);
        return "redirect:/painel/" + c.getMedico().getId();
    }

    @PostMapping("salvar/exame/{id}")
    public String salvarExame(@PathVariable Long id, @RequestParam("resultado") MultipartFile resultado) throws IOException {
        Exame e = exameRepository.findById(id).get();
        medicoService.realizarAtendimento(e, exameRepository);
        byte[] arquivoBytes = resultado.getBytes();
        e.setLaudo(arquivoBytes);
        exameRepository.save(e);
        return "redirect:/painel/" + e.getMedico().getId();
    }



    @PostMapping("/salvar/procedimento/{id}")
    public String salvarProcedimento(@PathVariable Long id, @RequestParam String remedios, @RequestParam String diagnostico, @RequestParam String riscos_observacoes) {
        Procedimento p = procedimentoRepository.findById(id).get();
        medicoService.realizarAtendimento(p, procedimentoRepository);
        p.setDiagnostico(diagnostico);
        p.setRemedios(remedios);
        p.setRiscos_observacoes(riscos_observacoes);
        procedimentoRepository.save(p);
        return "redirect:/painel/" + p.getMedico().getId();
    }


    @GetMapping("/agendamentos/resultado/consulta/{id}")
    public ModelAndView resultadoConsulta(@PathVariable Long id) {
        Consulta c = consultaRepository.findById(id).orElse(null);

        ModelAndView mv = new ModelAndView("resultado");
        mv.addObject("tipo", "Consulta");
        mv.addObject("agendamento", c);

        return mv;
    }

    @GetMapping("/agendamentos/resultado/exame/{id}")
    public ModelAndView resultadoExame(@PathVariable Long id) {
        Exame e = exameRepository.findById(id).orElse(null);

        ModelAndView mv = new ModelAndView("resultado");
        mv.addObject("tipo", "Exame");
        mv.addObject("agendamento", e);

        return mv;
    }


    @GetMapping("/agendamentos/resultado/procedimento/{id}")
    public ModelAndView resultadoProcedimento(@PathVariable Long id) {
        Procedimento p = procedimentoRepository.findById(id).orElse(null);

        ModelAndView mv = new ModelAndView("resultado");
        mv.addObject("tipo", "Procedimento");
        mv.addObject("agendamento", p);

        return mv;
    }



    @GetMapping("/agendamentos/resultado/exame/laudo/{id}")
    public ResponseEntity<byte[]> visualizarLaudo(@PathVariable Long id) {
        Exame exame = exameRepository.findById(id).orElse(null);

        if (exame == null || exame.getLaudo() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "inline; filename=laudo.pdf")
                .body(exame.getLaudo());
    }


}
