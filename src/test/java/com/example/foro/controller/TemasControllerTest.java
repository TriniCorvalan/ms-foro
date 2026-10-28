package com.example.foro.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.foro.model.Categoria;
import com.example.foro.model.Tema;
import com.example.foro.security.JwtUserPrincipal;
import com.example.foro.service.ComentarioService;
import com.example.foro.service.TemaService;

@ExtendWith(MockitoExtension.class)
class TemasControllerTest {

	@Mock
	private TemaService temaService;

	@Mock
	private ComentarioService comentarioService;

	@InjectMocks
	private TemasController controller;

	private MockMvc mockMvc;
	private Tema tema;
	private UsernamePasswordAuthenticationToken userAuth;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

		Categoria categoria = new Categoria();
		categoria.setId(1L);
		categoria.setNombre("Tecnología");

		tema = new Tema();
		tema.setId(1L);
		tema.setTitulo("Mejores IDEs");
		tema.setContenido("Contenido");
		tema.setUsuarioId(2L);
		tema.setCategoria(categoria);
		tema.setEstado("ACTIVO");

		JwtUserPrincipal principal = new JwtUserPrincipal(2L, "juan@foro.cl", "USUARIO");
		userAuth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
	}

	@Test
	void buscarReturnsList() throws Exception {
		when(temaService.buscar(1L, "IDE")).thenReturn(List.of(tema));

		mockMvc.perform(get("/temas").param("categoriaId", "1").param("q", "IDE"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].titulo").value("Mejores IDEs"));
	}

	@Test
	void getByIdWhenFoundReturnsOk() throws Exception {
		when(temaService.getById(1L)).thenReturn(tema);

		mockMvc.perform(get("/temas/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	void createTemaReturnsCreated() throws Exception {
		when(temaService.create(any(Tema.class), eq(2L))).thenReturn(tema);

		mockMvc.perform(post("/temas")
						.principal(userAuth)
						.contentType(APPLICATION_JSON)
						.content("{\"titulo\":\"Mejores IDEs\",\"contenido\":\"Contenido\",\"categoria\":{\"id\":1}}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.titulo").value("Mejores IDEs"));

		verify(temaService).create(any(Tema.class), eq(2L));
	}
}
