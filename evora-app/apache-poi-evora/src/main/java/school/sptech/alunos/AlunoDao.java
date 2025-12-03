package school.sptech.alunos;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AlunoDao {
    private final JdbcTemplate jdbcTemplate;

    public AlunoDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveAll(List<Aluno> alunos) {
        String sql = "INSERT IGNORE INTO aluno (ra, nome, email) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Aluno aluno = alunos.get(i);
                ps.setInt(1, aluno.getRa());
                ps.setString(2, aluno.getNome());
                ps.setString(3, aluno.getEmail());
            }

            @Override
            public int getBatchSize() {
                return alunos.size();
            }
        });
    }
}