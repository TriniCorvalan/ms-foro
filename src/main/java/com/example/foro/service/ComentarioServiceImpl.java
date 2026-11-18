package com.example.foro.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.foro.model.Comentario;
import com.example.foro.model.EstadoContenido;
import com.example.foro.model.Tema;
import com.example.foro.repository.ComentarioRepository;
import com.example.foro.repository.TemaRepository;

@Service
public class ComentarioServiceImpl implements ComentarioService {

	@Autowired
	private ComentarioRepository comentarioRepository;

	@Autowired
	private TemaRepository temaRepository;

	@Override
	public List<Comentario> getByTemaId(Long temaId) {
		return comentarioRepository.findByTemaIdAndEstadoOrderByFechaCreacionAsc(
				temaId, EstadoContenido.ACTIVO);
	}

	@Override
	public Comentario getById(Long id) {
		Comentario comentario = comentarioRepository.findById(id).orElse(null);
		if (comentario == null || EstadoContenido.BANEADO.equals(comentario.getEstado())) {
			return null;
		}
		return comentario;
	}

	@Override
	public Comentario create(Comentario comentario, Long usuarioId) {
		Long temaId = comentario.getTema() != null ? comentario.getTema().getId() : null;
		if (temaId == null) {
			throw new RuntimeException("El tema es obligatorio");
		}
		Tema tema = temaRepository.findById(temaId)
				.orElseThrow(() -> new RuntimeException("Tema no encontrado"));
		if (EstadoContenido.BANEADO.equals(tema.getEstado())) {
			throw new RuntimeException("No se puede comentar un tema baneado");
		}
		Comentario nuevo = new Comentario();
		nuevo.setContenido(comentario.getContenido());
		nuevo.setTema(tema);
		nuevo.setUsuarioId(usuarioId);
		nuevo.setEstado(EstadoContenido.ACTIVO);
		nuevo.setFechaCreacion(LocalDateTime.now());
		return comentarioRepository.save(nuevo);
	}

	@Override
	public Comentario update(Long id, Comentario cambios, Long usuarioId, boolean esAdmin) {
		Comentario existing = comentarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
		if (EstadoContenido.BANEADO.equals(existing.getEstado())) {
			throw new RuntimeException("Comentario no encontrado");
		}
		if (!esAdmin && !existing.getUsuarioId().equals(usuarioId)) {
			throw new SecurityException("No autorizado para modificar este comentario");
		}
		if (cambios.getContenido() != null) {
			existing.setContenido(cambios.getContenido());
		}
		// El ban/desban se gestiona en ms-moderacion; aquí no se cambia ESTADO.
		return comentarioRepository.save(existing);
	}

	@Override
	public void delete(Long id, Long usuarioId, boolean esAdmin) {
		Comentario existing = comentarioRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
		if (EstadoContenido.BANEADO.equals(existing.getEstado()) && !esAdmin) {
			throw new RuntimeException("Comentario no encontrado");
		}
		if (!esAdmin && !existing.getUsuarioId().equals(usuarioId)) {
			throw new SecurityException("No autorizado para eliminar este comentario");
		}
		comentarioRepository.deleteById(id);
	}
}
