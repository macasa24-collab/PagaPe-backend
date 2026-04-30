package com.pagape.api.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Asegúrate que el nombre coincida (UserService o UsuarioService)
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pagape.api.model.Gasto;
import com.pagape.api.model.Usuario;
import com.pagape.api.service.GastoService;
import com.pagape.api.service.UserService;

@RestController
@RequestMapping("/expense")
@CrossOrigin(origins = "*")
public class GastoController {

    @Autowired
    private GastoService gastoService;

    @Autowired
    private UserService usuarioService;

    @PostMapping("/{idPlan}/new") // Ajusta la ruta a /plans/{idPlan}/expense si quieres ser fiel al Flutter
    public ResponseEntity<?> registrarGasto(
            @PathVariable Integer idPlan,
            @RequestParam("importe") BigDecimal importe,
            @RequestParam("concepto") String concepto,
            @RequestParam(value = "ticket", required = false) MultipartFile archivo,
            Authentication authentication) {
        try {
            String emailUsuario = authentication.getName();
            Usuario pagador = usuarioService.obtenerPorEmail(emailUsuario);

            if (pagador == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no válido");
            }

            // Llamamos al servicio pasando los datos sueltos
            Gasto nuevoGasto = gastoService.crearGasto(idPlan, pagador, importe, concepto, archivo);

            // Devolvemos el objeto Expense que el front espera mapear
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoGasto);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
