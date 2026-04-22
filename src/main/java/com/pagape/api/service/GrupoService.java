package com.pagape.api.service;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pagape.api.model.Grupo;
import com.pagape.api.repository.GrupoRepository;

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    // Caracteres que usaremos para el código (quitamos letras confusas como O y 0)
    private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int LONGITUD_CODIGO = 8;
    private final SecureRandom random = new SecureRandom();

    /**
     * Genera un código alfanumérico aleatorio.
     */
    private String generarCadenaAleatoria() {
        StringBuilder sb = new StringBuilder(LONGITUD_CODIGO);
        for (int i = 0; i < LONGITUD_CODIGO; i++) {
            int index = random.nextInt(CARACTERES.length());
            sb.append(CARACTERES.charAt(index));
        }
        return sb.toString();
    }

    /**
     * Crea un grupo con un código único garantizado.
     */
    public Grupo crearGrupo(String nombre, String clave) {
        String codigoPropuesto;
        boolean existe;

        // Bucle de verificación: Genera y comprueba en DB
        do {
            codigoPropuesto = generarCadenaAleatoria();
            existe = grupoRepository.findByCodigoUnico(codigoPropuesto).isPresent();
        } while (existe);

        // Una vez que tenemos un código único, creamos el objeto
        Grupo nuevoGrupo = new Grupo(nombre, codigoPropuesto, clave);
        return grupoRepository.save(nuevoGrupo);
    }

    /**
     * Busca un grupo por su código de invitación (el de 12 caracteres).
     */
    public Grupo obtenerPorCodigo(String codigo) {
        return grupoRepository.findByCodigoUnico(codigo).orElse(null);
    }

    /**
     * Obtiene todos los grupos (útil para administración o depuración).
     */
    public List<Grupo> obtenerTodos() {
        return grupoRepository.findAll();
    }
}
