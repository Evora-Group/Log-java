package school.sptech;

import org.apache.poi.xssf.model.SharedStringsTable;

import java.util.List;

public class SheetHandlerInstituicao extends SheetHandler{

    // Constantes para deixar o código legível, em vez de usar "números mágicos".
    // Da nome para as colunas e linhas, inves de usar os números puros
//

    private static final int COL_ANO = 0;
    private static final int COL_UF = 1;
    private static final int COL_ID_MUNICIPIO = 2;
    private static final int COL_ID_INSTITUICAO = 7;
    private static final int COL_NOME_INSTITUICAO = 8;

    private final InstituicaoDao instituicaoDao; // O objeto para salvar no banco.
    private final int anoFiltro; // O ano a ser filtrado.
    private final String ufFiltro; // A UF a ser filtrada.

    public SheetHandlerInstituicao(SharedStringsTable sst, InstituicaoDao instituicaoDao, int anoFiltro, String ufFiltro) {
        super(sst);
        this.instituicaoDao = instituicaoDao;
        this.anoFiltro = anoFiltro;
        this.ufFiltro = ufFiltro;
    }



    /**
     * Método auxiliar que contém a lógica de negócio (o que fazer com os dados da linha).
     * @param linha Uma lista de strings com todos os valores da linha que acabou de ser lida.
     */

    public void processarLinha(List<String> linha) {
        // Verificação de segurança: a linha tem colunas suficientes para evitar erros?
        if (linha.size() <= COL_NOME_INSTITUICAO) {
            return; // Se não, simplesmente ignoramos esta linha.
        }

        try {
            // Extraímos os dados das colunas usando nossas constantes legíveis.
            double ano = Double.parseDouble(linha.get(COL_ANO)); // Excel trata números como double.
            String uf = linha.get(COL_UF);

            // Aplicamos nossa regra de negócio (o filtro).
            if (ano == anoFiltro && ufFiltro.equalsIgnoreCase(uf)) {
                // Se a linha corresponde ao filtro, criamos o objeto Instituicao.
                Instituicao instituicao = new Instituicao();
                instituicao.setUf(uf.toUpperCase());
                instituicao.setIdMunicipio((int) Double.parseDouble(linha.get(COL_ID_MUNICIPIO)));
                instituicao.setIdInstituicao((int) Double.parseDouble(linha.get(COL_ID_INSTITUICAO)));
                instituicao.setNome(linha.get(COL_NOME_INSTITUICAO).toUpperCase());

                // Mandamos o DAO salvar o objeto no banco de dados.
                instituicaoDao.save(instituicao);
                super.setContadorLinhas(getContadorLinhas() + 1);
            }
        } catch (NumberFormatException e) {
            // Se uma célula que deveria ser número tiver um texto, este erro acontece.
            // Nós registramos o aviso e continuamos para a próxima linha, sem quebrar a aplicação.
            logger.warn("Linha {} ignorada devido a erro de formatação de número: {}", super.getContadorLinhas(), e.getMessage());
        } catch (Exception e) {
            // Captura qualquer outro erro inesperado naquela linha.
            logger.error("Erro inesperado ao processar a linha {}. Dados da linha: {}", super.getContadorLinhas(), linha, e);
        }
    }

}
