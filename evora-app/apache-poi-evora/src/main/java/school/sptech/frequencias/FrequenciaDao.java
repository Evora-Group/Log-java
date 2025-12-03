package school.sptech.frequencias;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class FrequenciaDao {
    private final JdbcTemplate jdbcTemplate;

    public FrequenciaDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveAll(List<Frequencia> frequencias) {
        String sql = "INSERT IGNORE INTO frequencia (fkMatricula, fkDisciplina, data_aula, presente) VALUES (?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Frequencia f = frequencias.get(i);
                ps.setInt(1, f.getFkMatricula());
                ps.setInt(2, f.getFkDisciplina());
                ps.setDate(3, java.sql.Date.valueOf(f.getDataAula()));
                ps.setBoolean(4, f.getPresente());
//                ps.setString(5, f.getJustificativa());
            }

            @Override
            public int getBatchSize() {
                return frequencias.size();
            }
        });
    }
}