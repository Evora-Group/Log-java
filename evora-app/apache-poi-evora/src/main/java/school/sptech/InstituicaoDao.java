package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;

public class InstituicaoDao {

    private final JdbcTemplate jdbcTemplate;

    public InstituicaoDao(JdbcTemplate jdbcTemplate) throws InterruptedException {
        this.jdbcTemplate = jdbcTemplate;
    }

    Instituicao instituicao = new Instituicao();

    ConexaoBanco conexaoBanco = new ConexaoBanco();


    public void save(Instituicao instituicao) {

        jdbcTemplate
                .update("INSERT IGNORE INTO Instituicao (nome, idInstituicao, uf, idMunicipio) VALUES (?, ?, ?, ?);",
                        instituicao.getNome(),
                        instituicao.getIdInstituicao(),
                        instituicao.getUf(),
                        instituicao.getIdMunicipio());
    }

    public Boolean existsById(Integer idInstituicao) {
        String sql = "SELECT COUNT(1) FROM Instituicao WHERE idInstituicao = ?";

        // Executa a query "SELECT COUNT..." e espera um Integer (número) de volta
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idInstituicao);

        // Retorna true se a contagem for maior que 0
        return (count != null && count > 0);
    }
}
