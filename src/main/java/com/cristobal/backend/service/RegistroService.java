package com.cristobal.backend.service;

import com.cristobal.backend.model.Registro;
import com.cristobal.backend.repository.RegistroRepository;
import org.springframework.stereotype.Service;

@Service
public class RegistroService {

    private final RegistroRepository repository;

    public RegistroService(RegistroRepository repository) {
        this.repository = repository;
    }

    public Registro guardar(Registro registro) {
        return repository.save(registro);
    }
}