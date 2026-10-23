# 📚 Biblioteca personal
Aplicación para gestionar la biblioteca personal. Permite almacenar libros y hacer un seguimiento de cada uno según su estado: los que quiero (`deseado`), los que tengo (`lo tengo`), los que estoy leyendo (`leyendo`), los que he leído (`leido`).
Cada libro se guarda con: título, autor, ISBN, editorial, número de páginas, sinopsis, fechas de lectura y estado.
 
🚧 **Proyecto en desarrollo.** 
Primer proyecto del ciclo de DAM, trabajando por sprints.


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
- Comprobar si un libro existe antes de añadirlo.
- Confirmación antes de eliminar un libro.
- Modificar la sinopsis y cualquier otro dato de un libro.
- Elegir el estado de cada libro entre cuatro opciones: lo tengo, leyendo, leído y deseado.
- Registrar fecha de inicio y fecha de fin de lectura (desde modificar).
- Validación de los datos introducidos (ISBN, páginas, fechas, estados).
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
1. Conectar
2. Imprimir biblioteca
3. Ver libro
4. Añadir libro
5. Modificar libro
6. Eliminar libro
0. Salir
```


### 🗂️ Estructura del proyecto

| Archivo | Descripción |
| --- | --- |
| `Libro.java` | Modelo: representa un libro con todos sus atributos. |
| `EstadoLectura.java` | `enum` con los estados de lectura. Cada constante guarda el texto que usa en la base de datos (`valorDB`), y el método `desdeDB()` convierte ese texto en la constante correspondiente. |
| `LibroDAO.java` | Acceso a datos: conexión y operaciones sobre la tabla `Libro` con JDBC. |
| `Entrada.java` | Lectura y validación de los datos introducidos por consola. |
| `Menu.java` | Menú de consola y punto de entrada del programa. |
| `biblioteca.sql` | Script para crear la base de datos y la tabla `Libro`. |


### ⚠️ Limitaciones

Puntos a resolver en el siguiente sprint:

- Filtros de búsqueda: por autor, por título, por estado.
- Integrar la lógica de las fechas: si tiene fecha inicio y final el estado cambia automáticamente a `leido`, si tiene fecha inicio pero no final el estado cambia a `leyendo`.


### ➡️ Próximos pasos

- ~~Comprobar si el libro existe antes de pedir todos los datos.~~
- ~~Confirmación antes de eliminar.~~
- ~~Corregir la visualización de caracteres especiales.~~
- ~~Comprobar si existe una conexión activa antes de solicitar todos los datos.~~
- ~~Clase `Entrada` para validar la entrada de datos (texto, enteros, ISBN con expresión regular, fechas y estados).~~
- Filtros de búsqueda por título, autor o estado.
- Cambio automático de estado según las fechas de lectura.
- Pruebas y ajustes.


### 👩‍💻 Autora
Sara Alós Plaza [GitHub](https://github.com/saraalosp-cmd)