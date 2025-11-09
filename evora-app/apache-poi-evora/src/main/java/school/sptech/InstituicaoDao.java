package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;

public class InstituicaoDao {

    private final JdbcTemplate jdbcTemplate;

    public InstituicaoDao(JdbcTemplate jdbcTemplate) throws InterruptedException {
        this.jdbcTemplate = jdbcTemplate;
    }

    ConexaoBanco conexaoBanco = new ConexaoBanco();
    
    Instituicao instituicao = new Instituicao();



    public void save(Instituicao instituicao) {

        jdbcTemplate
                .update("INSERT IGNORE INTO Instituicao (nome, idInstituicao, uf, idMunicipio) VALUES (?, ?, ?, ?);",
                        instituicao.getNome(),
                        instituicao.getIdInstituicao(),
                        instituicao.getUf(),
                        instituicao.getIdMunicipio());
    }

}
