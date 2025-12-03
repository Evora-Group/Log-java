package school.sptech;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class GeradorDados {

    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger = LoggerFactory.getLogger(GeradorDados.class);

    public GeradorDados(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void gerarDadosFaltantes() {
        logger.info("🛠️ Iniciando geração automática de dados faltantes (Turmas, Disciplinas, Matrículas)...");

        try {
            // 1. GERAR DISCIPLINAS
            // Cria uma disciplina genérica para cada Instituição que ainda não tem disciplinas
            // "Se a instituição existe, crie uma disciplina 'Gestão de Tecnologia' para ela"
            String sqlDisciplinas = """
                INSERT INTO disciplina (fkInstituicao, nome, descricao)
                SELECT id_instituicao, 'Desenvolvimento de Sistemas', 'Disciplina Padrão Gerada'
                FROM instituicao
                WHERE id_instituicao NOT IN (SELECT DISTINCT fkInstituicao FROM disciplina);
            """;
            int discCriadas = jdbcTemplate.update(sqlDisciplinas);
            logger.info("✅ Disciplinas geradas: {}", discCriadas);


            // 2. GERAR TURMAS
            // Cria uma turma '2025-1' para cada Curso existente
            String sqlTurmas = """
                INSERT INTO turma (fkCurso, nome_sigla, ano, semestre, periodo)
                SELECT id_curso, 'ADS-2025', 2025, 1, 'Noturno'
                FROM curso
                WHERE id_curso NOT IN (SELECT DISTINCT fkCurso FROM turma);
            """;
            int turmasCriadas = jdbcTemplate.update(sqlTurmas);
            logger.info("✅ Turmas geradas: {}", turmasCriadas);


            // 3. GERAR MATRÍCULAS
            // Pega todos os alunos e matricula eles na PRIMEIRA turma que encontrar da lista
            // (Isso é um 'hack' para funcionar. Num sistema real, saberíamos a turma exata).
            String sqlMatriculas = """
                INSERT IGNORE INTO matricula (fkAluno, fkTurma, data_matricula, ativo)
                SELECT 
                    a.ra, 
                    (SELECT id_turma FROM turma ORDER BY id_turma LIMIT 1), -- Pega a primeira turma do banco
                    CURDATE(), 
                    1
                FROM aluno a
                WHERE a.ra NOT IN (SELECT DISTINCT fkAluno FROM matricula);
            """;
            int matriculasCriadas = jdbcTemplate.update(sqlMatriculas);
            logger.info("✅ Matrículas geradas: {}", matriculasCriadas);

        } catch (Exception e) {
            logger.error("❌ Erro ao gerar dados automáticos: ", e);
        }
    }
}