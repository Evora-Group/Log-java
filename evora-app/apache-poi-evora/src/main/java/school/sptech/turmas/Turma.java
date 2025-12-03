package school.sptech.turmas;

public class Turma {

    private Integer idTurma;
    private Integer fkCurso;
    private String nomeSigla;
    private Integer ano;
    private Integer semestre;

    public Turma() {
    }

    public Turma(Integer idTurma, Integer fkCurso, String nomeSigla, Integer ano, Integer semestre) {
        this.idTurma = idTurma;
        this.fkCurso = fkCurso;
        this.nomeSigla = nomeSigla;
        this.ano = ano;
        this.semestre = semestre;

    }

    public Integer getIdTurma() {
        return idTurma;
    }

    public void setIdTurma(Integer idTurma) {
        this.idTurma = idTurma;
    }

    public Integer getFkCurso() {
        return fkCurso;
    }

    public void setFkCurso(Integer fkCurso) {
        this.fkCurso = fkCurso;
    }

    public String getNomeSigla() {
        return nomeSigla;
    }

    public void setNomeSigla(String nomeSigla) {
        this.nomeSigla = nomeSigla;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getSemestre() {
        return semestre;
    }

    public void setSemestre(Integer semestre) {
        this.semestre = semestre;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Turma{");
        sb.append("idTurma=").append(idTurma);
        sb.append(", fkCurso=").append(fkCurso);
        sb.append(", nomeSigla='").append(nomeSigla).append('\'');
        sb.append(", ano=").append(ano);
        sb.append(", semestre=").append(semestre);
        sb.append('}');
        return sb.toString();
    }
}