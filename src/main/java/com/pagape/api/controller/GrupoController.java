package com.pagape.api.controller;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pagape.api.dto.request.GrupoRequest;
import com.pagape.api.dto.request.JoinGrupoRequest;
import com.pagape.api.dto.response.GrupoResponse;
import com.pagape.api.dto.response.MiembroResponse;
import com.pagape.api.model.Grupo;
import com.pagape.api.model.PerfilUsuarioGrupo;
import com.pagape.api.model.Usuario;
import com.pagape.api.repository.GrupoRepository;
import com.pagape.api.repository.PerfilUsuarioGrupoRepository;
import com.pagape.api.service.GrupoService;
import com.pagape.api.service.PerfilUsuarioGrupoService;
import com.pagape.api.service.UserService;

@RestController
@RequestMapping("/groups")
public class GrupoController {

    @Value("${storage.location}")
    private String storageLocation;

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private PerfilUsuarioGrupoService perfilService;

    @Autowired
    private UserService userService;

    @Autowired
    private PerfilUsuarioGrupoRepository perfilRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @PostMapping("/create")
    public ResponseEntity<?> crearGrupo(@RequestBody GrupoRequest request, Authentication authentication) {
        try {
            // 1. Extraemos el email del usuario desde el Token JWT
            // authentication.getName() devuelve el "subject" del token (tu email)
            String emailUsuario = authentication.getName();

            // 2. Buscamos el objeto Usuario completo para obtener su ID real
            Usuario creador = userService.obtenerPorEmail(emailUsuario);

            if (creador == null) {
                return ResponseEntity.status(404).body("Error: Usuario autenticado no encontrado en DB.");
            }

            // 3. Creamos el grupo (nombre y clave vienen del JSON)
            Grupo nuevoGrupo = grupoService.crearGrupo(request.getNombre(), request.getClave());

            // 4. Unimos al creador usando el ID que recuperamos de forma segura
            perfilService.unirseAGrupo(
                    creador.getId(),
                    nuevoGrupo.getCodigoUnico(),
                    request.getClave()
            );

            // 5. Devolvemos el grupo creado
            GrupoResponse respuesta = new GrupoResponse(
                    nuevoGrupo.getId(),
                    nuevoGrupo.getNombre(),
                    nuevoGrupo.getCodigoUnico(),
                    nuevoGrupo.getClaveAcceso(),
                    nuevoGrupo.isEsPremium(),
                    true,
                    BigDecimal.ZERO,
                    0,
                    0,
                    null,
                    nuevoGrupo.getUrlFotoGrupo()
            );

            return ResponseEntity.ok(respuesta); // Devolvemos el DTO limpio

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear grupo: " + e.getMessage());
        }
    }

    @GetMapping("/my-groups")
    public ResponseEntity<?> obtenerMisGrupos(Authentication authentication) {
        try {
            // 1. Extraemos el email del Token JWT
            String email = authentication.getName();

            // 2. Buscamos el usuario por email para tener su ID
            Usuario usuario = userService.obtenerPorEmail(email);

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("mensaje", "Usuario no encontrado"));
            }

            // 3. Llamamos al servicio que ahora devuelve List<GrupoResponse>
            List<GrupoResponse> misGrupos = perfilService.listarMisGrupos(usuario.getId());

            // 4. Si la lista está vacía, puedes devolver un 200 con lista vacía o un mensaje
            if (misGrupos.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "mensaje", "Aún no te has unido a ningún grupo",
                        "grupos", misGrupos
                ));
            }

            // 5. Devolvemos la lista de DTOs directamente
            return ResponseEntity.ok(misGrupos);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Error al obtener tus grupos",
                            "detalle", e.getMessage()));
        }
    }

    @PostMapping("/join")
    public ResponseEntity<?> unirseAGrupo(@RequestBody JoinGrupoRequest request, Authentication authentication) {
        try {
            String email = authentication.getName();
            Usuario usuario = userService.obtenerPorEmail(email);

            Grupo grupo = perfilService.unirseAGrupoConRespuesta(
                    usuario.getId(),
                    request.getCodigoUnico(),
                    request.getClave()
            );

            GrupoResponse respuesta = new GrupoResponse(
                    grupo.getId(),
                    grupo.getNombre(),
                    grupo.getCodigoUnico(),
                    grupo.getClaveAcceso(),
                    grupo.isEsPremium(),
                    false,
                    BigDecimal.ZERO,
                    0,
                    0,
                    null,
                    grupo.getUrlFotoGrupo()
            );

            return ResponseEntity.ok(respuesta);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "No se pudo unir al grupo"));
        }
    }

    @PostMapping("/{grupoId}/leave")
    public ResponseEntity<?> salirDelGrupo(@PathVariable Integer grupoId, Authentication authentication) {
        try {
            String email = authentication.getName();
            Usuario usuario = userService.obtenerPorEmail(email);

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("mensaje", "Usuario no encontrado"));
            }

            String resultado = perfilService.salirDelGrupo(usuario.getId(), grupoId);
            if (resultado.startsWith("Error")) {
                return ResponseEntity.badRequest().body(Map.of("mensaje", resultado));
            }

            return ResponseEntity.ok(Map.of("mensaje", resultado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "No se pudo procesar la salida del grupo"));
        }
    }

    @PostMapping("/{grupoId}/image")
    public ResponseEntity<?> subirImagenGrupo(
            @PathVariable Integer grupoId,
            @RequestParam("image") MultipartFile file,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Usuario usuario = userService.obtenerPorEmail(email);

            boolean esMiembro = perfilRepository.existsByIdIdUsuarioAndIdIdGrupo(usuario.getId(), grupoId);
            if (!esMiembro) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("mensaje", "No tienes permiso para modificar este grupo"));
            }

            Grupo grupo = grupoRepository.findById(grupoId).orElse(null);
            if (grupo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Grupo no encontrado"));
            }

            Path uploadsBase = Paths.get(storageLocation).getParent();
            if (uploadsBase == null) uploadsBase = Paths.get(storageLocation);
            Path dir = uploadsBase.resolve("grupos");
            Files.createDirectories(dir);
            String originalName = file.getOriginalFilename();
            int dotIdx = originalName != null ? originalName.lastIndexOf('.') : -1;
            String ext = dotIdx >= 0 ? originalName.substring(dotIdx) : ".jpg";
            String filename = "group_" + grupoId + "_" + UUID.randomUUID().toString().substring(0, 8) + ext;
            Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

            String url = "https://pagape-api.duckdns.org/uploads/grupos/" + filename;
            grupo.setUrlFotoGrupo(url);
            grupoRepository.save(grupo);

            return ResponseEntity.ok(Map.of("imageUrl", url));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error al subir imagen: " + e.getMessage()));
        }
    }

    @GetMapping("/{grupoId}/members")
    public ResponseEntity<?> obtenerMiembros(@PathVariable Integer grupoId, Authentication authentication) {
        try {
            // 1. Obtener quién está preguntando (desde el Token)
            String email = authentication.getName();
            Usuario usuario = userService.obtenerPorEmail(email);

            // 2. SEGURIDAD: Verificar si el usuario pertenece al grupo
            // Usamos el repository para ver si existe la fila en la tabla intermedia
            boolean esMiembro = perfilRepository.existsByIdIdUsuarioAndIdIdGrupo(usuario.getId(), grupoId);

            if (!esMiembro) {
                // Si no pertenece, le damos un 403 Forbidden (Prohibido)
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("mensaje", "No tienes permiso para ver los miembros de este grupo"));
            }

            // 3. Si pasó la validación, devolvemos la lista
            List<MiembroResponse> miembros = perfilService.listarMiembrosGrupo(grupoId);
            return ResponseEntity.ok(miembros);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error al validar acceso"));
        }
    }

}
