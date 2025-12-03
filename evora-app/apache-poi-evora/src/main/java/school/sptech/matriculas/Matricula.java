package school.sptech.matriculas;
import java.time.LocalDate;

public class Matricula {

    private Integer idMatricula;
    private Integer fkAluno; // RA
    private Integer fkTurma;
    private LocalDate dataMatricula;
    private Boolean ativo;

    public Matricula() {
    }

    public Matricula(Integer idMatricula, Integer fkAluno, Integer fkTurma, LocalDate dataMatricula, Boolean ativo) {
        this.idMatricula = idMatricula;
        this.fkAluno = fkAluno;
        this.fkTurma = fkTurma;
        this.dataMatricula = dataMatricula;
        this.ativo = ativo;
    }

    public Integer getIdMatricula() {
        return idMatricula;
    }

    public void setIdMatricula(Integer idMatricula) {
        this.idMatricula = idMatricula;
    }

    public Integer getFkAluno() {
        return fkAluno;
    }

    public void setFkAluno(Integer fkAluno) {
        this.fkAluno = fkAluno;
    }

    public Integer getFkTurma() {
        return fkTurma;
    }

    public void setFkTurma(Integer fkTurma) {
        this.fkTurma = fkTurma;
    }

    public LocalDate getDataMatricula() {
        return dataMatricula;
    }

    public void setDataMatricula(LocalDate dataMatricula) {
        this.dataMatricula = dataMatricula;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Matricula{");
        sb.append("idMatricula=").append(idMatricula);
        sb.append(", fkAluno=").append(fkAluno);
        sb.append(", fkTurma=").append(fkTurma);
        sb.append(", dataMatricula=").append(dataMatricula);
        sb.append(", ativo=").append(ativo);
        sb.append('}');
        return sb.toString();
    }
}