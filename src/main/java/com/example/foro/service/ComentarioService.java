package com.example.foro.service;

import java.util.List;

import com.example.foro.model.Comentario;

public interface ComentarioService {
	List<Comentario> getByTemaId(Long temaId);
	Comentario getById(Long id);
	Comentario create(Comentario comentario, Long usuarioId);
	Comentario update(Long id, Comentario cambios, Long usuarioId, boolean esAdmin);
	void delete(Long id, Long usuarioId, boolean esAdmin);
}
