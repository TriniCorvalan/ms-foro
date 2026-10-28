package com.example.foro.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.foro.model.Categoria;
import com.example.foro.security.JwtAuthenticationFilter;
import com.example.foro.security.SecurityConfig;
import com.example.foro.service.CategoriaService;

@WebMvcTest(controllers = CategoriasController.class,
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
				SecurityConfig.class,
				JwtAuthenticationFilter.class
		}))
@AutoConfigureMockMvc(addFilters = false)
class CategoriasControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CategoriaService categoriaService;

	private Categoria categoria;

	@BeforeEach
	void setUp() {
		categoria = new Categoria();
		categoria.setId(1L);
		categoria.setNombre("Tecnología");
		categoria.setDescripcion("Tech");
	}

	@Test
	void getAllReturnsList() throws Exception {
		when(categoriaService.getAll()).thenReturn(List.of(categoria));

		mockMvc.perform(get("/categorias"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].nombre").value("Tecnología"));
	}

	@Test
	void getByIdWhenMissingReturnsNotFound() throws Exception {
		when(categoriaService.getById(99L)).thenReturn(null);
		mockMvc.perform(get("/categorias/99")).andExpect(status().isNotFound());
	}

	@Test
	void createReturnsCreated() throws Exception {
		when(categoriaService.create(any(Categoria.class))).thenReturn(categoria);

		mockMvc.perform(post("/categorias")
						.contentType(APPLICATION_JSON)
						.content("{\"nombre\":\"Tecnología\",\"descripcion\":\"Tech\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	void updateReturnsOk() throws Exception {
		when(categoriaService.update(eq(1L), any(Categoria.class))).thenReturn(categoria);

		mockMvc.perform(put("/categorias/1")
						.contentType(APPLICATION_JSON)
						.content("{\"descripcion\":\"Nueva\"}"))
				.andExpect(status().isOk());
	}

	@Test
	void deleteReturnsNoContent() throws Exception {
		mockMvc.perform(delete("/categorias/1")).andExpect(status().isNoContent());
	}
}
