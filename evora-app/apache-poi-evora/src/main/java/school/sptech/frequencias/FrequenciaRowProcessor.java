package school.sptech;

import org.apache.poi.ss.usermodel.Row;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FrequenciaRowProcessor extends RowProcessor {

    // AJUSTE OS ÍNDICES
    private static final int COL_ID_MATRICULA = 0;
    private static final int COL_ID_DISCIPLINA = 1;
    private static final int COL_DATA = 2;
    private static final int COL_PRESENTE = 3; // 1 ou 0
    private static final int COL_JUSTIFICATIVA = 4;

    private final FrequenciaDao frequenciaDao;
    private final List<Frequencia> buffer = new ArrayList<>();
    private static final int TAMANHO_LOTE = 1000;

    public FrequenciaRowProcessor(FrequenciaDao frequenciaDao) {
        this.frequenciaDao = frequenciaDao;
    }

    @Override
    public boolean processAndSave(Row row) throws Exception {
        Double idMatricula = getSafeDouble(row, COL_ID_MATRICULA);
        Double idDisciplina = getSafeDouble(row, COL_ID_DISCIPLINA);
        String dataStr = getSafeString(row, COL_DATA);
        Double presenteDouble = getSafeDouble(row, COL_PRESENTE);
        String justificativa = getSafeString(row, COL_JUSTIFICATIVA);

        if (idMatricula == null || idDisciplina == null) {
            return false;
        }

        Frequencia frequencia = new Frequencia();
        frequencia.setFkMatricula(idMatricula.intValue());
        frequencia.setFkDisciplina(idDisciplina.intValue());

        // Tratamento da Data
        if (dataStr != null && dataStr.length() >= 10) {
            frequencia.setDataAula(LocalDate.parse(dataStr.substring(0, 10)));
        } else {
            return false; // Sem data não tem frequência
        }

        // 1 = Presente, 0 = Falta
        frequencia.setPresente(presenteDouble != null && presenteDouble == 1.0);
        frequencia.setJustificativa(justificativa);

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