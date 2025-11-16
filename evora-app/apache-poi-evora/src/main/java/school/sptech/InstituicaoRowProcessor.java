package school.sptech;

import org.apache.poi.ss.usermodel.Row;

public class InstituicaoRowProcessor extends RowProcessor {

    // Constantes de coluna (verifique se estão corretas)
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

        Double ano = getSafeDouble(row, COL_ANO);
        String uf = getSafeString(row, COL_UF);
        String nome = getSafeString(row, COL_NOME_INSTITUICAO);
        Double idMunicipio = getSafeDouble(row, COL_ID_MUNICIPIO);
        Double idInstituicao = getSafeDouble(row, COL_ID_INSTITUICAO);

        if (ano == null || uf == null || nome == null || idMunicipio == null || idInstituicao == null) {
            return false; // Ignora a linha por ter dados faltando
        }

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

}