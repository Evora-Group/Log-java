package school.sptech.cursos;

public class Curso {

    private Integer idCurso;
    private String modalidade;
    private Integer idInstituicao;
    private String nome;

    public Curso() {
    }

    public Curso(Integer idCurso, String modalidade, Integer idInstituicao, String nome) {
        this.idCurso = idCurso;
        this.modalidade = modalidade;
        this.idInstituicao = idInstituicao;
        this.nome = nome;
    }

    public Integer getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Integer idCurso) {
        this.idCurso = idCurso;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public Integer getIdInstituicao() {
        return idInstituicao;
    }

    public void setIdInstituicao(Integer idInstituicao) {
        this.idInstituicao = idInstituicao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Curso{");
        sb.append("idCurso=").append(idCurso);
        sb.append(", modalidade='").append(modalidade).append('\'');
        sb.append(", idInstituicao=").append(idInstituicao);
        sb.append(", nome='").append(nome).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
