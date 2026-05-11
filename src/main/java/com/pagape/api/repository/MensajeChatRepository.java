package com.pagape.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pagape.api.model.MensajeChat;

@Repository
public interface MensajeChatRepository extends JpaRepository<MensajeChat, Integer> {

    // Devuelve los últimos 50 mensajes de un grupo ordenados por fecha
    List<MensajeChat> findTop50ByGrupoIdOrderByTimestampAsc(Integer grupoId);
}