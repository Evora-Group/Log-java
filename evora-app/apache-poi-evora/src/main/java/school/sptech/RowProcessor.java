package school.sptech;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.text.DecimalFormat;


/**
 * Interface (Contrato) para uma estratégia de processamento de linha.
 * Qualquer classe que implementar isso saberá como processar e salvar
 * uma linha específica de um arquivo Excel.
 */
public abstract class RowProcessor {


    // Um formatador para converter números (como '2023.0') para strings ("2023")
    private final DecimalFormat numberFormatter = new DecimalFormat("0");


    /**
     * Processa uma única linha (Row) lida do Excel e a salva no banco.
     *
     * @param row O objeto Row do Apache POI.
     * @return true se um registro foi salvo, false se foi filtrado/ignorado.
     * @throws Exception se ocorrer um erro de parsing ou banco.
     */
    public abstract boolean processAndSave(Row row) throws Exception;

    /**
     * Lê uma célula como String, checando seu tipo original.
     */
    public String getSafeString(Row row, int index) {
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
    public Double getSafeDouble(Row row, int index) {
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