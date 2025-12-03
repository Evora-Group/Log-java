package school.sptech.avaliacoes;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Avaliacao {
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

    public Integer getFkMatricula() {
        return fkMatricula;
    }

    public void setFkMatricula(Integer fkMatricula) {
        this.fkMatricula = fkMatricula;
    }

    public Integer getFkDisciplina() {
        return fkDisciplina;
    }

    public void setFkDisciplina(Integer fkDisciplina) {
        this.fkDisciplina = fkDisciplina;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getNota() {
        return nota;
    }

    public void setNota(BigDecimal nota) {
        this.nota = nota;
    }

    public LocalDate getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(LocalDate dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Avaliacao{");
        sb.append("fkMatricula=").append(fkMatricula);
        sb.append(", fkDisciplina=").append(fkDisciplina);
        sb.append(", tipo='").append(tipo).append('\'');
        sb.append(", nota=").append(nota);
        sb.append(", dataAvaliacao=").append(dataAvaliacao);
        sb.append('}');
        return sb.toString();
    }
}