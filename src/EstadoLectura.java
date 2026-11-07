/**
 * Estados de lectura del libro.
 * Cada constante guarda el texto con el que se almacena en la base de datos.
 */
public enum EstadoLectura {
    // valores
    LO_TENGO("lo tengo"),
    LEYENDO("leyendo"),
    LEIDO("leido"),
    DESEADO("deseado");

    private final String valorDB;

    /**
     * @param valorDB texto del estado en la base de datos
     */
    EstadoLectura (String valorDB) {
        this.valorDB = valorDB;
    }

    public String getValorDB() { return valorDB;}

    /**
    * Pasa el texto de la DB al valor del enum
    *
    @param valor estado en la DB
    @return constante de EstadoLectura asociada al valor
    @throws IlegallArgumentException si no se corresponde a ningún estado
     */
    public static EstadoLectura desdeDB (String valor) {
        for (EstadoLectura estado : values()) {
            if (estado.valorDB.equals(valor)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado desconocido: " + valor);
    }
}
