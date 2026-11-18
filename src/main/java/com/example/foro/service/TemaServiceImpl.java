package com.example.foro.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.foro.model.Categoria;
import com.example.foro.model.EstadoContenido;
import com.example.foro.model.Tema;
import com.example.foro.repository.CategoriaRepository;
import com.example.foro.repository.TemaRepository;

@Service
public class TemaServiceImpl implements TemaService {

	@Autowired
	private TemaRepository temaRepository;

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Override
	public List<Tema> buscar(Long categoriaId, String q) {
		String query = (q == null || q.isBlank()) ? null : q.trim();
		return temaRepository.buscar(categoriaId, query);
	}

	@Override
	public Tema getById(Long id) {
		Tema tema = temaRepository.findById(id).orElse(null);
		if (tema == null || EstadoContenido.BANEADO.equals(tema.getEstado())) {
			return null;
		}
		return tema;
	}

	@Override
	public Tema create(Tema tema, Long usuarioId) {
		if (tema.getCategoria() == null || tema.getCategoria().getId() == null) {
			throw new RuntimeException("La categoría es obligatoria");
		}
		Categoria categoria = categoriaRepository.findById(tema.getCategoria().getId())
				.orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
		Tema nuevo = new Tema();
		nuevo.setTitulo(tema.getTitulo());
		nuevo.setContenido(tema.getContenido());
		nuevo.setCategoria(categoria);
		nuevo.setUsuarioId(usuarioId);
		nuevo.setEstado(EstadoContenido.ACTIVO);
		nuevo.setFechaCreacion(LocalDateTime.now());
		return temaRepository.save(nuevo);
	}

	@Override
	public Tema update(Long id, Tema cambios, Long usuarioId, boolean esAdmin) {
		Tema existing = temaRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Tema no encontrado"));
		if (EstadoContenido.BANEADO.equals(existing.getEstado())) {
			throw new RuntimeException("Tema no encontrado");
		}
		if (!esAdmin && !existing.getUsuarioId().equals(usuarioId)) {
			throw new SecurityException("No autorizado para modificar este tema");
		}
		if (cambios.getTitulo() != null) {
			existing.setTitulo(cambios.getTitulo());
		}
		if (cambios.getContenido() != null) {
			existing.setContenido(cambios.getContenido());
		}
		// El ban/desban se gestiona en ms-moderacion; aquí no se cambia ESTADO.
		if (cambios.getCategoria() != null && cambios.getCategoria().getId() != null) {
			Categoria categoria = categoriaRepository.findById(cambios.getCategoria().getId())
					.orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
			existing.setCategoria(categoria);
		}
		return temaRepository.save(existing);
	}

	@Override
	public void delete(Long id, Long usuarioId, boolean esAdmin) {
		Tema existing = temaRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Tema no encontrado"));
		if (EstadoContenido.BANEADO.equals(existing.getEstado()) && !esAdmin) {
			throw new RuntimeException("Tema no encontrado");
		}
		if (!esAdmin && !existing.getUsuarioId().equals(usuarioId)) {
			throw new SecurityException("No autorizado para eliminar este tema");
		}
		temaRepository.deleteById(id);
	}
}
