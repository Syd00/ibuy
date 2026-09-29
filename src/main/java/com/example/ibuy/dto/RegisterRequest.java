package com.example.ibuy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest (
    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Formato email non valido")
    String email,

    @NotBlank(message = "Lo username è obbligatorio")
    @Size(min = 3, max = 20, message = "Lo username deve contenere tra 3 e 20 caratteri")
    String username,

    @NotBlank(message = "La password è obbligatoria")
    @Size(min = 4, message = "La password deve contenere almeno 4 caratteri")
    String pass
){}
