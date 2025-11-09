package school.sptech;

import org.apache.poi.ss.usermodel.Row;



/**
 * Interface (Contrato) para uma estratégia de processamento de linha.
 * Qualquer classe que implementar isso saberá como processar e salvar
 * uma linha específica de um arquivo Excel.
 */
public interface RowProcessor {

    /**
     * Processa uma única linha (Row) lida do Excel e a salva no banco.
     *
     * @param row O objeto Row do Apache POI.
     * @return true se um registro foi salvo, false se foi filtrado/ignorado.
     * @throws Exception se ocorrer um erro de parsing ou banco.
     */
    boolean processAndSave(Row row) throws Exception;

}