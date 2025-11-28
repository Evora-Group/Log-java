package school.sptech;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AvaliacaoDao {
    private final JdbcTemplate jdbcTemplate;

    public AvaliacaoDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveAll(List<Avaliacao> avaliacoes) {
        String sql = "INSERT IGNORE INTO avaliacao (fkMatricula, fkDisciplina, tipo, nota, data_avaliacao) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Avaliacao a = avaliacoes.get(i);
                ps.setInt(1, a.getFkMatricula());
                ps.setInt(2, a.getFkDisciplina());
                ps.setString(3, a.getTipo());
                ps.setBigDecimal(4, a.getNota());
                ps.setDate(5, java.sql.Date.valueOf(a.getDataAvaliacao()));
            }

            @Override
            public int getBatchSize() {
                return avaliacoes.size();
            }
        });
    }
}