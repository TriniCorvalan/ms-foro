package com.example.foro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.foro.model.Tema;

public interface TemaRepository extends JpaRepository<Tema, Long> {

	List<Tema> findByCategoriaId(Long categoriaId);

	@Query("""
			SELECT t FROM Tema t
			WHERE (:categoriaId IS NULL OR t.categoria.id = :categoriaId)
			AND (:q IS NULL OR LOWER(t.titulo) LIKE LOWER(CONCAT('%', :q, '%')))
			""")
	List<Tema> buscar(@Param("categoriaId") Long categoriaId, @Param("q") String q);
}
