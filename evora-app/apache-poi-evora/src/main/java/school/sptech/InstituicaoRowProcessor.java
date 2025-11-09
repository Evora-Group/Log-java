package school.sptech;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

// Importamos isso para converter números (2023.0) para texto ("2023")
import java.text.DecimalFormat;

public class InstituicaoRowProcessor implements RowProcessor {

    // Constantes de coluna (verifique se estão corretas)
    private static final int COL_ANO = 0;
    private static final int COL_UF = 1;
    private static final int COL_ID_MUNICIPIO = 2;
    private static final int COL_ID_INSTITUICAO = 7;
    private static final int COL_NOME_INSTITUICAO = 8;

    private final InstituicaoDao instituicaoDao;
    private final int anoFiltro;
    private final String ufFiltro;

    // Um formatador para converter números (como '2023.0') para strings ("2023")
    private final DecimalFormat numberFormatter = new DecimalFormat("0");

    public InstituicaoRowProcessor(InstituicaoDao instituicaoDao, int anoFiltro, String ufFiltro) {
        this.instituicaoDao = instituicaoDao;
        this.anoFiltro = anoFiltro;
        this.ufFiltro = ufFiltro;
    }

    @Override
    public boolean processAndSave(Row row) throws Exception {

        // --- ETAPA 1: Ler todos os valores de forma segura (sem setCellType) ---
        Double ano = getSafeDouble(row, COL_ANO);
        String uf = getSafeString(row, COL_UF);
        String nome = getSafeString(row, COL_NOME_INSTITUICAO);
        Double idMunicipio = getSafeDouble(row, COL_ID_MUNICIPIO);
        Double idInstituicao = getSafeDouble(row, COL_ID_INSTITUICAO);

        // --- ETAPA 2: Validar dados essenciais ---
        if (ano == null || uf == null || nome == null || idMunicipio == null || idInstituicao == null) {
            return false; // Ignora a linha por ter dados faltando
        }

        // --- ETAPA 3: Aplicar o filtro ---
        if (ano.intValue() == anoFiltro && ufFiltro.equalsIgnoreCase(uf)) {

            Instituicao instituicao = new Instituicao();
            instituicao.setUf(uf.toUpperCase());
            instituicao.setNome(nome.toUpperCase());
            instituicao.setIdMunicipio(idMunicipio.intValue());
            instituicao.setIdInstituicao(idInstituicao.intValue());

            instituicaoDao.save(instituicao);
            return true; // Sucesso!
        }

        return false; // Ignorado pelo filtro
    }

    // --- MÉTODOS AUXILIARES "DEFENSIVOS" (SEM setCellType) ---

    /**
     * Lê uma célula como String, checando seu tipo original.
     */
    private String getSafeString(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.STRING) {
            String value = cell.getStringCellValue();
            return value.trim().isEmpty() ? null : value.trim();
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            // Converte o número para uma String (ex: 2023.0 -> "2023")
            return numberFormatter.format(cell.getNumericCellValue());
        }

        return null; // Ignora outros tipos (boolean, formula, etc.)
    }

    /**
     * Lê uma célula como Double, checando seu tipo original.
     */
    private Double getSafeDouble(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        }

        if (cell.getCellType() == CellType.STRING) {
            // Tenta converter uma String (ex: "2023") para número
            try {
                return Double.parseDouble(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                return null; // O texto não era um número
            }
        }

        return null; // Ignora outros tipos
    }
}