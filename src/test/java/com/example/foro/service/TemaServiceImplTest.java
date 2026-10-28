package com.example.foro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.foro.model.Categoria;
import com.example.foro.model.Tema;
import com.example.foro.repository.CategoriaRepository;
import com.example.foro.repository.TemaRepository;

@ExtendWith(MockitoExtension.class)
class TemaServiceImplTest {

	@Mock
	private TemaRepository temaRepository;

	@Mock
	private CategoriaRepository categoriaRepository;

	@InjectMocks
	private TemaServiceImpl service;

	private Categoria categoria;
	private Tema tema;

	@BeforeEach
	void setUp() {
		categoria = new Categoria();
		categoria.setId(1L);
		categoria.setNombre("Tecnología");

		tema = new Tema();
		tema.setId(1L);
		tema.setTitulo("Mejores IDEs");
		tema.setContenido("Contenido");
		tema.setUsuarioId(2L);
		tema.setCategoria(categoria);
		tema.setEstado("ACTIVO");
	}

	@Test
	void buscarDelegatesToRepository() {
		when(temaRepository.buscar(1L, "IDE")).thenReturn(List.of(tema));
		assertEquals(1, service.buscar(1L, "IDE").size());
	}

	@Test
	void getByIdWhenFound() {
		when(temaRepository.findById(1L)).thenReturn(Optional.of(tema));
		assertEquals(tema, service.getById(1L));
	}

	@Test
	void createPersistsWithUsuarioId() {
		Tema request = new Tema();
		request.setTitulo("Nuevo");
		request.setContenido("Body");
		request.setCategoria(categoria);

		when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
		when(temaRepository.save(any(Tema.class))).thenAnswer(inv -> {
			Tema t = inv.getArgument(0);
			t.setId(10L);
			return t;
		});

		Tema created = service.create(request, 2L);
		assertEquals(2L, created.getUsuarioId());
		assertEquals("Nuevo", created.getTitulo());
		verify(temaRepository).save(any(Tema.class));
	}

	@Test
	void updateByOwnerSucceeds() {
		when(temaRepository.findById(1L)).thenReturn(Optional.of(tema));
		when(temaRepository.save(any(Tema.class))).thenReturn(tema);

		Tema cambios = new Tema();
		cambios.setTitulo("Editado");
		Tema updated = service.update(1L, cambios, 2L, false);
		assertEquals(tema, updated);
	}

	@Test
	void updateByOtherUserThrowsSecurityException() {
		when(temaRepository.findById(1L)).thenReturn(Optional.of(tema));
		Tema cambios = new Tema();
		cambios.setTitulo("Hack");
		assertThrows(SecurityException.class, () -> service.update(1L, cambios, 99L, false));
		verify(temaRepository, never()).save(any());
	}

	@Test
	void deleteByAdminSucceeds() {
		when(temaRepository.findById(1L)).thenReturn(Optional.of(tema));
		service.delete(1L, 99L, true);
		verify(temaRepository).deleteById(1L);
	}
}
