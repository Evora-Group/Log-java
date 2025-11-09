package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;

public class CursosDao {

    private final JdbcTemplate jdbcTemplate;

    public CursosDao(JdbcTemplate jdbcTemplate) throws InterruptedException {
        this.jdbcTemplate = jdbcTemplate;
    }



    ConexaoBanco conexaoBanco = new ConexaoBanco();


    Curso curso = new Curso();

    public void save(Curso curso) {

        jdbcTemplate
                .update("INSERT INTO Curso (idCurso, fkInstituicao, descricao, modalidade) VALUES (?, ?, ?, ?);",
                        curso.getIdCurso(),
                        curso.getIdInstituicao(),
                        curso.getDescricao(),
                        curso.getModalidade());
    }

    }


