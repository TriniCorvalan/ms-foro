package com.example.foro.service;

import java.util.List;

import com.example.foro.model.Categoria;

public interface CategoriaService {
	List<Categoria> getAll();
	Categoria getById(Long id);
	Categoria create(Categoria categoria);
	Categoria update(Long id, Categoria cambios);
	void delete(Long id);
}
