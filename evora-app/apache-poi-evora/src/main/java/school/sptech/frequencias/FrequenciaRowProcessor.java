package school.sptech.frequencias;

import org.apache.poi.ss.usermodel.Row;
import school.sptech.RowProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class FrequenciaRowProcessor extends RowProcessor {

    private static final Logger logger = LoggerFactory.getLogger(FrequenciaRowProcessor.class);

    // Índices das colunas no Excel
    private static final int COL_ID_MATRICULA = 0;
    private static final int COL_ID_DISCIPLINA = 1;
    private static final int COL_DATA = 2;
    private static final int COL_PRESENTE = 3;

    private final FrequenciaDao frequenciaDao;
    private final List<Frequencia> buffer = new ArrayList<>();
    private static final int TAMANHO_LOTE = 1000;

    // Formatador para data brasileira (dd/MM/yyyy)
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public FrequenciaRowProcessor(FrequenciaDao frequenciaDao) {
        this.frequenciaDao = frequenciaDao;
    }

    @Override
    public boolean processAndSave(Row row) throws Exception {
        Double idMatricula = getSafeDouble(row, COL_ID_MATRICULA);
        Double idDisciplina = getSafeDouble(row, COL_ID_DISCIPLINA);
        String dataStr = getSafeString(row, COL_DATA);
        Double presenteDouble = getSafeDouble(row, COL_PRESENTE);

        // Validação básica de chaves estrangeiras
        if (idMatricula == null || idDisciplina == null) {
            return false; // Pula linha se não tiver IDs
        }

        Frequencia frequencia = new Frequencia();
        frequencia.setFkMatricula(idMatricula.intValue());
        frequencia.setFkDisciplina(idDisciplina.intValue());

        // --- LÓGICA DE DATA ROBUSTA ---
        if (dataStr != null && !dataStr.trim().isEmpty()) {
            try {
                String dataLimpa = dataStr.trim(); // Remove espaços em branco acidentais

                // Verifica se o formato é ISO (yyyy-MM-dd)
                if (dataLimpa.contains("-")) {
                    // Se tiver hora (ex: 2024-08-01 10:00), pega só a data
                    if (dataLimpa.length() >= 10) {
                        frequencia.setDataAula(LocalDate.parse(dataLimpa.substring(0, 10)));
                    } else {
                        // Data muito curta ou inválida para ISO
                        logger.warn("Data ISO inválida ou muito curta: '{}'", dataLimpa);
                        return false;
                    }
                }
                // Verifica se é formato Brasileiro (dd/MM/yyyy)
                else if (dataLimpa.contains("/")) {
                    // Se tiver hora (ex: 01/08/2024 10:00), pega só a data
                    if (dataLimpa.length() >= 10) {
                        String apenasData = dataLimpa.substring(0, 10);
                        frequencia.setDataAula(LocalDate.parse(apenasData, formatter));
                    } else {
                        // Tenta parse direto se for curto (ex: 1/8/2024) mas bate com o formatador
                        // O formatador dd/MM/yyyy exige zeros a esquerda, então isso cairia no catch
                        // mas é melhor tentar do que falhar direto.
                        frequencia.setDataAula(LocalDate.parse(dataLimpa, formatter));
                    }
                } else {
                    // Formato desconhecido (nem traço, nem barra)
                    return false;
                }
            } catch (DateTimeParseException e) {
                // Loga o erro específico para sabermos qual data falhou
                logger.error("Erro de parse de data na linha {}: '{}'. Ignorando registro.", row.getRowNum(), dataStr);
                return false;
            }
        } else {
            return false; // Sem data, sem registro
        }

        // Conversão de Double (1.0/0.0) para Boolean
        frequencia.setPresente(presenteDouble != null && presenteDouble == 1.0);

        // Adiciona ao buffer
        buffer.add(frequencia);

        // Se encheu o lote, salva no banco
        if (buffer.size() >= TAMANHO_LOTE) {
            flush();
        }
        return true;
    }

    /**
     * Salva qualquer registro restante no buffer.
     * Deve ser chamado ao final do loop principal.
     */
    public void flush() {
        if (!buffer.isEmpty()) {
            frequenciaDao.saveAll(buffer);
            buffer.clear();
        }
    }
}