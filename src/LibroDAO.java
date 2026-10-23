import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class LibroDAO {

    /** 
    * Conecta con la DB pidiendo datos por consola.
    * Si no se introduce ningún valor, se usa el valor por defecto, excepto la contraseña.
    * 
    * @param sc (Scanner)
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
     * @param conexion
     * @return true si hay una conexión activa
     */
    public boolean conexionActiva (Connection conexion) {
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
    * @param sc (Scanner)
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
    * @param conexion
    */
    public void imprimirBiblioteca(Connection conexion) {

        try (Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, titulo, autor, ISBN, editorial, estado FROM Libro")) {

            System.out.printf("%-5s%-30s%-30s%-20s%-20s%-20s%n", "id", "titulo", "autor", "ISBN", "editorial", "estado");
            System.out.println("-".repeat(125));

            while (rs.next()) {
                System.out.printf("%-5s%-30s%-30s%-20s%-20s%-20s%n",
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("ISBN"),
                rs.getString("editorial"),
                rs.getString("estado"));
                }
        } catch (SQLException e) {
            System.out.println("SQLException: "+ e.getMessage());
            System.out.println("SQLState: "+ e.getSQLState());
            System.out.println("VendorError: "+ e.getErrorCode());
        }
    }

    /** 
    * Imprime un libro concreto indicando el id.
    * 
    * @param conexion
    * @param id identificador
    */
    public void imprimirLibro(Connection conexion, int id) {
        Libro libro = buscarLibro(conexion, id);
        if (libro == null) {
            System.out.println("No existe ningún libro con id: " + id);
            return;
        }

        System.out.println("ID: "+ libro.getId());
        System.out.println("Título: "+ libro.getTitulo());
        System.out.println("Autor: "+ libro.getAutor());
        System.out.println("ISBN: "+ libro.getIsbn());
        System.out.println("Editorial: "+ libro.getEditorial());
        System.out.println("Páginas: "+ libro.getPaginas());
        System.out.println("Sinopsis: "+ libro.getSinopsis());
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String inicio = (libro.getInicioLectura() != null) ? libro.getInicioLectura().format(formato) : "-";
        String fin = (libro.getFinLectura() != null) ? libro.getFinLectura().format(formato) : "-";
        System.out.println("Inicio de lectura: " + inicio);
        System.out.println("Fin de lectura: " + fin);
        System.out.println("Estado: "+ libro.getEstado().getValorDB());  
    }

    /** 
    * Busca un libro por el id
    * 
    * @param conexion
    * @param id identificador
    * @return el libro con el id indicado si existe, null si no existe.
    */
    public Libro buscarLibro (Connection conexion, int id) {

        try (PreparedStatement ps = conexion.prepareStatement("SELECT titulo, autor, ISBN, editorial, paginas, " 
            + "sinopsis, inicio_lectura, fin_lectura, estado FROM Libro WHERE id = ?")) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Libro libro = new Libro(
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("ISBN"),
                        rs.getString("editorial"),
                        rs.getInt("paginas"),
                        rs.getString("sinopsis"),
                        EstadoLectura.desdeDB(rs.getString("estado")));
                    libro.setId(id);
                    Date inicioLectura = rs.getDate("inicio_lectura");
                    Date finLectura = rs.getDate("fin_lectura");
                    if (inicioLectura != null) libro.setInicioLectura(inicioLectura.toLocalDate());
                    if (finLectura != null) libro.setFinLectura(finLectura.toLocalDate());
                
                return libro;
                }
            }
        } catch (SQLException e) {
            System.out.println("SQLException: "+ e.getMessage());
            System.out.println("SQLState: "+ e.getSQLState());
            System.out.println("VendorError: "+ e.getErrorCode());
        }
        return null;
    }

    
    /** 
    * Añade un libro nuevo
    * 
    * @param conexion
    * @param libro 
    */
    public void añadirLibro(Connection conexion, Libro libro) {

        try (PreparedStatement ps = conexion.prepareStatement("INSERT INTO Libro (titulo, autor, ISBN, editorial, paginas, sinopsis, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getIsbn());
            ps.setString(4, libro.getEditorial());
            ps.setInt(5, libro.getPaginas());
            ps.setString(6, libro.getSinopsis());
            ps.setString(7, libro.getEstado().getValorDB());
            ps.executeUpdate();
            System.out.println("Libro añadido correctamente.");

        } catch (SQLException e) {
            System.out.println("SQLException: "+ e.getMessage());
            System.out.println("SQLState: "+ e.getSQLState());
            System.out.println("VendorError: "+ e.getErrorCode());
        }
    }

    /** 
    * Modifica el libro asociado al id indicado
    * 
    * @param conexion
    * @param sc (Scanner)
    * @param id identificador
    */
    public void modificarLibro(Connection conexion, Scanner sc, Entrada entrada, int id) {
        Libro libro = buscarLibro(conexion, id);
        if (libro == null) {
            System.out.println("No existe ningún libro con id: " + id);
            return;
        }
        
        libro.setTitulo(entrada.validarTexto("Título", libro.getTitulo()));
        libro.setAutor(entrada.validarTexto("Autor", libro.getAutor()));
        libro.setIsbn(entrada.validarIsbn(libro.getIsbn()));
        libro.setEditorial(entrada.validarTexto("Editorial", libro.getEditorial()));
        libro.setPaginas(entrada.validarEnteros("Páginas", libro.getPaginas()));
        libro.setSinopsis(pedir(sc, "Sinopsis", libro.getSinopsis()));
        libro.setEstado(entrada.validarEstado(libro.getEstado()));
        libro.setInicioLectura(entrada.validarFecha("Fecha de inicio", libro.getInicioLectura()));
        libro.setFinLectura(entrada.validarFecha("Fecha fin", libro.getFinLectura()));

        Date inicio = null;
        if (libro.getInicioLectura() != null) {
            inicio = Date.valueOf(libro.getInicioLectura());
        }

        Date fin = null;
        if (libro.getFinLectura() != null) {
            fin = Date.valueOf(libro.getFinLectura());
        }

        try (PreparedStatement ps = conexion.prepareStatement("UPDATE Libro SET titulo = ?, autor = ?, ISBN = ?, editorial = ?, paginas = ?, sinopsis = ?, inicio_lectura = ?, fin_lectura = ?, estado = ? WHERE id = ?")) {
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
            System.out.println("SQLException: "+ e.getMessage());
            System.out.println("SQLState: "+ e.getSQLState());
            System.out.println("VendorError: "+ e.getErrorCode());
        }
    }

    /** 
    * Elimina el libro asociado al id indicado
    * 
    * @param conexion
    * @param id identificador
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
            System.out.println("SQLException: "+ e.getMessage());
            System.out.println("SQLState: "+ e.getSQLState());
            System.out.println("VendorError: "+ e.getErrorCode());
        }
    }

    /** 
    * Desconexión de la DB
    * 
    * @param conexDB
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
