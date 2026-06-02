package com.cristobal.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "registros")
@Getter
@Setter
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ\\s\\-]+$", message = "El nombre solo puede contener letras")
    @Column(nullable = false)
    private String nombre;

    @NotBlank
    @Pattern(regexp = "^[2678]\\d{7}$", message = "Ingrese un número de teléfono de Costa Rica válido")
    @Column(nullable = false)
    private String telefono;

    @NotBlank
    @Pattern(regexp = "^\\d+$", message = "El código debe contener solo números")
    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
    }
}