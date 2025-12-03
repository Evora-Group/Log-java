package school.sptech.matriculas;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MatriculaDao {
    private final JdbcTemplate jdbcTemplate;
    public MatriculaDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public void saveAll(List<Matricula> lista) {
        String sql = "INSERT IGNORE INTO matricula (id_matricula, fkAluno, fkTurma, data_matricula, ativo) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Matricula m = lista.get(i);
                ps.setInt(1, m.getIdMatricula());
                ps.setInt(2, m.getFkAluno());
                ps.setInt(3, m.getFkTurma());
                ps.setDate(4, java.sql.Date.valueOf(m.getDataMatricula()));
                ps.setBoolean(5, m.getAtivo());
            }
            @Override
            public int getBatchSize() { return lista.size(); }
        });
    }
}