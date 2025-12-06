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

        // --- DEBUG: IMPRIME AS 5 PRIMEIRAS LINHAS NO CONSOLE ---
        if (row.getRowNum() <= 5) {
            System.out.println("--- DEBUG LINHA " + row.getRowNum() + " ---");
            System.out.println("Matricula Raw: " + idMatricula);
            System.out.println("Disciplina Raw: " + idDisciplina);
            System.out.println("Data Raw: '" + dataStr + "'");
            System.out.println("Presente Raw: " + presenteDouble);
            System.out.println("----------------------------------");
        }
        // -------------------------------------------------------

        if (idMatricula == null || idDisciplina == null) {
            if (row.getRowNum() <= 5) System.out.println("❌ PULANDO: IDs Nulos");
            return false;
        }

        Frequencia frequencia = new Frequencia();
        frequencia.setFkMatricula(idMatricula.intValue());
        frequencia.setFkDisciplina(idDisciplina.intValue());

        if (dataStr != null && !dataStr.trim().isEmpty()) {
            try {
                String dataLimpa = dataStr.trim();
                if (dataLimpa.contains("-")) {
                    if (dataLimpa.length() >= 10) {
                        frequencia.setDataAula(LocalDate.parse(dataLimpa.substring(0, 10)));
                    } else {
                        if (row.getRowNum() <= 5) System.out.println("❌ ERRO DATA ISO CURTA: " + dataLimpa);
                        return false;
                    }
                } else if (dataLimpa.contains("/")) {
                    if (dataLimpa.length() >= 10) {
                        frequencia.setDataAula(LocalDate.parse(dataLimpa.substring(0, 10), formatter));
                    } else {
                        // Tenta parse direto (ex: 1/8/2024)
                        frequencia.setDataAula(LocalDate.parse(dataLimpa, formatter));
                    }
                } else {
                    if (row.getRowNum() <= 5) System.out.println("❌ FORMATO DESCONHECIDO: " + dataLimpa);
                    return false;
                }
            } catch (DateTimeParseException e) {
                // LOG DE ERRO CRÍTICO
                System.err.println("❌ ERRO PARSE DATA NA LINHA " + row.getRowNum() + ": " + dataStr);
                e.printStackTrace(); // Mostra o erro exato no log
                return false;
            }
        } else {
            if (row.getRowNum() <= 5) System.out.println("❌ DATA VAZIA");
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