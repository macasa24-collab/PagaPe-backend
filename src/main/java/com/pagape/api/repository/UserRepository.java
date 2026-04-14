package com.pagape.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pagape.api.model.Usuario;

@Repository
public interface UserRepository extends JpaRepository<Usuario, Integer> {

}
