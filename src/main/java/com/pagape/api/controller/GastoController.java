package com.pagape.api.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping; // Asegúrate que el nombre coincida (UserService o UsuarioService)
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pagape.api.dto.request.RepartoDeudaRequest;
import com.pagape.api.dto.response.GastoResponse;
import com.pagape.api.dto.response.UsuarioResponse;
import com.pagape.api.model.Gasto;
import com.pagape.api.model.PerfilUsuarioGrupo;
import com.pagape.api.model.RepartoDeuda;
import com.pagape.api.model.Usuario;
import com.pagape.api.repository.GastoRepository;
import com.pagape.api.repository.PerfilUsuarioGrupoRepository;
import com.pagape.api.repository.PlanRepository;
import com.pagape.api.repository.RepartoDeudaRepository;
import com.pagape.api.service.GastoService;
import com.pagape.api.service.RepartoDeudaService;
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

    @Autowired
    private RepartoDeudaService repartoDeudaService;

    @Autowired
    private RepartoDeudaRepository repartoDeudaRepository;

    @Autowired
    private PlanRepository planRepository;

    @PostMapping("/{idPlan}/new")
    public ResponseEntity<?> registrarGasto(
            @PathVariable Integer idPlan,
            @RequestPart("importe") BigDecimal importe,
            @RequestPart("concepto") String concepto,
            @RequestPart(value = "ticket", required = false) MultipartFile archivo,
            Authentication authentication) {
        try {
            String emailUsuario = authentication.getName();
            Usuario pagador = usuarioService.obtenerPorEmail(emailUsuario);

            if (pagador == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no válido");
            }

            // Llamamos al servicio pasando los datos sueltos
            Gasto nuevoGasto = gastoService.crearGasto(idPlan, pagador, importe, concepto, archivo);

            // Mapeamos a DTO para evitar bucles JSON
            UsuarioResponse pagadorResponse = new UsuarioResponse(
                    pagador.getId(),
                    pagador.getNombre(),
                    pagador.getEmail()
            );
            GastoResponse gastoResponse = new GastoResponse(
                    nuevoGasto.getId(),
                    idPlan,
                    pagadorResponse,
                    nuevoGasto.getImporte(),
                    nuevoGasto.getConcepto(),
                    nuevoGasto.getUrlFotoTicket()
            );

            // Lógica de división de deuda
            Integer idGrupo = nuevoGasto.getPlanOrigen().getGrupo().getId();
            List<PerfilUsuarioGrupo> miembros = perfilRepository.findByGrupoId(idGrupo);
            List<Usuario> deudores = miembros.stream()
                    .map(PerfilUsuarioGrupo::getUsuario)
                    .filter(u -> !u.getId().equals(pagador.getId()))
                    .collect(Collectors.toList());

            int totalMiembros = miembros.size();
            if (totalMiembros > 0) {
                BigDecimal cuota = importe.divide(BigDecimal.valueOf(totalMiembros), 2, RoundingMode.HALF_UP);
                for (Usuario deudor : deudores) {
                    RepartoDeuda reparto = new RepartoDeuda(nuevoGasto.getId(), deudor.getId(), cuota);
                    repartoDeudaRepository.save(reparto);
                }

                // Actualizar balances
                for (PerfilUsuarioGrupo perfil : miembros) {
                    BigDecimal totalOwedToThem = repartoDeudaRepository.sumCuotaDebeWherePagadorIs(perfil.getUsuario().getId(), idGrupo);
                    BigDecimal totalTheyOwe = repartoDeudaRepository.sumCuotaDebeWhereDeudorIs(perfil.getUsuario().getId(), idGrupo);
                    BigDecimal balance = totalOwedToThem.subtract(totalTheyOwe);
                    perfil.setBalanceActual(balance);
                    perfilRepository.save(perfil);
                }
            }

            // Devolvemos el DTO
            return ResponseEntity.status(HttpStatus.CREATED).body(gastoResponse);

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
            boolean esMiembro = perfilRepository.existeUsuarioEnGrupoPorEmail(email, idGrupo);

            if (!esMiembro) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No eres miembro de este grupo.");
            }

            List<Gasto> gastos = gastoService.listarGastosConfirmadosPorGrupo(idGrupo);

            // Mapeamos a DTOs
            List<GastoResponse> gastosResponse = gastos.stream().map(gasto -> {
                UsuarioResponse pagadorResponse = new UsuarioResponse(
                        gasto.getPagador().getId(),
                        gasto.getPagador().getNombre(),
                        gasto.getPagador().getEmail()
                );
                return new GastoResponse(
                        gasto.getId(),
                        gasto.getPlanOrigen().getId(),
                        pagadorResponse,
                        gasto.getImporte(),
                        gasto.getConcepto(),
                        gasto.getUrlFotoTicket()
                );
            }).collect(Collectors.toList());

            return ResponseEntity.ok(gastosResponse);

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

            if (!perfilRepository.existeUsuarioEnGrupoPorEmail(email, idGrupo)) {
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

    /**
     * Endpoint para crear gasto con deudas personalizadas (NO usa división
     * automática)
     */
    @PostMapping("/{idPlan}/new-custom-debts")
    public ResponseEntity<?> crearGastoConDeudasPersonalizadas(
            @PathVariable Integer idPlan,
            @RequestPart("importe") BigDecimal importe,
            @RequestPart("concepto") String concepto,
            @RequestPart(value = "ticket", required = false) MultipartFile archivo,
            @RequestPart("deudas") List<RepartoDeudaRequest> deudasRequest,
            Authentication authentication) {
        try {
            String emailUsuario = authentication.getName();
            Usuario pagador = usuarioService.obtenerPorEmail(emailUsuario);

            if (pagador == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no válido");
            }

            // 1. Llamamos al servicio para crear el gasto (MISMA LÓGICA que registrarGasto)
            Gasto nuevoGasto = gastoService.crearGasto(idPlan, pagador, importe, concepto, archivo);

            // 2. Mapeamos a DTO para evitar bucles JSON
            UsuarioResponse pagadorResponse = new UsuarioResponse(
                    pagador.getId(),
                    pagador.getNombre(),
                    pagador.getEmail()
            );
            GastoResponse gastoResponse = new GastoResponse(
                    nuevoGasto.getId(),
                    idPlan,
                    pagadorResponse,
                    nuevoGasto.getImporte(),
                    nuevoGasto.getConcepto(),
                    nuevoGasto.getUrlFotoTicket()
            );

            // 3. VALIDACIÓN: La suma de deudas NO debe exceder el importe del gasto
            BigDecimal totalDeudas = deudasRequest.stream()
                    .map(RepartoDeudaRequest::getCuotaDebe)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (totalDeudas.compareTo(importe) > 0) {
                return ResponseEntity.badRequest()
                        .body("Error: La suma de deudas (" + totalDeudas + ") excede el importe del gasto (" + importe + ")");
            }

            // 4. Lógica de deudas personalizadas (DIFERENCIA con registrarGasto)
            Integer idGrupo = nuevoGasto.getPlanOrigen().getGrupo().getId();
            List<PerfilUsuarioGrupo> miembros = perfilRepository.findByGrupoId(idGrupo);

            // Crear deudas personalizadas SOLO para los miembros que NO son el pagador
            List<RepartoDeuda> deudas = deudasRequest.stream()
                    .filter(deuda -> !deuda.getIdUsuarioDeudor().equals(pagador.getId())) // Excluir al pagador
                    .map(req -> new RepartoDeuda(nuevoGasto.getId(), req.getIdUsuarioDeudor(), req.getCuotaDebe()))
                    .collect(Collectors.toList());

            // Guardar las deudas
            repartoDeudaRepository.saveAll(deudas);

            // 5. Actualizar balances (IGUAL que registrarGasto)
            for (PerfilUsuarioGrupo perfil : miembros) {
                BigDecimal totalOwedToThem = repartoDeudaRepository.sumCuotaDebeWherePagadorIs(perfil.getUsuario().getId(), idGrupo);
                BigDecimal totalTheyOwe = repartoDeudaRepository.sumCuotaDebeWhereDeudorIs(perfil.getUsuario().getId(), idGrupo);
                BigDecimal balance = totalOwedToThem.subtract(totalTheyOwe);
                perfil.setBalanceActual(balance);
                perfilRepository.save(perfil);
            }

            // Devolvemos el DTO
            return ResponseEntity.status(HttpStatus.CREATED).body(gastoResponse);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

}
