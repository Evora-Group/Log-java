package school.sptech.turmas;

import org.apache.poi.ss.usermodel.Row;

import school.sptech.*;
import java.util.ArrayList;
import java.util.List;

public class TurmaRowProcessor extends RowProcessor {
    private static final int COL_ID = 0;
    private static final int COL_FK_CURSO = 1;
    private static final int COL_SIGLA = 2;
    private static final int COL_ANO = 3;
    private static final int COL_SEMESTRE = 4;

    private final TurmaDao dao;
    private final List<Turma> buffer = new ArrayList<>();

    public TurmaRowProcessor(TurmaDao dao) { this.dao = dao; }

    @Override
    public boolean processAndSave(Row row) {
        Double id = getSafeDouble(row, COL_ID);
        Double fkCurso = getSafeDouble(row, COL_FK_CURSO);
        String sigla = getSafeString(row, COL_SIGLA);
        Double ano = getSafeDouble(row, COL_ANO);
        Double semestre = getSafeDouble(row, COL_SEMESTRE);

        if (id == null || fkCurso == null) return false;

        Turma t = new Turma();
        t.setIdTurma(id.intValue());
        t.setFkCurso(fkCurso.intValue());
        t.setNomeSigla(sigla);
        t.setAno(ano != null ? ano.intValue() : 2025);
        t.setSemestre(semestre != null ? semestre.intValue() : 1);

        buffer.add(t);
        if (buffer.size() >= 1000) flush();
        return true;
    }
    public void flush() {
        if (!buffer.isEmpty()) { dao.saveAll(buffer); buffer.clear(); }
    }
}