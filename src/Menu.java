import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.Scanner;

public class Menu {
        private Scanner sc = new Scanner(System.in, "Cp850");
        private LibroDAO libroDAO = new LibroDAO();
        private Connection conexion;
        
        private Entrada entrada = new Entrada(sc);
        
        String menu = """
            BIBLIOTECA
            1. Conectar
            2. Imprimir biblioteca
            3. Ver libro
            4. Añadir libro
            5. Modificar libro
            6. Eliminar libro
            0. Salir
        """;

        /** 
        * Valida que la opción elegida esté dentro de las opciones del menú y, según la opción elegida
        * realiza unas acciones u otras
        * 
        */
        public void validarOpcion() {
            int opcion;
            int id;

            do {
                System.out.println("\n" + menu);
                opcion = entrada.validarEnteros("Opción");

                if (opcion >= 2 && opcion <= 6 && !libroDAO.conexionActiva(conexion)) {
                    System.out.println("Primero conecta la base de datos (opción 1).");
                    continue;
                }

                switch(opcion) {
                    case 1: 
                    if (libroDAO.conexionActiva(conexion)) {
                        System.out.println("Ya hay una conexión activa.");
                    } else {
                        conexion = libroDAO.conexion(sc);
                    }
                    break;
                    case 2: 
                        System.out.println("\nIMPRIMIR BIBLIOTECA");
                        libroDAO.imprimirBiblioteca(conexion);
                        break;
                    case 3: 
                        System.out.println("\nIMPRIMIR LIBRO");
                        id = entrada.validarEnteros("Introduce el ID");
                        libroDAO.imprimirLibro(conexion, id);
                        break;
                    case 4:
                        System.out.println("\nAÑADIR LIBRO");
                        String titulo = entrada.validarTexto("Título");
                        String autor = entrada.validarTexto("Autor");
                        String isbn = entrada.validarIsbn();
                        String editorial = entrada.validarTexto("Editorial");
                        int paginas = entrada.validarEnteros("Páginas");
                        System.out.println("Sinopsis");
                        String sinopsis = sc.nextLine();
                        EstadoLectura estado = entrada.validarEstado();

                        Libro libro = new Libro (titulo, autor, isbn, editorial, paginas, sinopsis, estado);
                        libroDAO.añadirLibro(conexion, libro);
                        break;
                    case 5: 
                        System.out.println("\nMODIFICAR LIBRO");
                        id = entrada.validarEnteros("Introduce el ID");
                        libroDAO.modificarLibro(conexion,sc, entrada, id);
                        break;
                    case 6: 
                        System.out.println("\nELIMINAR LIBRO");
                        id = entrada.validarEnteros("Introduce el ID");
                        if (entrada.confirmar("Estás seguro de eliminar? Está acción no se puede deshacer")) {
                            libroDAO.eliminarLibro(conexion, id);
                        } else {
                            System.out.println("Eliminación cancelada.");
                        }
                        break;
                    case 0:
                        System.out.println("\nSALIR");
                        System.out.println("Saliendo...");
                        libroDAO.desconexion(conexion);
                        break;
                    default: 
                        System.out.println("Opción no válida.");

                }
            } while (opcion != 0);
        }

    /** 
    * Ejecuta el código
    */
    public static void main(String[] args) {
        Menu app = new Menu();
        app.validarOpcion();
    }
}