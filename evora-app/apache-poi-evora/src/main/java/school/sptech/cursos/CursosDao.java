package school.sptech.cursos;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CursosDao {

    private final JdbcTemplate jdbcTemplate;

    public CursosDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Método legado para salvar um único curso.
     * Mantido para compatibilidade, caso algum teste unitário o utilize.
     */
    public void save(Curso curso) {
        String sql = "INSERT IGNORE INTO curso (id_curso, fkInstituicao, nome, modalidade) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                curso.getIdCurso(),
                curso.getIdInstituicao(),
                curso.getNome(),
                curso.getModalidade());
    }

    /**
     * MÉTODO OTIMIZADO (BATCH INSERT)
     * Insere uma lista de cursos de uma só vez, reduzindo drasticamente as idas ao banco de dados.
     * Isso resolve o erro "cannot find symbol: method saveAll".
     */
    public void saveAll(List<Curso> cursos) {
        String sql = "INSERT IGNORE INTO curso (id_curso, fkInstituicao, nome, modalidade) VALUES (?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Curso curso = cursos.get(i);

                // Mapeia os valores do objeto Java para os ? do SQL
                ps.setInt(1, curso.getIdCurso());
                ps.setInt(2, curso.getIdInstituicao());
                ps.setString(3, curso.getNome());
                ps.setString(4, curso.getModalidade());
            }

            @Override
            public int getBatchSize() {
                // Define quantas vezes o SQL será executado neste lote
                return cursos.size();
            }
        });
    }
}