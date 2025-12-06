package school.sptech.frequencias;

import org.apache.poi.ss.usermodel.DateUtil; // <--- IMPORTANTE
import org.apache.poi.ss.usermodel.Row;
import school.sptech.RowProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FrequenciaRowProcessor extends RowProcessor {

    private static final Logger logger = LoggerFactory.getLogger(FrequenciaRowProcessor.class);

    private static final int COL_ID_MATRICULA = 0;
    private static final int COL_ID_DISCIPLINA = 1;
    private static final int COL_DATA = 2;
    private static final int COL_PRESENTE = 3;

    private final FrequenciaDao frequenciaDao;
    private final List<Frequencia> buffer = new ArrayList<>();
    private static final int TAMANHO_LOTE = 1000;

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
            return false;
        }

        Frequencia frequencia = new Frequencia();
        frequencia.setFkMatricula(idMatricula.intValue());
        frequencia.setFkDisciplina(idDisciplina.intValue());

        if (dataStr != null && !dataStr.trim().isEmpty()) {
            String dataLimpa = dataStr.trim();
            try {
                // 1. Tenta formato ISO (yyyy-MM-dd)
                if (dataLimpa.contains("-")) {
                    if (dataLimpa.length() >= 10) {
                        frequencia.setDataAula(LocalDate.parse(dataLimpa.substring(0, 10)));
                    } else {
                        return false;
                    }
                }
                // 2. Tenta formato BR (dd/MM/yyyy)
                else if (dataLimpa.contains("/")) {
                    if (dataLimpa.length() >= 10) {
                        frequencia.setDataAula(LocalDate.parse(dataLimpa.substring(0, 10), formatter));
                    } else {
                        frequencia.setDataAula(LocalDate.parse(dataLimpa, formatter));
                    }
                }
                // 3. Tenta formato SERIAL DO EXCEL (ex: 45998)
                else if (dataLimpa.matches("-?\\d+(\\.\\d+)?")) {
                    // É um número! Vamos converter usando o Apache POI
                    double valorNumerico = Double.parseDouble(dataLimpa);

                    // Converte Double do Excel para java.util.Date
                    Date javaDate = DateUtil.getJavaDate(valorNumerico);

                    if (javaDate != null) {
                        // Converte java.util.Date para LocalDate
                        frequencia.setDataAula(javaDate.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate());
                    } else {
                        return false;
                    }
                }
                else {
                    // Formato realmente desconhecido
                    return false;
                }
            } catch (Exception e) {
                // Se der qualquer erro de conversão, loga e pula
                logger.error("Erro ao converter data na linha {}: '{}'", row.getRowNum(), dataStr);
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