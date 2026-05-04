package com.pagape.api.controller;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Asegúrate que el nombre coincida (UserService o UsuarioService)
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pagape.api.model.Gasto;
import com.pagape.api.model.Usuario;
import com.pagape.api.repository.GastoRepository;
import com.pagape.api.repository.PerfilUsuarioGrupoRepository;
import com.pagape.api.service.GastoService;
import com.pagape.api.service.UserService;

@RestController
@RequestMapping("/expense")
@CrossOrigin(origins = "*")
public class GastoController {

    @Value("${storage.location}")
    private String storageLocation;

    @Autowired
    private GastoService gastoService;

    @Autowired
    private UserService usuarioService;

    @Autowired
    private GastoRepository gastoRepository;

    @Autowired
    private PerfilUsuarioGrupoRepository perfilRepository;

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

    /**
     * 1. LISTADO DE GASTOS Devuelve el JSON con los datos y la URL para cargar
     * la imagen.
     */
    @GetMapping("/group/{idGrupo}/confirmed")
    public ResponseEntity<?> listarGastosConfirmados(
            @PathVariable Integer idGrupo,
            Authentication authentication) {
        try {
            String email = authentication.getName();

            // Validación de seguridad en Linux
            boolean esMiembro = perfilRepository.existsById_IdUsuario_EmailAndId_IdGrupo(email, idGrupo);

            if (!esMiembro) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No eres miembro de este grupo.");
            }

            List<Gasto> gastos = gastoService.listarGastosConfirmadosPorGrupo(idGrupo);
            return ResponseEntity.ok(gastos);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    /**
     * 2. VISOR DE IMÁGENES SEGURO Este es el endpoint que el "frontend" llamará
     * para mostrar la foto.
     */
    @GetMapping("/download/{idGasto}")
    public ResponseEntity<?> descargarFotoGasto(
            @PathVariable Integer idGasto,
            Authentication authentication) {
        try {
            Gasto gasto = gastoRepository.findById(idGasto)
                    .orElseThrow(() -> new RuntimeException("Gasto inexistente"));

            // Verificación de membresía: solo si eres del grupo ves la foto
            String email = authentication.getName();
            Integer idGrupo = gasto.getPlanOrigen().getGrupo().getId();

            if (!perfilRepository.existsById_IdUsuario_EmailAndId_IdGrupo(email, idGrupo)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Lectura del archivo en el servidor Linux
            Path rutaFile = Paths.get(storageLocation).resolve(idGasto + ".jpg");
            Resource recurso = new UrlResource(rutaFile.toUri());

            if (recurso.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(recurso);
            }
            return ResponseEntity.notFound().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
