package com.example.foro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.foro.model.Comentario;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
	List<Comentario> findByTemaId(Long temaId);
}
