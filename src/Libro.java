import java.time.LocalDate;

public class Libro {
    // atributos
    private int id;
    private String titulo;
    private String autor;
    private String isbn;
    private String editorial;
    private int paginas;
    private String sinopsis;
    private LocalDate inicioLectura;
    private LocalDate finLectura;
    private EstadoLectura estado;

    // constructor
    public Libro (String titulo, String autor, String isbn, String editorial, int paginas, String sinopsis, EstadoLectura estado) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.editorial = editorial;
        this.paginas = paginas;
        this.sinopsis = sinopsis;
        this.estado = estado;
    }

    // getters y setters
    public int getId() { return id;}
    public void setId(int nuevoId) {this.id = nuevoId;}

    public String getTitulo() { return titulo;}
    public void setTitulo(String nuevoTitulo) {this.titulo = nuevoTitulo;}

    public String getAutor() { return autor;}
    public void setAutor(String nuevoAutor) {this.autor = nuevoAutor;}

    public String getIsbn() { return isbn;}
    public void setIsbn(String nuevoIsbn) {this.isbn = nuevoIsbn;}

    public String getEditorial() { return editorial;}
    public void setEditorial(String nuevaEditorial) {this.editorial = nuevaEditorial;}

    public int getPaginas() { return paginas;}
    public void setPaginas(int nuevaPaginas) {this.paginas = nuevaPaginas;}

    public String getSinopsis() { return sinopsis;}
    public void setSinopsis(String nuevaSinopsis) {this.sinopsis = nuevaSinopsis;}

    public LocalDate getInicioLectura() {return inicioLectura;}
    public void setInicioLectura(LocalDate nuevaFecha) {this.inicioLectura = nuevaFecha;}

    public LocalDate getFinLectura() {return finLectura;}
    public void setFinLectura(LocalDate nuevaFecha) {this.finLectura = nuevaFecha;}

    public EstadoLectura getEstado() { return estado;}
    public void setEstado(EstadoLectura nuevoEstado) {this.estado = nuevoEstado;}

    // toString()
    @Override 
    public String toString() {
        return String.format("%-5d %-40s %-30s %-15s %-10s",
        id, titulo, autor, isbn, estado.getValorDB());
    }

    /**
     * Modifica el estado de lectura según las fechas de inicio o fin de lectura.
     * Si no hay fecha fin cambia a "leido", si solo hay fecha de inicio cambia a "leyendo"
     * Si no hay ninguna fecha, no se modifica.
     */
    public void actualizarEstado() {
        if (finLectura != null) {
            setEstado(EstadoLectura.LEIDO);
        } else if (inicioLectura != null) {
            setEstado(EstadoLectura.LEYENDO);
        }
    }
}
