package com.example.foro.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.foro.model.Tema;
import com.example.foro.security.JwtUserPrincipal;
import com.example.foro.security.SecurityUtils;
import com.example.foro.service.ComentarioService;
import com.example.foro.service.TemaService;

@RestController
@RequestMapping("/temas")
@CrossOrigin(origins = "*")
public class TemasController {

	@Autowired
	private TemaService temaService;

	@Autowired
	private ComentarioService comentarioService;

	@GetMapping
	public List<Tema> buscar(
			@RequestParam(required = false) Long categoriaId,
			@RequestParam(required = false) String q) {
		return temaService.buscar(categoriaId, q);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Tema> getById(@PathVariable Long id) {
		Tema found = temaService.getById(id);
		if (found == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(found);
	}

	@GetMapping("/{id}/comentarios")
	public ResponseEntity<?> getComentarios(@PathVariable Long id) {
		if (temaService.getById(id) == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(comentarioService.getByTemaId(id));
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody Tema tema, Authentication authentication) {
		JwtUserPrincipal user = (JwtUserPrincipal) authentication.getPrincipal();
		try {
			Tema creado = temaService.create(tema, user.getId());
			return ResponseEntity.status(HttpStatus.CREATED).body(creado);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Tema tema,
			Authentication authentication) {
		JwtUserPrincipal user = (JwtUserPrincipal) authentication.getPrincipal();
		try {
			Tema updated = temaService.update(id, tema, user.getId(), SecurityUtils.isAdmin(authentication));
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
			temaService.delete(id, user.getId(), SecurityUtils.isAdmin(authentication));
			return ResponseEntity.noContent().build();
		} catch (SecurityException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
	}
}
