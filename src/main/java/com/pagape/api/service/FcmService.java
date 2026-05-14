package com.pagape.api.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;

@Service
public class FcmService {

    private static final Logger log = LoggerFactory.getLogger(FcmService.class);

    public void enviarNotificacionChat(List<String> tokens, String nombreEmisor,
                                       String texto, Integer grupoId) {
        if (tokens.isEmpty()) return;

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(nombreEmisor)
                        .setBody(texto.length() > 100 ? texto.substring(0, 97) + "..." : texto)
                        .build())
                .putData("tipo",    "CHAT")
                .putData("grupoId", grupoId.toString())
                .addAllTokens(tokens)
                .build();

        try {
            FirebaseMessaging.getInstance().sendEachForMulticast(message);
        } catch (Exception e) {
            log.error("Error enviando notificación FCM: {}", e.getMessage());
        }
    }

    public void enviarNotificacionPlanAceptado(List<String> tokens, String tituloPlan,
                                               String fechaPlan, String nombreGrupo,
                                               Integer grupoId) {
        if (tokens.isEmpty()) return;

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle("✅ Plan aceptado en " + nombreGrupo)
                        .setBody("\"" + tituloPlan + "\" — " + fechaPlan)
                        .build())
                .putData("tipo",        "PLAN_ACEPTADO")
                .putData("grupoId",     grupoId.toString())
                .putData("tituloPlan",  tituloPlan)
                .putData("fechaPlan",   fechaPlan)
                .putData("nombreGrupo", nombreGrupo)
                .addAllTokens(tokens)
                .build();

        try {
            FirebaseMessaging.getInstance().sendEachForMulticast(message);
        } catch (Exception e) {
            log.error("Error enviando notificación plan aceptado: {}", e.getMessage());
        }
    }
}
