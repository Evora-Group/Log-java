package school.sptech;

public class Curso {

    private Integer idCurso;
    private String descricao;
    private String modalidade;
    private Integer idInstituicao;

    public Curso() {
    }

    public Curso(Integer idCurso, String descricao, String modalidade, Integer idInstituicao) {
        this.idCurso = idCurso;
        this.descricao = descricao;
        this.modalidade = modalidade;
        this.idInstituicao = idInstituicao;
    }

    public Integer getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Integer idCurso) {
        this.idCurso = idCurso;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Curso{");
        sb.append("idCurso=").append(idCurso);
        sb.append(", descricao='").append(descricao).append('\'');
        sb.append(", modalidade='").append(modalidade).append('\'');
        sb.append(", idInstituicao=").append(idInstituicao);
        sb.append('}');
        return sb.toString();
    }
}
