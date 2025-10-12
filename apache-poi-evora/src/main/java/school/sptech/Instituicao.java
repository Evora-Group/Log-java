package school.sptech;

public class Instituicao {

    private Integer idInstituicao;
    private Integer idMunicipio;
    private String nome;
    private String uf;


    public Instituicao() {
    }

    public Instituicao(Integer idInstituicao, Integer idMunicipio, String nome, String uf) {
        this.idInstituicao = idInstituicao;
        this.idMunicipio = idMunicipio;
        this.nome = nome;
        this.uf = uf;
    }

    public Integer getIdInstituicao() {
        return idInstituicao;
    }

    public void setIdInstituicao(Integer idInstituicao) {
        this.idInstituicao = idInstituicao;
    }

    public Integer getIdMunicipio() {
        return idMunicipio;
    }

    public void setIdMunicipio(Integer idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Instituicao{");
        sb.append("idInstituicao=").append(idInstituicao);
        sb.append(", idMunicipio=").append(idMunicipio);
        sb.append(", nome='").append(nome).append('\'');
        sb.append(", uf='").append(uf).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
