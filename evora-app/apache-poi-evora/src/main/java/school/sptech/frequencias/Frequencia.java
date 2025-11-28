package school.sptech;

import java.time.LocalDate;

public class Frequencia {
    private Integer idFrequencia;
    private Integer fkMatricula;
    private Integer fkDisciplina;
    private LocalDate dataAula;
    private Boolean presente; // TINYINT(1) no banco vira Boolean/Integer
    private String justificativa;

    public Frequencia() {}

    public Frequencia(Integer fkMatricula, Integer fkDisciplina, LocalDate dataAula, Boolean presente, String justificativa) {
        this.fkMatricula = fkMatricula;
        this.fkDisciplina = fkDisciplina;
        this.dataAula = dataAula;
        this.presente = presente;
        this.justificativa = justificativa;
    }

    // Getters e Setters
    public Integer getIdFrequencia() { return idFrequencia; }
    public void setIdFrequencia(Integer idFrequencia) { this.idFrequencia = idFrequencia; }
    public Integer getFkMatricula() { return fkMatricula; }
    public void setFkMatricula(Integer fkMatricula) { this.fkMatricula = fkMatricula; }
    public Integer getFkDisciplina() { return fkDisciplina; }
    public void setFkDisciplina(Integer fkDisciplina) { this.fkDisciplina = fkDisciplina; }
    public LocalDate getDataAula() { return dataAula; }
    public void setDataAula(LocalDate dataAula) { this.dataAula = dataAula; }
    public Boolean getPresente() { return presente; }
    public void setPresente(Boolean presente) { this.presente = presente; }
    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
}