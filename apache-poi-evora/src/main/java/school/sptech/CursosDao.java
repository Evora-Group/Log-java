package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;

public class CursosDao {

    private final JdbcTemplate jdbcTemplate;

    public CursosDao(JdbcTemplate jdbcTemplate) throws InterruptedException {
        this.jdbcTemplate = jdbcTemplate;
    }

    Curso curso = new Curso();

    ConexaoBanco conexaoBanco = new ConexaoBanco();


    public void save(Curso curso) {

        jdbcTemplate
                .update("INSERT INTO Curso (idCurso, fkInstituicao, descricao, modalidade) VALUES (?, ?, ?, ?);",
                        curso.getIdCurso(),
                        curso.getIdInstituicao(),
                        curso.getDescricao(),
                        curso.getModalidade());
    }

    }


