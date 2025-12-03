package school.sptech;

import com.github.pjfanning.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

// Imports dos pacotes organizados
import school.sptech.alunos.*;
import school.sptech.avaliacoes.*;
import school.sptech.cursos.*;
import school.sptech.disciplinas.*; // Assumindo que criou este pacote
import school.sptech.frequencias.*;
import school.sptech.instituicoes.*;
import school.sptech.matriculas.*;   // Assumindo que criou este pacote
import school.sptech.turmas.*;       // Assumindo que criou este pacote
import school.sptech.grades.*;       // Assumindo que criou este pacote

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Region region = Region.US_EAST_1;

        // 1. Variáveis de Ambiente (Mapeamento completo dos arquivos S3)
        String instituicaoKey = System.getenv("S3_FILE_KEY_INSTITUICAO");
        String cursoKey       = System.getenv("S3_FILE_KEY_CURSO");
        String alunoKey       = System.getenv("S3_FILE_KEY_ALUNO");
        String disciplinaKey  = System.getenv("S3_FILE_KEY_DISCIPLINA"); // Novo
        String gradeKey       = System.getenv("S3_FILE_KEY_GRADE");       // Novo
        String turmaKey       = System.getenv("S3_FILE_KEY_TURMA");       // Novo
        String matriculaKey   = System.getenv("S3_FILE_KEY_MATRICULA");   // Novo
        String avaliacaoKey   = System.getenv("S3_FILE_KEY_AVALIACAO");
        String frequenciaKey  = System.getenv("S3_FILE_KEY_FREQUENCIA");

        try (LeituraS3 leitorS3 = new LeituraS3(region)) {

            ConexaoBanco conexaoBanco = new ConexaoBanco();

            // 2. Instanciação dos DAOs
            InstituicaoDao instituicaoDao = new InstituicaoDao(conexaoBanco.getJdbcTemplate());
            CursosDao cursosDao           = new CursosDao(conexaoBanco.getJdbcTemplate());
            AlunoDao alunoDao             = new AlunoDao(conexaoBanco.getJdbcTemplate());
            DisciplinaDao disciplinaDao   = new DisciplinaDao(conexaoBanco.getJdbcTemplate());
            GradeCurricularDao gradeDao   = new GradeCurricularDao(conexaoBanco.getJdbcTemplate());
            TurmaDao turmaDao             = new TurmaDao(conexaoBanco.getJdbcTemplate());
            MatriculaDao matriculaDao     = new MatriculaDao(conexaoBanco.getJdbcTemplate());
            AvaliacaoDao avaliacaoDao     = new AvaliacaoDao(conexaoBanco.getJdbcTemplate());
            FrequenciaDao frequenciaDao   = new FrequenciaDao(conexaoBanco.getJdbcTemplate());

            // 3. Instanciação dos Processors (Leitores)
            RowProcessor instituicaoProc = new InstituicaoRowProcessor(instituicaoDao, 2023, "SP");
            RowProcessor cursoProc       = new CursoRowProcessor(cursosDao, instituicaoDao);
            RowProcessor alunoProc       = new AlunoRowProcessor(alunoDao);
            RowProcessor disciplinaProc  = new DisciplinaRowProcessor(disciplinaDao);
            RowProcessor gradeProc       = new GradeCurricularRowProcessor(gradeDao);
            RowProcessor turmaProc       = new TurmaRowProcessor(turmaDao);
            RowProcessor matriculaProc   = new MatriculaRowProcessor(matriculaDao);
            RowProcessor avaliacaoProc   = new AvaliacaoRowProcessor(avaliacaoDao);
            RowProcessor frequenciaProc  = new FrequenciaRowProcessor(frequenciaDao);

            logger.info("🚀 Iniciando Pipeline de Carga de Dados...");

            // -----------------------------------------------------------
            // ETAPA 1: Fundações (Sem dependências FK)
            // -----------------------------------------------------------
            processarArquivo(leitorS3, instituicaoKey, instituicaoProc, "Instituições");
            processarArquivo(leitorS3, alunoKey,       alunoProc,       "Alunos");

            // -----------------------------------------------------------
            // ETAPA 2: Estrutura Acadêmica Básica
            // -----------------------------------------------------------
            // Curso depende de Instituição
            processarArquivo(leitorS3, cursoKey,       cursoProc,       "Cursos");

            // Disciplina depende de Instituição
            processarArquivo(leitorS3, disciplinaKey,  disciplinaProc,  "Disciplinas");

            // -----------------------------------------------------------
            // ETAPA 3: Conexões (Turmas e Grades)
            // -----------------------------------------------------------
            // Grade depende de Curso e Disciplina
            processarArquivo(leitorS3, gradeKey,       gradeProc,       "Grade Curricular");

            // Turma depende de Curso
            processarArquivo(leitorS3, turmaKey,       turmaProc,       "Turmas");

            // -----------------------------------------------------------
            // ETAPA 4: Operação (Matrículas)
            // -----------------------------------------------------------
            // Matrícula depende de Aluno e Turma (O elo principal)
            processarArquivo(leitorS3, matriculaKey,   matriculaProc,   "Matrículas");

            // -----------------------------------------------------------
            // ETAPA 5: Dados Transacionais (Notas e Faltas)
            // -----------------------------------------------------------
            // Dependem de Matrícula e Disciplina
            processarArquivo(leitorS3, avaliacaoKey,   avaliacaoProc,   "Avaliações");
            processarArquivo(leitorS3, frequenciaKey,  frequenciaProc,  "Frequências");

            // Sucesso
            SlackNotifier.sendRichMessage("✅", "Carga Full Completa",
                    "Todas as bases foram processadas com sucesso.", "#36A64F");

        } catch (Exception e) {
            logger.error("Erro fatal no Main.", e);
            SlackNotifier.sendRichMessage("🔴", "Erro Fatal", e.getMessage(), "#FF0000");
        }
        logger.info("Fim da execução.");
    }

    /**
     * Método genérico para ler do S3, processar linha a linha e salvar no banco.
     */
    private static void processarArquivo(LeituraS3 leitorS3, String fileKey,
                                         RowProcessor processor, String nomeProcesso) {

        logger.info("\n--- 📂 Iniciando carga: {} ---", nomeProcesso);

        if (fileKey == null || fileKey.isEmpty()) {
            logger.warn("⚠️ Variável S3 para '{}' não definida. Pulando etapa.", nomeProcesso);
            return;
        }

        long contadorLinhas = 0;
        long contadorSalvos = 0;
        final int LINHA_CABECALHO = 1; // Ajuste se seu Excel começar na linha 0 ou 1

        try (ResponseInputStream<GetObjectResponse> s3ObjectStream = leitorS3.obterInputStream(fileKey);
             Workbook workbook = StreamingReader.builder()
                     .rowCacheSize(100)
                     .bufferSize(4096)
                     .open(s3ObjectStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                contadorLinhas++;
                if (contadorLinhas <= LINHA_CABECALHO) continue;

                try {
                    // Processa e acumula no buffer interno do processor
                    if (processor.processAndSave(row)) {
                        contadorSalvos++;
                    }
                } catch (Exception e) {
                    // Log silencioso ou debug para não poluir console em erros esperados
                    // logger.debug("Erro linha {}: {}", contadorLinhas, e.getMessage());
                }

                if (contadorLinhas % 10000 == 0) {
                    logger.info("... processadas {} linhas.", contadorLinhas);
                }
            }

            // --- OTIMIZAÇÃO: FLUSH FINAL ---
            // Garante que o que sobrou no buffer (ex: últimos 450 registros) seja salvo
            logger.info("💾 Salvando lote final de {}...", nomeProcesso);

            if (processor instanceof InstituicaoRowProcessor) ((InstituicaoRowProcessor) processor).flush();
            else if (processor instanceof CursoRowProcessor) ((CursoRowProcessor) processor).flush();
            else if (processor instanceof AlunoRowProcessor) ((AlunoRowProcessor) processor).flush();
            else if (processor instanceof DisciplinaRowProcessor) ((DisciplinaRowProcessor) processor).flush();
            else if (processor instanceof TurmaRowProcessor) ((TurmaRowProcessor) processor).flush();
            else if (processor instanceof GradeCurricularRowProcessor) ((GradeCurricularRowProcessor) processor).flush();
            else if (processor instanceof MatriculaRowProcessor) ((MatriculaRowProcessor) processor).flush();
            else if (processor instanceof AvaliacaoRowProcessor) ((AvaliacaoRowProcessor) processor).flush();
            else if (processor instanceof FrequenciaRowProcessor) ((FrequenciaRowProcessor) processor).flush();
            // -------------------------------

        } catch (Exception e) {
            logger.error("❌ Erro crítico em {}: {}", nomeProcesso, e.getMessage());
        }

        logger.info("--- Fim {}. Linhas lidas: {}. ---", nomeProcesso, contadorLinhas);
    }
}