package school.sptech.instituicoes;
import org.apache.poi.ss.usermodel.Row;
import school.sptech.*; // Ajuste o import do Model
import school.sptech.RowProcessor;

import java.util.ArrayList;
import java.util.List;

public class InstituicaoRowProcessor extends RowProcessor {

    // Constantes de coluna
    private static final int COL_ANO = 0;
    private static final int COL_UF = 1;
    private static final int COL_ID_MUNICIPIO = 2;
    private static final int COL_ID_INSTITUICAO = 7;
    private static final int COL_NOME_INSTITUICAO = 8;

    private final InstituicaoDao instituicaoDao;
    private final int anoFiltro;
    private final String ufFiltro;

    // Buffer para otimização em lote (Batch Insert)
    private final List<Instituicao> buffer = new ArrayList<>();
    private static final int TAMANHO_LOTE = 1000;

    public InstituicaoRowProcessor(InstituicaoDao instituicaoDao, int anoFiltro, String ufFiltro) {
        this.instituicaoDao = instituicaoDao;
        this.anoFiltro = anoFiltro;
        this.ufFiltro = ufFiltro;
    }

    @Override
    public boolean processAndSave(Row row) throws Exception {

        Double ano = super.getSafeDouble(row, COL_ANO);
        String uf = super.getSafeString(row, COL_UF);
        String nome = super.getSafeString(row, COL_NOME_INSTITUICAO);
        Double idMunicipio = super.getSafeDouble(row, COL_ID_MUNICIPIO);
        Double idInstituicao = super.getSafeDouble(row, COL_ID_INSTITUICAO);

        if (ano == null || uf == null || nome == null || idMunicipio == null || idInstituicao == null) {
            return false; // Ignora linha com dados faltando
        }

        // Aplica o filtro (Ex: Ano 2023 e UF SP)
        if (ano.intValue() == anoFiltro && ufFiltro.equalsIgnoreCase(uf)) {

            Instituicao instituicao = new Instituicao();
            instituicao.setUf(uf.toUpperCase());
            instituicao.setNome(nome.toUpperCase());
            instituicao.setIdMunicipio(idMunicipio.intValue());
            instituicao.setIdInstituicao(idInstituicao.intValue());

            // --- LÓGICA DE BATCH ---
            buffer.add(instituicao);

            // Se encheu o caminhão (1000 registros), despacha pro banco
            if (buffer.size() >= TAMANHO_LOTE) {
                flush();
            }

            return true; // Contabiliza como lido/processado
        }

        return false; // Ignorado pelo filtro de Ano/UF
    }

    /**
     * Salva o que sobrou no buffer e limpa a lista.
     * Chamado automaticamente pelo processAndSave ou manualmente pelo Main no final.
     */
    public void flush() {
        if (!buffer.isEmpty()) {
            // Requer que seu InstituicaoDao tenha o método saveAll(List<Instituicao>)
            // Se não tiver, use um loop simples aqui: for(Instituicao i : buffer) dao.save(i);
            instituicaoDao.saveAll(buffer);
            buffer.clear();
        }
    }
}