package com.cristobal.backend.controller;

import com.cristobal.backend.model.Registro;
import com.cristobal.backend.service.RegistroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registros")
public class RegistroController {

    private final RegistroService service;

    public RegistroController(RegistroService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Registro> crear(@Valid @RequestBody Registro registro) {
        Registro guardado = service.guardar(registro);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }
}