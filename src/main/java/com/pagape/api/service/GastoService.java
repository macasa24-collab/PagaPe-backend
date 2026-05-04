package com.pagape.api.service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.pagape.api.model.Gasto;
import com.pagape.api.model.Plan;
import com.pagape.api.model.Usuario;
import com.pagape.api.repository.GastoRepository;
import com.pagape.api.repository.PerfilUsuarioGrupoRepository;
import com.pagape.api.repository.PlanRepository;

@Service
public class GastoService {

    @Autowired
    private GastoRepository gastoRepository;
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private PerfilUsuarioGrupoRepository perfilRepository; // Necesario para verificar pertenencia

    // 1. DEBES AÑADIR ESTA VARIABLE AQUÍ ARRIBA 👈
    @Value("${storage.location}")
    private String storageLocation;

    @Transactional
    public Gasto crearGasto(Integer idPlan, Usuario pagador, BigDecimal importe, String concepto, MultipartFile archivo) throws IOException {

        // 1. Validaciones de Plan y Membresía (igual que antes)
        Plan plan = planRepository.findById(idPlan)
                .orElseThrow(() -> new RuntimeException("El plan no existe"));

        boolean esMiembro = perfilRepository.existsByIdIdUsuarioAndIdIdGrupo(pagador.getId(), plan.getGrupo().getId());
        if (!esMiembro) {
            throw new RuntimeException("No eres miembro del grupo");
        }

        // 2. Creamos y guardamos el gasto primero (para tener el ID)
        Gasto gasto = new Gasto();
        gasto.setPlanOrigen(plan);
        gasto.setPagador(pagador);
        gasto.setImporte(importe);
        gasto.setConcepto(concepto);
        gasto = gastoRepository.save(gasto); // Aquí se genera el ID

        // 3. Si hay imagen, la procesamos
        if (archivo != null && !archivo.isEmpty()) {
            String nombreArchivo = gasto.getId() + ".jpg";
            // Define tu ruta física absoluta o relativa
            Path rutaCarpeta = Paths.get(storageLocation).toAbsolutePath().normalize();

            // En Linux esto se traducirá a /var/pagape/uploads/gastos/
            Files.createDirectories(rutaCarpeta);

            Path rutaCompleta = rutaCarpeta.resolve(nombreArchivo);

            // Convertir a JPG
            BufferedImage imagenOriginal = ImageIO.read(archivo.getInputStream());
            if (imagenOriginal != null) {
                BufferedImage jpgImagen = new BufferedImage(
                        imagenOriginal.getWidth(),
                        imagenOriginal.getHeight(),
                        BufferedImage.TYPE_INT_RGB);

                jpgImagen.createGraphics().drawImage(imagenOriginal, 0, 0, Color.WHITE, null);

                ImageIO.write(jpgImagen, "jpg", rutaCompleta.toFile());

                // Guardamos la URL relativa en la base de datos
                gasto.setUrlFotoTicket("/uploads/gastos/" + nombreArchivo);
                gasto = gastoRepository.save(gasto); // Actualizamos con la URL
            }
        }

        return gasto;
    }

    public List<Gasto> listarGastosConfirmadosPorGrupo(Integer idGrupo) {
        // Buscamos gastos donde:
        // 1. El ID del grupo coincida.
        // 2. La votación del plan esté cerrada (voto finalizado).
        // 3. El plan NO haya sido denegado.
        return gastoRepository.findByPlanOrigen_Grupo_IdAndPlanOrigen_VotacionCerradaTrueAndPlanOrigen_DenegadoFalse(idGrupo);
    }
}
