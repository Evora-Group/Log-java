package school.sptech;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public class InstituicaoRowProcessor implements RowProcessor {

    // Constantes para as colunas do SEU arquivo de Instituição
    private static final int COL_ANO = 0;
    private static final int COL_UF = 1;
    private static final int COL_ID_MUNICIPIO = 2;
    private static final int COL_ID_INSTITUICAO = 7;
    private static final int COL_NOME_INSTITUICAO = 8;

    private final InstituicaoDao instituicaoDao;
    private final int anoFiltro;
    private final String ufFiltro;

    public InstituicaoRowProcessor(InstituicaoDao instituicaoDao, int anoFiltro, String ufFiltro) {
        this.instituicaoDao = instituicaoDao;
        this.anoFiltro = anoFiltro;
        this.ufFiltro = ufFiltro;
    }

    @Override
    public boolean processAndSave(Row row) throws Exception {
        // Validação: checa se a célula principal está vazia
        if (row.getCell(COL_NOME_INSTITUICAO) == null || row.getCell(COL_ANO) == null) {
            return false; // Ignora linha
        }

        double ano = row.getCell(COL_ANO).getNumericCellValue();
        String uf = row.getCell(COL_UF).getStringCellValue();

        // Aplica o filtro
        if (ano == anoFiltro && ufFiltro.equalsIgnoreCase(uf)) {
            Instituicao instituicao = new Instituicao();
            instituicao.setUf(uf.toUpperCase());
            instituicao.setIdMunicipio((int) row.getCell(COL_ID_MUNICIPIO).getNumericCellValue());
            instituicao.setIdInstituicao((int) row.getCell(COL_ID_INSTITUICAO).getNumericCellValue());

            // Boa prática: Força a leitura do nome como String
            Cell nomeCell = row.getCell(COL_NOME_INSTITUICAO);
            nomeCell.setCellType(CellType.STRING);
            instituicao.setNome(nomeCell.getStringCellValue().toUpperCase());

            instituicaoDao.save(instituicao);
            return true; // Retorna true (salvou)
        }

        return false; // Retorna false (filtrado)
    }
}