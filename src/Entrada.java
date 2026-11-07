import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Lee y valida por consola los datos que introduce el usuario.
 * Cada método repite la pregunta hasta recibir un valor válido.
 */
public class Entrada {
    
    private final Scanner sc;

    /**
     * @param sc scanner compartido con el resto de la aplicación
    */
    public Entrada (Scanner sc) {
        this.sc = sc;
    }
    
    /**
     * Pide el ISBN y valida que tenga 13 dígitos.
     * Se eliminan espacios y guiones, o el actual si se deja vacío.
     * 
     * @return el ISBN con 13 dígitos.
    */
    public String validarIsbn () {
        while(true) {
            System.out.println("ISBN (13 dígitos): ");
            String valor = sc.nextLine().replaceAll("[-\\s]", "");
            if (valor.matches("\\d{13}")) {
                return valor;
            }
            System.out.println("ISBN no válido, debe tener 13 dígitos.");
        }
    }

    /**
     * Pide el ISBN mostrando el valor actual.
     * 
     * @param isbn valor actual.
     * @return el ISBN introducido, o el actual si se deja vacío.
    */
    public String validarIsbn (String isbn) {
        while(true) {
            System.out.println("ISBN (13 dígitos) [" + isbn + "]: ");
            String valor = sc.nextLine().replaceAll("[-\\s]", "");
            if (valor.isEmpty()) {
                return isbn;
            }
            if (valor.matches("\\d{13}")) {
                return valor;
            }
            System.out.println("ISBN no válido, debe tener 13 dígitos.");
        }
    }

    /**
     * Pide un entero y repite la pregunta hasta que sea válido.
     * 
     * @param dato nombre del dato solicitado
     * @return el entero introducido cuando sea correcto.
     */
    public int validarEnteros (String dato) {
        while(true) {
            System.out.println(dato + ": ");
            String valor = sc.nextLine().trim();
            if (valor.matches("\\d{1,4}")) {
                return Integer.parseInt(valor);
            }
            System.out.println("Introduce un número entero válido.");
        }
    }

    /**
     * Pide un entero mostrando el valor actual.
     * Si se deja vacío, se mantiene el actual.
     * 
     * @param dato nombre del dato solicitado.
     * @param numero valor actual.
     * @return el entero introducido o el actual si se deja vacío.
     */
    public int validarEnteros (String dato, int numero) {
        while(true) {
            System.out.println(dato + " [" + numero + "]: ");
            String valor = sc.nextLine().trim();
            if (valor.isEmpty()) {
                return numero;
            }
            if (valor.matches("\\d{1,4}")) {
                return Integer.parseInt(valor);
            }
            System.out.println("Introduce un número entero válido.");
        }
    }

    /**
     * Pide el estado de lectura y valida que sea uno de los cuatro permitidos.
     * No distingue mayúsculas de minúsculas y acepta "leído".
     * 
     * @return el estado correspondiente, o el actual si se deja vacío.
     */
    public EstadoLectura validarEstado() {
        while (true) {
            System.out.println("Estado (lo tengo, leyendo, leido, deseado): ");
            String valor = sc.nextLine().trim().toLowerCase().replace("í", "i");
            if (valor.matches("lo tengo|leyendo|leido|deseado")) {
                return EstadoLectura.desdeDB(valor);
            }
            System.out.println("Estado no válido.");
        }
    }

    /**
     * Pide el estado de lectura y valida que sea uno de los cuatro permitidos.
     * No distingue mayúsculas de minúsculas y acepta "leído".
     * 
     * @param estado valor actual
     * @return el estado correspondiente, o el actual si se deja vacío.
     */
    public EstadoLectura validarEstado(EstadoLectura estado) {
        while (true) {
            System.out.println("Estado (lo tengo, leyendo, leido, deseado) [" + estado.getValorDB() + "]: ");
            String valor = sc.nextLine().trim().toLowerCase().replace("í", "i");
            if (valor.isEmpty()) {
                return estado;
            }
            if (valor.matches("lo tengo|leyendo|leido|deseado")) {
                return EstadoLectura.desdeDB(valor);
            }
            System.out.println("Estado no válido.");
        }
    }

    /**
     * Pide una fecha con formato dd-mm-aaaa y comprueba que sea correcto.
     * Si se deja vacío se mantiene la actual.
     * Si se pasa "-", se borra la fecha.
     * 
     * @param dato nombre del dato solicitado
     * @param fecha valor actual (puede ser null)
     * @return fecha introducida si es correcta, o null si se escribe "-".
     */
    public LocalDate validarFecha(String dato, LocalDate fecha) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String actual = (fecha == null) ? "" : fecha.format(formato);
        while(true) {
            System.out.println(dato + " (dd-mm-aaaa) [" + actual + "]: ");
            String valor = sc.nextLine().trim();
            if (valor.isEmpty()) {
                return fecha;
            }
            if (valor.equals("-")) {
                return null;
            }

            try {
                return LocalDate.parse(valor, formato);
            } catch (DateTimeParseException e) {
                System.out.println("Fecha no válida.");
            }
        }
    }

    /**
     * Pide un texto obligatorio, eliminando los espacios al principio y al final y 
     * dejando la primera letra en mayúscula. 
     * 
     * @param dato nombre del dato solicitado.
     * @return texto formateado.
     */
    public String validarTexto(String dato) {
        while(true) {
            System.out.println(dato + ": ");
            String valor = sc.nextLine().trim();
            if (!valor.isEmpty()) {
                return valor.substring(0,1).toUpperCase() + valor.substring(1);
            }
            System.out.println("Debes introducir un valor.");
        }
    }

    /**
     * Pide un texto, mostrando el actual. Si se deja vacío, se mantiene el actual.
     * Si no, se eliminan los espacios al principio y final, dejando la primera en
     * mayúscula.
     * 
     * @param dato nombre del dato solicitado.
     * @param actual valor actual
     * @return texto formateado.
     */
    public String validarTexto(String dato, String actual) {
        System.out.println(dato + " [" + actual + "]: ");
        String valor = sc.nextLine().trim();
        if (valor.isEmpty()){
            return actual;
        }
        return valor.substring(0,1).toUpperCase() + valor.substring(1);
    }

    
    /**
     * Solicita confirmación antes de eliminar un libro.
     * 
     * @param mensaje pregunta que muestra al usuario
     * @return true si responde S, false si responde N
     */
    public boolean confirmar(String mensaje) {
        while (true) {
            System.out.println(mensaje + " (S/N): ");
            String valor = sc.nextLine().trim();
            if (valor.equalsIgnoreCase("S")) return true;
            if (valor.equalsIgnoreCase("N")) return false;
            System.out.println("Responde S o N.");
        }
    }
}
