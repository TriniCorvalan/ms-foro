package com.example.foro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.foro.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
