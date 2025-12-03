package school.sptech.disciplinas;

public class Disciplina {
    private Integer idDisciplina;
    private Integer fkInstituicao;
    private String nome;

    public Disciplina() {
    }

    public Disciplina(Integer idDisciplina, Integer fkInstituicao, String nome) {
        this.idDisciplina = idDisciplina;
        this.fkInstituicao = fkInstituicao;
        this.nome = nome;
    }

    public Integer getIdDisciplina() {
        return idDisciplina;
    }

    public void setIdDisciplina(Integer idDisciplina) {
        this.idDisciplina = idDisciplina;
    }

    public Integer getFkInstituicao() {
        return fkInstituicao;
    }

    public void setFkInstituicao(Integer fkInstituicao) {
        this.fkInstituicao = fkInstituicao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Disciplina{");
        sb.append("idDisciplina=").append(idDisciplina);
        sb.append(", fkInstituicao=").append(fkInstituicao);
        sb.append(", nome='").append(nome).append('\'');
        sb.append('}');
        return sb.toString();
    }
}