package com.example.foro.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.foro.model.Comentario;
import com.example.foro.security.JwtUserPrincipal;
import com.example.foro.security.SecurityUtils;
import com.example.foro.service.ComentarioService;

@RestController
@RequestMapping("/comentarios")
@CrossOrigin(origins = "*")
public class ComentariosController {

	@Autowired
	private ComentarioService comentarioService;

	@GetMapping("/{id}")
	public ResponseEntity<Comentario> getById(@PathVariable Long id) {
		Comentario found = comentarioService.getById(id);
		if (found == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(found);
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody Comentario comentario, Authentication authentication) {
		JwtUserPrincipal user = (JwtUserPrincipal) authentication.getPrincipal();
		try {
			Comentario creado = comentarioService.create(comentario, user.getId());
			return ResponseEntity.status(HttpStatus.CREATED).body(creado);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Comentario comentario,
			Authentication authentication) {
		JwtUserPrincipal user = (JwtUserPrincipal) authentication.getPrincipal();
		try {
			Comentario updated = comentarioService.update(id, comentario, user.getId(),
					SecurityUtils.isAdmin(authentication));
			return ResponseEntity.ok(updated);
		} catch (SecurityException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id, Authentication authentication) {
		JwtUserPrincipal user = (JwtUserPrincipal) authentication.getPrincipal();
		try {
			comentarioService.delete(id, user.getId(), SecurityUtils.isAdmin(authentication));
			return ResponseEntity.noContent().build();
		} catch (SecurityException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
	}
}
