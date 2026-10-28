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
import com.example.foro.repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {

	@Mock
	private CategoriaRepository categoriaRepository;

	@InjectMocks
	private CategoriaServiceImpl service;

	private Categoria categoria;

	@BeforeEach
	void setUp() {
		categoria = new Categoria();
		categoria.setId(1L);
		categoria.setNombre("Gaming");
		categoria.setDescripcion("Videojuegos");
	}

	@Test
	void getAllReturnsList() {
		when(categoriaRepository.findAll()).thenReturn(List.of(categoria));
		assertEquals(1, service.getAll().size());
	}

	@Test
	void createSavesCategoria() {
		when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);
		Categoria request = new Categoria();
		request.setNombre("Gaming");
		assertEquals(categoria, service.create(request));
	}

	@Test
	void updateWhenMissingThrows() {
		when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(RuntimeException.class, () -> service.update(1L, categoria));
		verify(categoriaRepository, never()).save(any());
	}

	@Test
	void deleteWhenExists() {
		when(categoriaRepository.existsById(1L)).thenReturn(true);
		service.delete(1L);
		verify(categoriaRepository).deleteById(1L);
	}

	@Test
	void deleteWhenMissingThrows() {
		when(categoriaRepository.existsById(1L)).thenReturn(false);
		assertThrows(RuntimeException.class, () -> service.delete(1L));
	}
}
