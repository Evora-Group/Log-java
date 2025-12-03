package school.sptech.instituicoes;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import school.sptech.*; // Certifique-se de importar sua classe de modelo correta

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class InstituicaoDao {

    private final JdbcTemplate jdbcTemplate;

    public InstituicaoDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Método otimizado para salvar listas grandes de uma só vez.
     */
    public void saveAll(List<Instituicao> instituicoes) {
        // Mantivemos as colunas id_instituicao, nome e uf conforme seu SQL original.
        // Se você criou a coluna 'id_municipio' no banco, adicione-a aqui no INSERT.
        String sql = "INSERT IGNORE INTO instituicao (id_instituicao, nome, uf) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Instituicao instituicao = instituicoes.get(i);

                ps.setInt(1, instituicao.getIdInstituicao());
                ps.setString(2, instituicao.getNome());
                ps.setString(3, instituicao.getUf());

                // Obs: O idMunicipio está sendo lido pelo processador,
                // mas não está sendo salvo aqui porque a tabela 'instituicao'
                // do seu script SQL não tinha a coluna 'id_municipio' ou 'cidade' mapeada corretamente.
            }

            @Override
            public int getBatchSize() {
                return instituicoes.size();
            }
        });
    }

    /**
     * Método legado para salvar um único registro (se necessário).
     */
    public void save(Instituicao instituicao) {
        String sql = "INSERT IGNORE INTO instituicao (id_instituicao, nome, uf) VALUES (?, ?, ?)";

        jdbcTemplate.update(sql,
                instituicao.getIdInstituicao(),
                instituicao.getNome(),
                instituicao.getUf()
        );
    }

    public Boolean existsById(Integer idInstituicao) {
        String sql = "SELECT COUNT(1) FROM instituicao WHERE id_instituicao = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, idInstituicao);
        return (count != null && count > 0);
    }
}