import java.sql.Connection;
import java.util.Scanner;

/**
 * 
 * Menú de consola de la biblioteca. Pide la opción al usuario y llama
 * a las operaciones de LibroDAO.
 */
public class Menu {
        private Scanner sc = new Scanner(System.in);
        private LibroDAO libroDAO = new LibroDAO();
        private Connection conexion;
        
        private Entrada entrada = new Entrada(sc);
        
        /** Texto de las opciones del menú. */
        String menu = """
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

                if (opcion >= 2 && opcion <= 9 && !libroDAO.conexionActiva(conexion)) {
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
                        System.out.println();
                        libroDAO.imprimirBiblioteca(conexion);
                        break;
                    case 3: 
                        System.out.println("\nIMPRIMIR LIBRO");
                        System.out.println();
                        id = entrada.validarEnteros("Introduce el ID");
                        System.out.println();
                        libroDAO.imprimirLibro(conexion, id);
                        break;
                    case 4:
                        System.out.println("\nAÑADIR LIBRO");
                        System.out.println();
                        String titulo = entrada.validarTexto("Título");
                        String autor = entrada.validarTexto("Autor");

                        if (libroDAO.comprobarLibro(conexion, autor, titulo)) {
                            if (!entrada.confirmar("Ya existe un libro con ese título y autor. ¿Añadir igualmente?")) {
                                System.out.println("Operación cancelada.");
                                break;
                            }
                        }

                        String isbn;
                        while (true) {
                            isbn = entrada.validarIsbn();
                            if (!libroDAO.existeIsbn(conexion, isbn)) {
                                break;
                            }
                            System.out.println("Ya existe un libro con ese ISBN.");
                        }

                        String editorial = entrada.validarTexto("Editorial");
                        int paginas = entrada.validarEnteros("Páginas");
                        System.out.println("Sinopsis: ");
                        String sinopsis = sc.nextLine();
                        EstadoLectura estado = entrada.validarEstado();

                        Libro libro = new Libro (titulo, autor, isbn, editorial, paginas, sinopsis, estado);
                        libroDAO.añadirLibro(conexion, libro);
                        break;
                    case 5: 
                        System.out.println("\nMODIFICAR LIBRO");
                        System.out.println();
                        id = entrada.validarEnteros("Introduce el ID");
                        libroDAO.modificarLibro(conexion,sc, entrada, id);
                        break;
                    case 6: 
                        System.out.println("\nELIMINAR LIBRO");
                        System.out.println();
                        id = entrada.validarEnteros("Introduce el ID");
                        if (entrada.confirmar("¿Estás seguro de eliminar? Está acción no se puede deshacer")) {
                            libroDAO.eliminarLibro(conexion, id);
                        } else {
                            System.out.println("Eliminación cancelada.");
                        }
                        break;
                    case 7:
                        System.out.println("\nBUSCAR POR TÍTULO");
                        System.out.println();
                        titulo = entrada.validarTexto("Introduce el título");
                        System.out.println();
                        libroDAO.imprimirLista(libroDAO.buscarPorTitulo(conexion, titulo));
                        break;
                    case 8:
                        System.out.println("\nBUSCAR POR AUTOR");
                        System.out.println();
                        autor = entrada.validarTexto("Introduce el autor");
                        System.out.println();
                        libroDAO.imprimirLista(libroDAO.buscarPorAutor(conexion, autor));
                        break;
                    case 9:
                        System.out.println("\nFILTRAR POR ESTADO");
                        System.out.println();
                        estado = entrada.validarEstado();
                        System.out.println();
                        libroDAO.imprimirLista(libroDAO.filtrarPorEstado(conexion, estado));
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

    /** Punto de entrada de la aplicación. */
    public static void main(String[] args) {
        Menu app = new Menu();
        app.validarOpcion();
    }
}