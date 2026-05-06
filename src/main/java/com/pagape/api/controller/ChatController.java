package com.pagape.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pagape.api.dto.MensajeChatDTO;
import com.pagape.api.model.Grupo;
import com.pagape.api.model.MensajeChat;
import com.pagape.api.model.Usuario;
import com.pagape.api.repository.GrupoRepository;
import com.pagape.api.repository.MensajeChatRepository;
import com.pagape.api.repository.PerfilUsuarioGrupoRepository;
import com.pagape.api.service.UserService;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private MensajeChatRepository mensajeRepository;
    @Autowired private GrupoRepository grupoRepository;
    @Autowired private UserService userService;
    @Autowired private PerfilUsuarioGrupoRepository perfilRepository;

    // ── REST: historial de mensajes (últimos 50) ──────────────────────────
    @GetMapping("/history/{grupoId}")
    public ResponseEntity<?> getHistory(@PathVariable Integer grupoId, Authentication auth) {
        Usuario usuario = userService.obtenerPorEmail(auth.getName());
        boolean esMiembro = perfilRepository.existsByIdIdUsuarioAndIdIdGrupo(
                usuario.getId(), grupoId);

        if (!esMiembro) {
            return ResponseEntity.status(403).body("No perteneces a este grupo");
        }

        List<MensajeChat> mensajes = mensajeRepository
                .findTop50ByGrupoIdOrderByTimestampAsc(grupoId);

        List<MensajeChatDTO> dtos = mensajes.stream()
                .map(m -> new MensajeChatDTO(
                        m.getUsuario().getId(),
                        m.getUsuario().getNombre(),
                        m.getGrupo().getId(),
                        m.getTexto(),
                        m.getTimestamp()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // ── WebSocket: recibir y reenviar mensaje ─────────────────────────────
    // Flutter envía a: /app/chat/send/{grupoId}
    // Todos los suscritos a: /topic/chat/{grupoId} lo reciben
    @MessageMapping("/chat/send/{grupoId}")
    public void sendMessage(
            @DestinationVariable Integer grupoId,
            @Payload MensajeChatDTO payload
    ) {
        // 1. Obtener entidades
        Grupo grupo = grupoRepository.findById(grupoId).orElseThrow();
        Usuario usuario = userService.obtenerPorId(payload.getIdUsuario());
        if (usuario == null) return;

        // 2. Verificar que el usuario pertenece al grupo
        boolean esMiembro = perfilRepository.existsByIdIdUsuarioAndIdIdGrupo(
                usuario.getId(), grupoId);
        if (!esMiembro) return;

        // 3. Guardar en BD
        MensajeChat mensaje = new MensajeChat(grupo, usuario, payload.getTexto());
        mensajeRepository.save(mensaje);

        // 4. Construir DTO de respuesta con timestamp real
        MensajeChatDTO respuesta = new MensajeChatDTO(
                usuario.getId(),
                usuario.getNombre(),
                grupoId,
                payload.getTexto(),
                mensaje.getTimestamp()
        );

        // 5. Broadcast a todos los suscritos del grupo
        messagingTemplate.convertAndSend("/topic/chat/" + grupoId, respuesta);
    }
}
