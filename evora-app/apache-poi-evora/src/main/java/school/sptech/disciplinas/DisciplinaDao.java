package school.sptech.disciplinas;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DisciplinaDao {
    private final JdbcTemplate jdbcTemplate;

    public DisciplinaDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public void saveAll(List<Disciplina> lista) {
        String sql = "INSERT IGNORE INTO disciplina (id_disciplina, fkInstituicao, nome) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Disciplina d = lista.get(i);
                ps.setInt(1, d.getIdDisciplina());
                ps.setInt(2, d.getFkInstituicao());
                ps.setString(3, d.getNome());

            }
            @Override
            public int getBatchSize() { return lista.size(); }
        });
    }
}