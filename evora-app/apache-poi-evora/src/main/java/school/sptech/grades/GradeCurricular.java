package school.sptech.grades;

public class GradeCurricular {
    private Integer fkCurso;
    private Integer fkDisciplina;

    public GradeCurricular() {
    }

    public GradeCurricular(Integer fkCurso, Integer fkDisciplina) {
        this.fkCurso = fkCurso;
        this.fkDisciplina = fkDisciplina;
    }

    public Integer getFkCurso() {
        return fkCurso;
    }

    public void setFkCurso(Integer fkCurso) {
        this.fkCurso = fkCurso;
    }

    public Integer getFkDisciplina() {
        return fkDisciplina;
    }

    public void setFkDisciplina(Integer fkDisciplina) {
        this.fkDisciplina = fkDisciplina;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GradeCurricular{");
        sb.append("fkCurso=").append(fkCurso);
        sb.append(", fkDisciplina=").append(fkDisciplina);
        sb.append('}');
        return sb.toString();
    }
}