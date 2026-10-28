# ms-foro

Microservicio de **foro** (categorías, temas y comentarios) del Sistema de Foros en Línea (DUOC Full Stack III — Exp 1 Semana 2).

Depende de **[ms-usuarios](https://github.com/TriniCorvalan/ms-usuarios)** para el login y la emisión del JWT (mismo `jwt.secret`).

- Puerto: **8082**
- Stack: Spring Boot 4, Spring Data JPA, Spring Security (valida JWT emitido por ms-usuarios), Oracle Cloud
- Referencia auth: [ms-usuarios](https://github.com/TriniCorvalan/ms-usuarios) (`POST /auth/login`)

## Endpoints

| Método | Ruta | Acceso | Descripción |
|--------|------|--------|-------------|
| GET | `/categorias` | Público | Listar |
| GET | `/categorias/{id}` | Público | Detalle |
| POST/PUT/DELETE | `/categorias` | ADMIN | Gestión |
| GET | `/temas?categoriaId=&q=` | Público | Listar / buscar |
| GET | `/temas/{id}` | Público | Ver tema |
| GET | `/temas/{id}/comentarios` | Público | Comentarios del tema |
| POST | `/temas` | Autenticado | Publicar |
| PUT/DELETE | `/temas/{id}` | Dueño o ADMIN | Editar / eliminar |
| POST | `/comentarios` | Autenticado | Comentar |
| PUT/DELETE | `/comentarios/{id}` | Dueño o ADMIN | Editar / eliminar |

## Configuración

Usa el mismo schema Oracle `FORO_APP` y el mismo wallet que [ms-usuarios](https://github.com/TriniCorvalan/ms-usuarios).

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

## Ejecutar

Primero obtén un token en [ms-usuarios](https://github.com/TriniCorvalan/ms-usuarios) (`POST http://localhost:8081/auth/login`), luego:

```bash
./mvnw spring-boot:run
```

Header en Postman: `Authorization: Bearer <token>`

## Tests

```bash
./mvnw test
```
