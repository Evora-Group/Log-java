package school.sptech;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AvaliacaoRowProcessor extends RowProcessor {

    // VERIFIQUE SE ESTES ÍNDICES BATEM COM SEU EXCEL (A=0, B=1, etc)
    private static final int COL_ID_MATRICULA = 0;
    private static final int COL_ID_DISCIPLINA = 1;
    private static final int COL_TIPO = 2;
    private static final int COL_NOTA = 3;
    private static final int COL_DATA = 4;

    private final AvaliacaoDao avaliacaoDao;
    private final List<Avaliacao> buffer = new ArrayList<>();
    private static final int TAMANHO_LOTE = 1000;

    public AvaliacaoRowProcessor(AvaliacaoDao avaliacaoDao) {
        this.avaliacaoDao = avaliacaoDao;
    }

    @Override
    public boolean processAndSave(Row row) throws Exception {
        Double idMatricula = getSafeDouble(row, COL_ID_MATRICULA);
        Double idDisciplina = getSafeDouble(row, COL_ID_DISCIPLINA);
        String tipo = getSafeString(row, COL_TIPO);
        Double notaDouble = getSafeDouble(row, COL_NOTA);

        // Lógica Robusta de Data
        LocalDate dataFinal = LocalDate.now();
        try {
            Cell cellData = row.getCell(COL_DATA);
            if (cellData != null) {
                if (cellData.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cellData)) {
                    dataFinal = cellData.getLocalDateTimeCellValue().toLocalDate();
                } else {
                    String temp = getSafeString(row, COL_DATA);
                    if (temp != null && temp.length() >= 10) {
                        dataFinal = LocalDate.parse(temp.substring(0, 10));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Erro Data Linha " + row.getRowNum());
        }

        // DEBUG: Se algo estiver nulo, avisa no console
        if (idMatricula == null || idDisciplina == null || notaDouble == null) {
            System.out.println("❌ Ignorado (Dados nulos) Linha " + row.getRowNum() +
                    " | Matr: " + idMatricula +
                    " | Disc: " + idDisciplina +
                    " | Nota: " + notaDouble);
            return false;
        }

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setFkMatricula(idMatricula.intValue());
        avaliacao.setFkDisciplina(idDisciplina.intValue());
        avaliacao.setTipo(tipo);
        avaliacao.setNota(BigDecimal.valueOf(notaDouble));
        avaliacao.setDataAvaliacao(dataFinal);

        buffer.add(avaliacao);

        if (buffer.size() >= TAMANHO_LOTE) {
            flush();
        }
        return true;
    }

    public void flush() {
        if (!buffer.isEmpty()) {
            System.out.println("💾 Salvando lote de " + buffer.size() + " avaliações...");
            avaliacaoDao.saveAll(buffer);
            buffer.clear();
        }
    }
}