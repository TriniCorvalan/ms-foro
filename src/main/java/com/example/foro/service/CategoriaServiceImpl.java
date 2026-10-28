package com.example.foro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.foro.model.Categoria;
import com.example.foro.repository.CategoriaRepository;

@Service
public class CategoriaServiceImpl implements CategoriaService {

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Override
	public List<Categoria> getAll() {
		return categoriaRepository.findAll();
	}

	@Override
	public Categoria getById(Long id) {
		return categoriaRepository.findById(id).orElse(null);
	}

	@Override
	public Categoria create(Categoria categoria) {
		categoria.setId(null);
		return categoriaRepository.save(categoria);
	}

	@Override
	public Categoria update(Long id, Categoria cambios) {
		Categoria existing = categoriaRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
		if (cambios.getNombre() != null) {
			existing.setNombre(cambios.getNombre());
		}
		if (cambios.getDescripcion() != null) {
			existing.setDescripcion(cambios.getDescripcion());
		}
		return categoriaRepository.save(existing);
	}

	@Override
	public void delete(Long id) {
		if (!categoriaRepository.existsById(id)) {
			throw new RuntimeException("Categoría no encontrada");
		}
		categoriaRepository.deleteById(id);
	}
}
