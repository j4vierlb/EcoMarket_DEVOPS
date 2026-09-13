# 🌿 EcoMarket — API REST de Mercado Ecológico

Backend de un mercado ecológico desarrollado con **Spring Boot**, que expone una API REST para la gestión de productos, usuarios y pedidos. Arquitectura en capas siguiendo buenas prácticas de desarrollo Java.

## 🧭 Modelo de ramificación: GitFlow

Este proyecto implementa **GitFlow** como estrategia de trabajo colaborativo para mantener trazabilidad, estabilidad y orden en el desarrollo.

### Ramas principales

- `main`: versión estable y lista para producción.
- `develop`: rama de integración para nuevas funcionalidades.
- `feature/<nombre>`: desarrollo de nuevas funcionalidades.
- `hotfix/<nombre>`: correcciones urgentes sobre producción.

### ¿Por qué GitFlow?

GitFlow es adecuado para este proyecto porque:

- separa claramente el código de producción del desarrollo activo;
- facilita la colaboración entre varios integrantes;
- permite trabajar en features en paralelo sin afectar la rama principal;
- ofrece un flujo definido para correcciones urgentes con `hotfix`.

Este enfoque cumple con la evaluación de DevOps, ya que demuestra control de versiones, trazabilidad y un proceso ordenado de integración.

---

## 🔀 Flujo de trabajo del equipo

1. Crear una rama `feature/<nombre>` desde `develop`.
2. Implementar la funcionalidad con commits pequeños y descriptivos.
3. Abrir un Pull Request hacia `develop`.
4. Revisar el código y dejar evidencia de aprobación.
5. Integrar a `develop` usando merge con trazabilidad.
6. Cuando haya un problema crítico en producción, crear `hotfix/<nombre>` desde `main`.
7. Corregir y fusionar a `main`, luego sincronizar a `develop`.

---

## 📝 Convenciones de commits

Se recomienda usar mensajes en estilo convencional:

- `feat:` nuevas funcionalidades
- `fix:` correcciones de errores
- `docs:` cambios en documentación
- `chore:` tareas de mantenimiento
- `refactor:` mejoras internas sin cambiar comportamiento
- `test:` cambios relacionados con pruebas

Ejemplos:

- `feat(productos): agregar endpoints CRUD`
- `fix(usuario): corregir validación de correo`
- `docs(readme): documentar flujo GitFlow`
- `chore(ci): configurar GitHub Actions`

---

## ✅ Reglas de revisión y merge

- Todo cambio debe pasar la validación automática.
- Todo Pull Request debe revisarse antes del merge.
- No se fusiona directamente a `main` sin revisión.
- Los conflictos deben resolverse antes de integrar.
- Los mensajes de commit deben ser claros y específicos.

---

## 🚀 GitHub Actions

El repositorio incluye una acción automática configurada en `.github/workflows/ci.yml`.

Se ejecuta en:

- cada push a `develop`
- cada pull request hacia `main`

La pipeline valida el proyecto ejecutando Maven con:

```bash
mvn test
```

Esto permite automatizar la integración de cambios y comprobar que el microservicio sigue compilando y funcionando correctamente.

---

## 🚀 Tecnologías utilizadas

| Capa | Tecnología |
|------|-----------|
| Backend | Java + Spring Boot |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | MySQL |
| API | REST con JSON |
| Build | Maven |

---

## ✨ Endpoints disponibles

### 📦 Productos — `/api/productos`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/productos` | Listar todos los productos |
| GET | `/api/productos/{id}` | Obtener producto por ID |
| POST | `/api/productos` | Crear nuevo producto |
| PUT | `/api/productos/{id}` | Actualizar producto |
| DELETE | `/api/productos/{id}` | Eliminar producto |

### 👤 Usuarios — `/api/usuarios`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/usuarios` | Listar todos los usuarios |
| GET | `/api/usuarios/{id}` | Obtener usuario por ID |
| POST | `/api/usuarios` | Crear nuevo usuario |
| PUT | `/api/usuarios/{id}` | Actualizar usuario |
| DELETE | `/api/usuarios/{id}` | Eliminar usuario |

### 🛒 Pedidos — `/api/pedidos`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/pedidos` | Listar todos los pedidos |
| GET | `/api/pedidos/{id}` | Obtener pedido por ID |
| POST | `/api/pedidos` | Crear nuevo pedido |
| DELETE | `/api/pedidos/{id}` | Eliminar pedido |

---

## 🏗️ Arquitectura

El proyecto sigue una arquitectura en 3 capas:

```
Controller (REST)  →  Service (lógica de negocio)  →  Repository (acceso a datos)
```

```
src/
└── main/java/com/grupo3/EcoMarket/
    ├── controller/       # Controladores REST
    │   ├── ProductoController.java
    │   ├── UsuarioController.java
    │   └── PedidoController.java
    ├── service/          # Lógica de negocio
    │   ├── ProductoService.java
    │   ├── UsuarioService.java
    │   └── Pedidoservice.java
    ├── repository/       # Acceso a datos (Spring Data JPA)
    │   ├── ProductoRepository.java
    │   ├── UsuarioRepository.java
    │   └── PedidoRepository.java
    └── model/            # Entidades JPA
        ├── Producto.java
        ├── Usuario.java
        └── pedido.java
```

---

## ⚙️ Instalación y ejecución

### Requisitos previos

- Java 17+
- Maven
- MySQL

### Configuración de base de datos

Crea una base de datos en MySQL y configura el archivo `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecomarket
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
```

### Pasos para ejecutar

```bash
# 1. Clonar el repositorio
git clone https://github.com/j4vierlb/EcoMarket.git
cd EcoMarket

# 2. Compilar y ejecutar
mvn spring-boot:run
```

La API estará disponible en `http://localhost:8080`

---

## 📝 Ejemplo de uso

### Crear un producto

```bash
POST /api/productos
Content-Type: application/json

{
  "nombre": "Manzanas orgánicas",
  "descripcion": "Manzanas cultivadas sin pesticidas",
  "stock": 100,
  "precio": 1500
}
```

### Crear un usuario

```bash
POST /api/usuarios
Content-Type: application/json

{
  "nombre": "Juan Pérez",
  "correo": "juan@email.com",
  "contrasena": "segura123"
}
```

---

## 👨‍💻 Autor

**Javier Lara Bustos**  
[GitHub](https://github.com/j4vierlb)

## 🔧 Hotfix: ajuste de pipeline

Se corrige la rama de hotfix para mantener la automatización y la sincronización entre main y develop.
