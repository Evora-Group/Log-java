package school.sptech.grades;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class GradeCurricularDao {
    private final JdbcTemplate jdbcTemplate;

    public GradeCurricularDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public void saveAll(List<GradeCurricular> lista) {
        // Tabela de relacionamento N:N
        String sql = "INSERT IGNORE INTO grade_curricular (fkCurso, fkDisciplina) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                GradeCurricular g = lista.get(i);
                ps.setInt(1, g.getFkCurso());
                ps.setInt(2, g.getFkDisciplina());
            }
            @Override
            public int getBatchSize() { return lista.size(); }
        });
    }
}
