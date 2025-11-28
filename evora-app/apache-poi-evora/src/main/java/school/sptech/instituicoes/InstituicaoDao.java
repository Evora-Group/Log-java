package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;

public class InstituicaoDao {

    private final JdbcTemplate jdbcTemplate;

    public InstituicaoDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Instituicao instituicao) {

        String sql = "INSERT IGNORE INTO instituicao (id_instituicao, nome, uf) VALUES (?, ?, ?)";

        jdbcTemplate.update(sql,
                instituicao.getIdInstituicao(), // Vai entrar na coluna id_instituicao
                instituicao.getNome(),
                instituicao.getUf()
        );
    }

    public Boolean existsById(Integer idInstituicao) {
        // CORREÇÃO: WHERE id_instituicao (o nome que está no banco)
        String sql = "SELECT COUNT(1) FROM instituicao WHERE id_instituicao = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idInstituicao);

        return (count != null && count > 0);
    }
}