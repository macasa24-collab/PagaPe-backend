package com.pagape.api.service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pagape.api.model.Grupo;
import com.pagape.api.model.Usuario;
import com.pagape.api.repository.GrupoRepository;
import com.pagape.api.repository.UserRepository;

@Service
public class AvatarService {

    private static final long MAX_SIZE_BYTES = 5L * 1024 * 1024;
    private static final List<String> TIPOS_PERMITIDOS = List.of("image/jpeg", "image/png", "image/gif");

    @Value("${storage.avatars.location}")
    private String avatarsLocation;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    public String subirAvatarUsuario(Integer userId, MultipartFile archivo) throws IOException {
        validarArchivo(archivo);
        String ext = obtenerExtension(archivo);
        String nombreArchivo = "user_" + userId + "." + ext;
        String url = guardarArchivo(archivo, nombreArchivo);

        Usuario usuario = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setUrlFotoPerfil(url);
        userRepository.save(usuario);
        return url;
    }

    public String subirAvatarGrupo(Integer groupId, MultipartFile archivo) throws IOException {
        validarArchivo(archivo);
        String ext = obtenerExtension(archivo);
        String nombreArchivo = "group_" + groupId + "." + ext;
        String url = guardarArchivo(archivo, nombreArchivo);

        Grupo grupo = grupoRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        grupo.setUrlFotoGrupo(url);
        grupoRepository.save(grupo);
        return url;
    }

    private void validarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }
        if (archivo.getSize() > MAX_SIZE_BYTES) {
            throw new IllegalArgumentException("El archivo supera el tamaño máximo de 5MB");
        }
        String contentType = archivo.getContentType();
        if (contentType == null || !TIPOS_PERMITIDOS.contains(contentType)) {
            throw new IllegalArgumentException("Formato no permitido. Solo se aceptan JPG, PNG y GIF");
        }
    }

    private String obtenerExtension(MultipartFile archivo) {
        String contentType = archivo.getContentType();
        if ("image/gif".equals(contentType)) return "gif";
        if ("image/png".equals(contentType)) return "png";
        return "jpg";
    }

    private String guardarArchivo(MultipartFile archivo, String nombreArchivo) throws IOException {
        Path rutaCarpeta = Paths.get(avatarsLocation).toAbsolutePath().normalize();
        Files.createDirectories(rutaCarpeta);
        Path rutaCompleta = rutaCarpeta.resolve(nombreArchivo);

        String contentType = archivo.getContentType();
        if ("image/gif".equals(contentType)) {
            Files.copy(archivo.getInputStream(), rutaCompleta, StandardCopyOption.REPLACE_EXISTING);
        } else if ("image/png".equals(contentType)) {
            BufferedImage imagen = ImageIO.read(archivo.getInputStream());
            if (imagen == null) throw new IllegalArgumentException("No se pudo leer la imagen");
            ImageIO.write(imagen, "png", rutaCompleta.toFile());
        } else {
            BufferedImage imagenOriginal = ImageIO.read(archivo.getInputStream());
            if (imagenOriginal == null) throw new IllegalArgumentException("No se pudo leer la imagen");
            BufferedImage jpgImagen = new BufferedImage(
                    imagenOriginal.getWidth(),
                    imagenOriginal.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            jpgImagen.createGraphics().drawImage(imagenOriginal, 0, 0, Color.WHITE, null);
            ImageIO.write(jpgImagen, "jpg", rutaCompleta.toFile());
        }

        return "/uploads/avatars/" + nombreArchivo;
    }
}
