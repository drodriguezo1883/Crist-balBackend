package com.cristobal.backend.service;

import com.cristobal.backend.model.Registro;
import com.cristobal.backend.repository.RegistroRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RegistroService {

    private final RegistroRepository repository;

    public RegistroService(RegistroRepository repository) {
        this.repository = repository;
    }

    public Registro guardar(Registro registro) {
        if (repository.existsByTelefonoAndCodigo(registro.getTelefono(), registro.getCodigo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Este código ya fue registrado con ese número de teléfono");
        }
        return repository.save(registro);
    }
}