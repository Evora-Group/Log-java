package school.sptech.grades;

import org.apache.poi.ss.usermodel.Row;
import school.sptech.*;

import java.util.ArrayList;
import java.util.List;

public class GradeCurricularRowProcessor extends RowProcessor {
    private static final int COL_FK_CURSO = 0;
    private static final int COL_FK_DISCIPLINA = 1;

    private final GradeCurricularDao dao;
    private final List<GradeCurricular> buffer = new ArrayList<>();

    public GradeCurricularRowProcessor(GradeCurricularDao dao) { this.dao = dao; }

    @Override
    public boolean processAndSave(Row row) {
        Double fkCurso = getSafeDouble(row, COL_FK_CURSO);
        Double fkDisc = getSafeDouble(row, COL_FK_DISCIPLINA);

        if (fkCurso == null || fkDisc == null) return false;

        GradeCurricular g = new GradeCurricular();
        g.setFkCurso(fkCurso.intValue());
        g.setFkDisciplina(fkDisc.intValue());

        buffer.add(g);
        if (buffer.size() >= 1000) flush();
        return true;
    }

    public void flush() {
        if (!buffer.isEmpty()) {
            dao.saveAll(buffer);
            buffer.clear();
        }
    }
}
