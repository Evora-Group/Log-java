package school.sptech;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Region region = Region.US_EAST_1;

        // 1. Busque as chaves dos 3 arquivos das variáveis de ambiente
        // Ex: "dados/instituicoes.xlsx"
        String instituicaoFileKey = System.getenv("S3_FILE_KEY_INSTITUICAO");
        // Ex: "dados/cursos.xlsx"
//        String cursoFileKey = System.getenv("S3_FILE_KEY_CURSO");
//        // Ex: "dados/alunos.xlsx"
//        String alunoFileKey = System.getenv("S3_FILE_KEY_ALUNO");


        // 2. Use try-with-resources para o Leitor S3 e configure os DAOs
        try (LeituraS3 leitorS3 = new LeituraS3(region)) {

            ConexaoBanco conexaoBanco = new ConexaoBanco();

            // 3. Crie os DAOs
            InstituicaoDao instituicaoDao = new InstituicaoDao(conexaoBanco.getJdbcTemplate());
            CursosDao cursosDao = new CursosDao(conexaoBanco.getJdbcTemplate());

            // 4. Crie as Estratégias de Processamento (Processors)
            RowProcessor instituicaoProcessor = new InstituicaoRowProcessor(instituicaoDao, 2023, "SP");


            // 5. Execute os processos em sequência
            processarArquivo(leitorS3, instituicaoFileKey, instituicaoProcessor, "Instituições");


        } catch (InterruptedException e) {
            logger.error("Conexão com o banco foi interrompida.", e);
        } catch (Exception e) {
            logger.error("Ocorreu um erro fatal em um dos processos de importação.", e);
        }

        logger.info("Todos os processos de importação foram finalizados.");
    }

    /**
     * Método auxiliar que executa um ciclo de importação (S3 -> Excel Stream -> BD).
     * Ele é genérico e funciona para qualquer arquivo, graças ao RowProcessor.
     */
    private static void processarArquivo(LeituraS3 leitorS3, String fileKey,
                                         RowProcessor processor, String nomeProcesso) {

        logger.info("\n--- Iniciando processo de carga: {} ---", nomeProcesso);
        if (fileKey == null || fileKey.isEmpty()) {
            logger.warn("AVISO: Chave S3 para '{}' não definida (Ex: S3_FILE_KEY_{}). Pulando...",
                    nomeProcesso, nomeProcesso.toUpperCase());
            return;
        }

        long contadorLinhas = 0;
        long contadorSalvos = 0;
        final int LOG_PROGRESSO_INTERVALO = 10000;
        final int LINHA_CABECALHO = 1; // Pula a primeira linha

        // Use try-with-resources para o Stream do S3 e o Workbook de Streaming
        try (ResponseInputStream<GetObjectResponse> s3ObjectStream = leitorS3.obterInputStream(fileKey);
             Workbook workbook = StreamingReader.builder()
                     .rowCacheSize(100)    // Cache de linhas em memória
                     .bufferSize(4096)     // Buffer de leitura do arquivo
                     .open(s3ObjectStream)) { // Abre o Stream do S3

            logger.info("Workbook {} aberto em modo streaming...", fileKey);
            Sheet sheet = workbook.getSheetAt(0); // Pega a primeira planilha

            // A MÁGICA: Itera linha a linha sem carregar tudo na memória
            for (Row row : sheet) {
                contadorLinhas++;

                if (contadorLinhas <= LINHA_CABECALHO) {
                    continue; // Pula o cabeçalho
                }

                // Boa prática: Trata erro por linha
                // Se uma linha falhar (ex: NuberFormatException), ela não para o processo inteiro.
                try {
                    if (processor.processAndSave(row)) {
                        contadorSalvos++;
                    }
                } catch (Exception e) {
                    logger.warn("Erro ao processar linha {}: {}", contadorLinhas, e.getMessage(), e);
                }

                // Log de progresso
                if (contadorLinhas % LOG_PROGRESSO_INTERVALO == 0) {
                    logger.info("Processadas {} linhas. Salvos {} registros.", contadorLinhas, contadorSalvos);
                }
            }

        } catch (Exception e) {
            logger.error("Ocorreu um erro fatal durante o processo de {}: {}", nomeProcesso, e.getMessage(), e);
        }

        logger.info("--- Processo '{}' finalizado. Total de linhas lidas: {}. Registros salvos: {} ---",
                nomeProcesso, (contadorLinhas - LINHA_CABECALHO), contadorSalvos);
    }
}