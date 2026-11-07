# 📚 Biblioteca personal
Aplicación para gestionar la biblioteca personal. Permite almacenar libros y hacer un seguimiento de cada uno según su estado: los que quiero (`deseado`), los que tengo (`lo tengo`), los que estoy leyendo (`leyendo`), los que he leído (`leido`).
Cada libro se guarda con: título, autor, ISBN, editorial, número de páginas, sinopsis, fechas de lectura y estado.
 
✅ **Proyecto finalizado.**
Primer proyecto del ciclo de DAM, trabajado por sprints.


### 🛠️ Tecnologías

- Java
- MySQL
- JDBC (driver MySQL Connector/J)
- Visual Studio Code
- Git/GitHub


### ✅ Requisitos funcionales
**Implementadas**

- Conectar la base de datos introduciendo los datos por consola (servidor, puerto, base de datos, usuario y contraseña). Si no se escribe nada, se usan los valores por defecto: localhost, 3306, biblioteca y root. La contraseña no tiene valor por defecto.
- Comprobar si existe una conexión activa antes de solicitar todos los datos.
- Visualizar la biblioteca completa.
- Consultar la información de un libro a partir de su id: ISBN, editorial, número de páginas, sinopsis, fechas de lectura y estado.
- Añadir, modificar y eliminar libros.
- Comprobar si un libro existe (mismo título y autor) antes de añadirlo.
- Comprobar que el ISBN no esté repetido al añadir o modificar un libro.
- Confirmación antes de eliminar un libro.
- Modificar la sinopsis y cualquier otro dato de un libro.
- Elegir el estado de cada libro entre cuatro opciones: lo tengo, leyendo, leído y deseado.
- Registrar fecha de inicio y fecha de fin de lectura (desde modificar).
- Cambio automático del estado según las fechas de lectura: si hay fecha fin pasa a `leido`; si solo hay fecha de inicio a `leyendo`. Si no hay fechas, el estado se elige al modificar.
- Buscar libros por título y por autor aun escribiendo solo una parte del texto (no distingue mayúsculas de minúsculas).
- Filtrar libros por estado de lectura.
- Validación de los datos introducidos (ISBN, páginas, fechas, estados). La fecha fin no puede ser anterior a la de inicio.
- Cancelar la introducción de datos escribiendo `cancelar`, lo que devuelve al menú (no aplica a la conexión ni a la sinopsis al añadir).
- Correcta visualización de los caracteres especiales.

### ▶️ Cómo ejecutarlo
**Requisitos**

- JDK 15 o superior (desarrollado con JDK 24)
- MySQL Server
- Driver MySQL Connector/J añadido al proyecto (archivo .jar)
- **Windows:** para que los caracteres especiales se visualicen correctamente, activar: Configuración → Hora e idioma → Idioma y región → Idioma para mostrar de Windows → `Versión beta: Use UTF-8 Unicode para la compatibilidad de idioma en todo el mundo`

**Pasos**

1. Clona el repositorio:

```bash
   git clone https://github.com/saraalosp-cmd/bibliotecaJava.git
```

2. Crea la base de datos ejecutando el script de SQL del repositorio (biblioteca.sql).

3. Añade el .jar del driver a la carpeta `lib` del proyecto.

4. Compila y ejecuta la clase Menu.

   **Windows:**
   ```bash
   javac -cp "lib/mysql-connector-j-26.7.0.jar" -d bin src/*.java
   java -cp "bin;lib/mysql-connector-j-26.7.0.jar" Menu
   ```

   **Linux/macOS:**
   ```bash
   javac -cp "lib/mysql-connector-j-26.7.0.jar" -d bin src/*.java
   java -cp "bin:lib/mysql-connector-j-26.7.0.jar" Menu
   ```

5. En el menú, elige primero la opción **1. Conectar** e introduce los datos de tu servidor MySQL.


### 📋 Menú

```text
BIBLIOTECA
--------------------------------
1. Conectar
2. Imprimir biblioteca
3. Ver libro
4. Añadir libro
5. Modificar libro
6. Eliminar libro
7. Buscar por título
8. Buscar por autor
9. Filtrar por estado de lectura
0. Salir

(Escribe "cancelar" para volver al menú)
```


### 🗂️ Estructura del proyecto

| Archivo | Descripción |
| --- | --- |
| `Libro.java` | Modelo: representa un libro con todos sus atributos. Incluye `actualizarEstado()`, que ajusta el estado de lectura según las fechas de inicio y fin. |
| `EstadoLectura.java` | `enum` con los estados de lectura. Cada constante guarda el texto que usa en la base de datos (`valorDB`), y el método `desdeDB()` convierte ese texto en la constante correspondiente. |
| `LibroDAO.java` | Acceso a datos: conexión y operaciones sobre la tabla `Libro` con JDBC. |
| `Entrada.java` | Lectura y validación de los datos introducidos por consola. |
| `CancelarException.java` | Excepción que se lanza cuando el usuario cancela la operación en curso. |
| `Menu.java` | Menú de consola y punto de entrada del programa. |
| `biblioteca.sql` | Script para crear la base de datos y la tabla `Libro`. |

### ⚠️ Limitaciones conocidas

- La contraseña de la base de datos se muestra en pantalla al escribirla.
- Escribir **exactamente** `cancelar` cancela la operación, así que no se puede utilizar solo esa palabra como dato. Un título como "Cancelar la deuda" sí se puede guardar.
- No se puede cancelar con `cancelar` en las preguntas de conexión ni en la sinopsis al añadir un libro.
- Los números (ID, páginas, opción del menú) admiten hasta 4 dígitos.
- Las fechas de lectura y el cambio automático de estado solo funcionan al modificar un libro; al añadirlo, se elige a mano.


### 👩‍💻 Autora
Sara Alós Plaza - 2º DAM <2026/2027>
[Repositorios](https://github.com/saraalosp-cmd)