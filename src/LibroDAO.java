import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * Acceso a la tabla Libro de la base de datos: conexión, consultas,
 * altas, modificaciones y bajas.
 */
public class LibroDAO {

    /**
     * Conecta con la DB pidiendo datos por consola.
     * Si no se introduce ningún valor, se usa el valor por defecto, excepto la
     * contraseña.
     * 
     * @param sc scanner para leer los datos por consola.
     * @return conexión abierta con la DB, null si hay algún error.
     */
    public Connection conexion(Scanner sc) {

        String servidor = pedir(sc, "Servidor", "localhost");
        String puerto = pedir(sc, "Puerto", "3306");
        String db = pedir(sc, "Base de datos", "biblioteca");
        String usuario = pedir(sc, "Usuario", "root");
        System.out.println("Contraseña []: ");
        String contrasenya = sc.nextLine();

        String url = "jdbc:mysql://" + servidor + ":" + puerto + "/" + db + "?serverTimezone=UTC";

        try {
            Connection conexDB = DriverManager.getConnection(url, usuario, contrasenya);
            System.out.println("Conexión exitosa!\n");
            return conexDB;
        } catch (SQLException e) {
            System.out.println("Error en conexión :" + e.getMessage());
            return null;
        }
    }

    /**
     * Comprueba si la conexión está abierta.
     * 
     * @param conexion conexión activa con la DB
     * @return true si hay una conexión activa
     */
    public boolean conexionActiva(Connection conexion) {
        try {
            return conexion != null && !conexion.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Solicita los datos.
     * Si no se introduce ningún valor, se usa el valor por defecto.
     * 
     * @param sc scanner para leer los datos por consola.
     * @param dato nombre del dato que se pide
     * @param defecto valor usado si no se introduce nada
     * @return el texto introducido, o el valor por defecto
     */
    public static String pedir(Scanner sc, String dato, String defecto) {

        System.out.println(dato + " [" + defecto + "]: ");
        String valor = sc.nextLine().trim();
        return valor.isEmpty() ? defecto : valor;
    }

    /**
     * Imprime la biblioteca de libros almacenados en la DB
     * 
     * @param conexion conexión activa con la DB
     */
    public void imprimirBiblioteca(Connection conexion) {

        try (Statement stmt = conexion.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id, titulo, autor, ISBN, editorial, estado FROM Libro")) {

            System.out.printf("%-5s%-40s%-25s%-20s%-20s%-15s%n", "id", "titulo", "autor", "ISBN", "editorial",
                    "estado");
            System.out.println("-".repeat(125));

            while (rs.next()) {
                System.out.printf("%-5s%-40s%-25s%-20s%-20s%-15s%n",
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("ISBN"),
                        rs.getString("editorial"),
                        rs.getString("estado"));
            }
        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    /**
     * Imprime un libro concreto indicando el id.
     * 
     * @param conexion conexión activa con la DB
     * @param id identificador
     */
    public void imprimirLibro(Connection conexion, int id) {
        Libro libro = buscarLibro(conexion, id);
        if (libro == null) {
            System.out.println("No existe ningún libro con id: " + id);
            return;
        }

        System.out.println("ID: " + libro.getId());
        System.out.println("Título: " + libro.getTitulo());
        System.out.println("Autor: " + libro.getAutor());
        System.out.println("ISBN: " + libro.getIsbn());
        System.out.println("Editorial: " + libro.getEditorial());
        System.out.println("Páginas: " + libro.getPaginas());
        System.out.println("Sinopsis: " + libro.getSinopsis());
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String inicio = (libro.getInicioLectura() != null) ? libro.getInicioLectura().format(formato) : "-";
        String fin = (libro.getFinLectura() != null) ? libro.getFinLectura().format(formato) : "-";
        System.out.println("Inicio de lectura: " + inicio);
        System.out.println("Fin de lectura: " + fin);
        System.out.println("Estado: " + libro.getEstado().getValorDB());
    }

    /**
     * Busca un libro por el id
     * 
     * @param conexion conexión activa con la DB
     * @param id identificador
     * @return el libro con el id indicado si existe, null si no existe.
     */
    public Libro buscarLibro(Connection conexion, int id) {

        try (PreparedStatement ps = conexion.prepareStatement("SELECT id, titulo, autor, ISBN, editorial, paginas, "
                + "sinopsis, inicio_lectura, fin_lectura, estado FROM Libro WHERE id = ?")) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return construirLibro(rs);
                }
            }
        } catch (SQLException e) {
            mostrarError(e);
        }
        return null;
    }

    /**
     * Añade un libro nuevo
     * 
     * @param conexion conexión activa con la DB
     * @param libro libro que se añade
     */
    public void añadirLibro(Connection conexion, Libro libro) {

        try (PreparedStatement ps = conexion
                .prepareStatement("INSERT INTO Libro (titulo, autor, ISBN, editorial, paginas, sinopsis, estado) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)")) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getPaginas());
            ps.setString(6, libro.getSinopsis());
            ps.setString(7, libro.getEstado().getValorDB());
            ps.executeUpdate();
            System.out.println("\nLibro añadido correctamente.");

        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    /**
     * Comprueba si ya exista un libro con ese autor y título
     * 
     * @param conexion conexión activa con la DB
     * @param autor autor del libro
     * @param titulo título del libro
     * @return true si existe, false si no existe
     */
    public boolean comprobarLibro(Connection conexion, String autor, String titulo) {

        try (PreparedStatement ps = conexion.prepareStatement("SELECT id FROM Libro WHERE autor = ? AND titulo = ?")) {

            ps.setString(1, autor);
            ps.setString(2, titulo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            mostrarError(e);
        }
        return false;
    }

    /**
     * Modifica el libro asociado al id indicado
     * 
     * @param conexion conexión activa con la DB
     * @param sc scanner para leer los datos por consola.
     * @param id identificador del libro
     */
    public void modificarLibro(Connection conexion, Scanner sc, Entrada entrada, int id) {
        Libro libro = buscarLibro(conexion, id);
        if (libro == null) {
            System.out.println("No existe ningún libro con id: " + id);
            return;
        }

        libro.setTitulo(entrada.validarTexto("Título", libro.getTitulo()));
        libro.setAutor(entrada.validarTexto("Autor", libro.getAutor()));
        
        String isbnOriginal = libro.getIsbn();
        while(true) {
            libro.setIsbn(entrada.validarIsbn(isbnOriginal));
            if (libro.getIsbn().equals(isbnOriginal) || !existeIsbn(conexion, libro.getIsbn())) {
                break;
            }
            System.out.println("Ya existe otro libro con ese ISBN.");
        }

        libro.setEditorial(entrada.validarTexto("Editorial", libro.getEditorial()));
        libro.setPaginas(entrada.validarEnteros("Páginas", libro.getPaginas()));
        libro.setSinopsis(pedir(sc, "Sinopsis", libro.getSinopsis()));

        while (true) {
            libro.setInicioLectura(entrada.validarFecha("Fecha de inicio", libro.getInicioLectura()));
            libro.setFinLectura(entrada.validarFecha("Fecha fin", libro.getFinLectura()));
            if (libro.getInicioLectura() != null && libro.getFinLectura() != null && libro.getFinLectura().isBefore(libro.getInicioLectura())) {
                System.out.println("La fecha de fin de lectura no puede ser anterior a la de inicio.");
            } else {
                break;
            }
        }
        if (libro.getInicioLectura() == null && libro.getFinLectura() == null) {
            libro.setEstado(entrada.validarEstado(libro.getEstado()));
        }

        EstadoLectura anterior = libro.getEstado();
        libro.actualizarEstado();
        if (libro.getEstado() != anterior) {
            System.out.println("Estado actualizado a " + libro.getEstado().getValorDB());
        }

        Date inicio = null;
        if (libro.getInicioLectura() != null) {
            inicio = Date.valueOf(libro.getInicioLectura());
        }

        Date fin = null;
        if (libro.getFinLectura() != null) {
            fin = Date.valueOf(libro.getFinLectura());
        }

        try (PreparedStatement ps = conexion.prepareStatement(
                "UPDATE Libro SET titulo = ?, autor = ?, ISBN = ?, editorial = ?, paginas = ?, sinopsis = ?, inicio_lectura = ?, fin_lectura = ?, estado = ? WHERE id = ?")) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getPaginas());
            ps.setString(6, libro.getSinopsis());
            ps.setDate(7, inicio);
            ps.setDate(8, fin);
            ps.setString(9, libro.getEstado().getValorDB());
            ps.setInt(10, libro.getId());

            ps.executeUpdate();
            System.out.println("Libro modificado correctamente.");

        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    /**
     * Elimina el libro asociado al id indicado
     * 
     * @param conexion conexión activa con la DB
     * @param id identificador del libro
     */
    public void eliminarLibro(Connection conexion, int id) {
        Libro libro = buscarLibro(conexion, id);
        if (libro == null) {
            System.out.println("No existe ningún libro con id: " + id);
            return;
        }

        try (PreparedStatement ps = conexion.prepareStatement("DELETE FROM Libro WHERE id = ?")) {
            ps.setInt(1, id);

            ps.executeUpdate();
            System.out.println("Libro eliminado correctamente.");

        } catch (SQLException e) {
            mostrarError(e);
        }
    }

    /**
     * Buscar por título
     * 
     * @param conexion conexión activa con la DB
     * @param titulo título del libro
     * @return listado de libros coincidentes
     */
    public List<Libro> buscarPorTitulo(Connection conexion, String titulo) {
        List<Libro> libros = new ArrayList<>();

        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT id, titulo, autor, ISBN, editorial, paginas, sinopsis, inicio_lectura, fin_lectura, estado FROM Libro WHERE titulo LIKE ?")) {

            ps.setString(1, "%" + titulo + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    libros.add(construirLibro(rs));
                }
            }

        } catch (SQLException e) {
            mostrarError(e);
        }
        return libros;
    }

    /**
     * Buscar por autor
     * 
     * @param conexion conexión activa con la DB
     * @param autor autor del libro
     * @return lista de libros coincidentes
     */
    public List<Libro> buscarPorAutor(Connection conexion, String autor) {
        List<Libro> libros = new ArrayList<>();

        try (PreparedStatement ps = conexion.prepareStatement(
                "SELECT id, titulo, autor, ISBN, editorial, paginas, sinopsis, inicio_lectura, fin_lectura, estado FROM Libro WHERE autor LIKE ?")) {

            ps.setString(1, "%" + autor + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    libros.add(construirLibro(rs));
                }
            }

        } catch (SQLException e) {
            mostrarError(e);
        }
        return libros;
    }

    /**
     * Filtra los libros por estado de lectura
     * 
     * @param conexion conexión activa con la DB
     * @param estado estado de lectura que se filtra
     * @return lista de libros coincidentes, vacía si no hay coincidencias
     */
    public List<Libro> filtrarPorEstado(Connection conexion, EstadoLectura estado) {
        List<Libro> libros = new ArrayList<>();

        try (PreparedStatement ps = conexion.prepareStatement(
            "SELECT id, titulo, autor, ISBN, editorial, paginas, sinopsis, inicio_lectura, fin_lectura, estado FROM Libro WHERE estado = ?")) {
            
            ps.setString(1, estado.getValorDB());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    libros.add(construirLibro(rs));
                }
            }

        } catch (SQLException e) {
            mostrarError(e);
        }
        return libros;
    }

    /**
     * Crea un libro a partir del actual ResultSet.
     * @param rs resultado de la consulta
     * @return libro creado
     * @throws SQLException si falla la lectura de alguna columna
     */
    private Libro construirLibro(ResultSet rs) throws SQLException {
        Libro libro = new Libro(
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("ISBN"),
                rs.getString("editorial"),
                rs.getInt("paginas"),
                rs.getString("sinopsis"),
                EstadoLectura.desdeDB(rs.getString("estado")));
        libro.setId(rs.getInt("id"));

        Date inicioLectura = rs.getDate("inicio_lectura");
        Date finLectura = rs.getDate("fin_lectura");
        if (inicioLectura != null)
            libro.setInicioLectura(inicioLectura.toLocalDate());
        if (finLectura != null)
            libro.setFinLectura(finLectura.toLocalDate());

        return libro;
    }

    /**
     * Imprime en forma de tabla la lista de libros.
     * Si la lista está vacía, muestra un mensaje indicando que no se ha encontrado nada.
     * @param libros lista de libros resultado de los filtros aplicados
     */
    public void imprimirLista(List<Libro> libros) {
        if(libros.isEmpty()) {
            System.out.println("No se ha encontrado.");
            return;
        }

        System.out.printf("%-5s%-40s%-25s%-20s%-20s%-20s%n", "id", "titulo", "autor", "ISBN", "editorial", "estado");
        System.out.println("-".repeat(125));

        for (Libro libro : libros) {
            System.out.printf("%-5s%-40s%-25s%-20s%-20s%-15s%n",
                        libro.getId(),
                        libro.getTitulo(),
                        libro.getAutor(),
                        libro.getIsbn(),
                        libro.getEditorial(),
                        libro.getEstado().getValorDB());
        }
    }

    /**
     * Comprueba si ya existe un libro con ese ISBN.
     * @param conexion conexión activa con la DB
     * @param isbn ISBN a comprobar
     * @return true si existe, false si no existe
     */
    public boolean existeIsbn(Connection conexion, String isbn) {
        try (PreparedStatement ps = conexion.prepareStatement("SELECT id FROM Libro WHERE ISBN = ?")) {
            ps.setString(1, isbn);

            try(ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            mostrarError(e);
        }
        return false;
    }

    /**
     * Muestra por consola los datos de error de la DB.
     * @param e excepción capturada
     */
    private void mostrarError(SQLException e) {
        System.out.println("SQLException: " + e.getMessage());
        System.out.println("SQLState: " + e.getSQLState());
        System.out.println("VendorError: " + e.getErrorCode());
    }
    

    /**
     * Desconexión de la DB
     * @param conexDB conexión que se cierra (puede ser null).
     */
    public void desconexion(Connection conexDB) {
        if (conexDB != null) {
            try {
                conexDB.close();
                System.out.println("\nDesconectado correctamente.");
            } catch (SQLException e) {
                System.out.println("Error al desconectar: " + e.getMessage());
            }
        } else {
            System.out.println("No hay ninguna conexión activa.");
        }
    }
}
