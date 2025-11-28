package school.sptech;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Avaliacao {
    private Integer idAvaliacao;
    private Integer fkMatricula;
    private Integer fkDisciplina;
    private String tipo;
    private BigDecimal nota;
    private LocalDate dataAvaliacao;

    public Avaliacao() {}

    public Avaliacao(Integer fkMatricula, Integer fkDisciplina, String tipo, BigDecimal nota, LocalDate dataAvaliacao) {
        this.fkMatricula = fkMatricula;
        this.fkDisciplina = fkDisciplina;
        this.tipo = tipo;
        this.nota = nota;
        this.dataAvaliacao = dataAvaliacao;
    }

    // Getters e Setters
    public Integer getIdAvaliacao() { return idAvaliacao; }
    public void setIdAvaliacao(Integer idAvaliacao) { this.idAvaliacao = idAvaliacao; }
    public Integer getFkMatricula() { return fkMatricula; }
    public void setFkMatricula(Integer fkMatricula) { this.fkMatricula = fkMatricula; }
    public Integer getFkDisciplina() { return fkDisciplina; }
    public void setFkDisciplina(Integer fkDisciplina) { this.fkDisciplina = fkDisciplina; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public BigDecimal getNota() { return nota; }
    public void setNota(BigDecimal nota) { this.nota = nota; }
    public LocalDate getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(LocalDate dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }
}