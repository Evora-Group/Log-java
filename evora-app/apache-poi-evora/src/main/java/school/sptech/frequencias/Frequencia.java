package school.sptech.frequencias;

import java.time.LocalDate;

public class Frequencia {
    private Integer fkMatricula;
    private Integer fkDisciplina;
    private LocalDate dataAula;
    private Boolean presente; // TINYINT(1) no banco vira Boolean/Integer

    public Frequencia() {}

    public Frequencia(Integer fkMatricula, Integer fkDisciplina, LocalDate dataAula, Boolean presente) {
        this.fkMatricula = fkMatricula;
        this.fkDisciplina = fkDisciplina;
        this.dataAula = dataAula;
        this.presente = presente;
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

    public LocalDate getDataAula() {
        return dataAula;
    }

    public void setDataAula(LocalDate dataAula) {
        this.dataAula = dataAula;
    }

    public Boolean getPresente() {
        return presente;
    }

    public void setPresente(Boolean presente) {
        this.presente = presente;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Frequencia{");
        sb.append("fkMatricula=").append(fkMatricula);
        sb.append(", fkDisciplina=").append(fkDisciplina);
        sb.append(", dataAula=").append(dataAula);
        sb.append(", presente=").append(presente);
        sb.append('}');
        return sb.toString();
    }
}