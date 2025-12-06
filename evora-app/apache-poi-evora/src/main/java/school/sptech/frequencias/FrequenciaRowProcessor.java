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

    // Índices
    private static final int COL_ID_MATRICULA = 0;
    private static final int COL_ID_DISCIPLINA = 1;
    private static final int COL_DATA = 2;
    private static final int COL_PRESENTE = 3;

    private final FrequenciaDao frequenciaDao;
    private final List<Frequencia> buffer = new ArrayList<>();
    private static final int TAMANHO_LOTE = 1000;

    // Formatador para data brasileira
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

        if (idMatricula == null || idDisciplina == null) {
            // Logger temporário para debug
            // logger.warn("Linha pulada: Matricula ou Disciplina nulos");
            return false;
        }

        Frequencia frequencia = new Frequencia();
        frequencia.setFkMatricula(idMatricula.intValue());
        frequencia.setFkDisciplina(idDisciplina.intValue());

        // CORREÇÃO AQUI:
        if (dataStr != null && !dataStr.isEmpty()) {
            try {
                // Tenta formato ISO (yyyy-MM-dd) primeiro, se falhar tenta BR
                if (dataStr.contains("-")) {
                    frequencia.setDataAula(LocalDate.parse(dataStr.substring(0, 10)));
                } else {
                    // Assume formato dd/MM/yyyy
                    // Se houver hora junto (ex: 01/01/2023 00:00), pega só os 10 primeiros
                    String dataLimpa = dataStr.length() >= 10 ? dataStr.substring(0, 10) : dataStr;
                    frequencia.setDataAula(LocalDate.parse(dataLimpa, formatter));
                }
            } catch (DateTimeParseException e) {
                logger.error("Erro de parse de data: '{}'. Ignorando linha.", dataStr);
                return false;
            }
        } else {
            return false;
        }

        frequencia.setPresente(presenteDouble != null && presenteDouble == 1.0);

        buffer.add(frequencia);

        if (buffer.size() >= TAMANHO_LOTE) {
            flush();
        }
        return true;
    }

    public void flush() {
        if (!buffer.isEmpty()) {
            frequenciaDao.saveAll(buffer);
            buffer.clear();
        }
    }
}