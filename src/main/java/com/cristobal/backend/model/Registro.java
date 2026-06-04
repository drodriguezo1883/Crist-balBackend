package com.cristobal.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ\\s\\-]+$", message = "El nombre solo puede contener letras")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Pattern(regexp = "^[2678]\\d{7}$", message = "Ingrese un número de teléfono de Costa Rica válido")
    @Column(nullable = false, length = 8)
    private String telefono;

    @NotBlank
    @Pattern(regexp = "^\\d{12}$", message = "El código debe tener exactamente 12 dígitos")
    @Column(nullable = false, length = 12)
    private String codigo;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
    }
}