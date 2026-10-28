package com.example.foro.service;

import java.util.List;

import com.example.foro.model.Tema;

public interface TemaService {
	List<Tema> buscar(Long categoriaId, String q);
	Tema getById(Long id);
	Tema create(Tema tema, Long usuarioId);
	Tema update(Long id, Tema cambios, Long usuarioId, boolean esAdmin);
	void delete(Long id, Long usuarioId, boolean esAdmin);
}
