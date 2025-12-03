package school.sptech.matriculas;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import school.sptech.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MatriculaRowProcessor extends RowProcessor {
    private static final int COL_ID = 0;
    private static final int COL_FK_ALUNO = 1; // RA
    private static final int COL_FK_TURMA = 2;


    private final MatriculaDao dao;
    private final List<Matricula> buffer = new ArrayList<>();

    public MatriculaRowProcessor(MatriculaDao dao) { this.dao = dao; }

    @Override
    public boolean processAndSave(Row row) {
        Double id = getSafeDouble(row, COL_ID);
        Double fkAluno = getSafeDouble(row, COL_FK_ALUNO);
        Double fkTurma = getSafeDouble(row, COL_FK_TURMA);
;

        if (id == null || fkAluno == null || fkTurma == null) return false;

        Matricula m = new Matricula();
        m.setIdMatricula(id.intValue());
        m.setFkAluno(fkAluno.intValue());
        m.setFkTurma(fkTurma.intValue());
        m.setAtivo(true);
        m.setDataMatricula(LocalDate.now());

        buffer.add(m);
        if (buffer.size() >= 1000) flush();
        return true;
    }
    public void flush() {
        if (!buffer.isEmpty()) { dao.saveAll(buffer); buffer.clear(); }
    }
}