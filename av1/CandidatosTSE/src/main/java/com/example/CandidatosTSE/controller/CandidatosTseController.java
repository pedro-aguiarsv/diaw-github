package com.example.CandidatosTSE.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.CandidatosTSE.model.Candidato;
import com.example.CandidatosTSE.service.CandidatosTseService;

@Controller
public class CandidatosTseController {

    private final CandidatosTseService candidatosTseService;

    public CandidatosTseController(CandidatosTseService candidatosTseService) {
        this.candidatosTseService = candidatosTseService;
    }

    @GetMapping("/")
    public String index(@RequestParam(required = false) String cargo,
                        @RequestParam(required = false) String partido,
                        @RequestParam(required = false) String texto,
                        Model model) {

        if (cargo == null) {
            cargo = "";
        }
        if (partido == null) {
            partido = "";
        }
        if (texto == null) {
            texto = "";
        }

        List<Candidato> candidatos = candidatosTseService.filtrar(cargo, partido, texto);

        model.addAttribute("candidatos", candidatos);
        model.addAttribute("totalEncontrado", candidatos.size());
        model.addAttribute("cargos", candidatosTseService.listarCargos());
        model.addAttribute("partidos", candidatosTseService.listarPartidos());
        model.addAttribute("cargoSelecionado", cargo);
        model.addAttribute("partidoSelecionado", partido);
        model.addAttribute("textoSelecionado", texto);

        return "index";
    }
}
