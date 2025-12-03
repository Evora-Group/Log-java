package school.sptech.turmas;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class TurmaDao {
    private final JdbcTemplate jdbcTemplate;

    public TurmaDao(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public void saveAll(List<Turma> lista) {
        String sql = "INSERT IGNORE INTO turma (id_turma, fkCurso, nome_sigla, ano, semestre) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Turma t = lista.get(i);
                ps.setInt(1, t.getIdTurma());
                ps.setInt(2, t.getFkCurso());
                ps.setString(3, t.getNomeSigla());
                ps.setInt(4, t.getAno());
                ps.setInt(5, t.getSemestre());
            }
            @Override
            public int getBatchSize() { return lista.size(); }
        });
    }
}