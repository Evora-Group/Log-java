package school.sptech;

import org.apache.poi.ss.usermodel.Row;

import java.text.DecimalFormat;

public class CursoRowProcessor extends RowProcessor {


    // Constantes de coluna (verifique se estão corretas)
    private static final int COL_ANO = 0;
    private static final int COL_ID_INSTITUICAO = 6;
    private static final int COL_MODALIDADE = 4;
    private static final int COL_ID_CURSO = 8;
    private static final int COL_DESCRICAO = 7;

    private final CursosDao cursosDao;
    private final InstituicaoDao instituicaoDao;

    public CursoRowProcessor(CursosDao cursosDao, InstituicaoDao instituicaoDao) {
        this.cursosDao = cursosDao;
        this.instituicaoDao = instituicaoDao;
    }

    @Override
    public boolean processAndSave(Row row) throws Exception {

        Double ano = getSafeDouble(row, COL_ANO);
        String modalidade = getSafeString(row, COL_MODALIDADE);
        String descricao = getSafeString(row, COL_DESCRICAO);
        Double idCurso = getSafeDouble(row, COL_ID_CURSO);
        Double idInstituicao = getSafeDouble(row, COL_ID_INSTITUICAO);

        if (ano == null || modalidade == null || descricao == null || idCurso == null || idInstituicao == null) {
            return false; // Ignora a linha por ter dados faltando
        }

        if (!instituicaoDao.existsById(idInstituicao.intValue())){
            return false; // Ignora a linha se o idInstituicao não existir no banco
        }



        Curso curso = new Curso();
        curso.setModalidade(modalidade.toUpperCase());
        curso.setDescricao(descricao.toUpperCase());
        curso.setIdCurso(idCurso.intValue());
        curso.setIdInstituicao(idInstituicao.intValue());

        cursosDao.save(curso);

        return true;

    }



}