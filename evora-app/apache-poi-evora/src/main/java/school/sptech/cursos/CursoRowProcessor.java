package school.sptech;

import org.apache.poi.ss.usermodel.Row;
import java.util.ArrayList;
import java.util.List;

public class CursoRowProcessor extends RowProcessor {

    private static final int COL_ANO = 0;
    private static final int COL_ID_INSTITUICAO = 6;
    private static final int COL_MODALIDADE = 4;
    private static final int COL_ID_CURSO = 8;
    private static final int COL_NOME = 7;

    private final CursosDao cursosDao;
    private final InstituicaoDao instituicaoDao;

    // Buffer para armazenar os cursos antes de salvar (Lote)
    private final List<Curso> buffer = new ArrayList<>();
    private static final int TAMANHO_LOTE = 1000; // Salva a cada 1000 registros

    public CursoRowProcessor(CursosDao cursosDao, InstituicaoDao instituicaoDao) {
        this.cursosDao = cursosDao;
        this.instituicaoDao = instituicaoDao;
    }

    @Override
    public boolean processAndSave(Row row) throws Exception {
        // 1. Extração dos dados
        Double ano = getSafeDouble(row, COL_ANO);
        String modalidade = getSafeString(row, COL_MODALIDADE);
        String nome = getSafeString(row, COL_NOME);
        Double idCurso = getSafeDouble(row, COL_ID_CURSO);
        Double idInstituicao = getSafeDouble(row, COL_ID_INSTITUICAO);

        // 2. Validações básicas
        if (ano == null || modalidade == null || nome == null || idCurso == null || idInstituicao == null) {
            return false;
        }

        // Validação de FK (Nota: Isso ainda faz um SELECT por linha.
        // Para otimização extrema, cachear os IDs das instituições num Set no construtor)
        if (!instituicaoDao.existsById(idInstituicao.intValue())){
            return false;
        }

        // 3. Criação do Objeto
        Curso curso = new Curso();
        curso.setModalidade(modalidade.toUpperCase());
        curso.setNome(nome.toUpperCase());
        curso.setIdCurso(idCurso.intValue());
        curso.setIdInstituicao(idInstituicao.intValue());

        // 4. LÓGICA DE BATCH (LOTE)
        buffer.add(curso);

        // Se a caixa encheu, salva tudo e limpa
        if (buffer.size() >= TAMANHO_LOTE) {
            cursosDao.saveAll(buffer);
            buffer.clear(); // Esvazia a caixa para os próximos
        }

        return true;
    }

    /**
     * Método auxiliar para salvar o que sobrou no buffer ao final do arquivo.
     * Ex: Se tiver 1050 registros, salva 1000 no loop e sobram 50 aqui.
     */
    public void flush() {
        if (!buffer.isEmpty()) {
            cursosDao.saveAll(buffer);
            buffer.clear();
        }
    }
}