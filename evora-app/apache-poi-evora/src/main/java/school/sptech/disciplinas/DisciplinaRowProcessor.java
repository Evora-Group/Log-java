package school.sptech.disciplinas;

import org.apache.poi.ss.usermodel.Row;
import school.sptech.*;
import java.util.ArrayList;
import java.util.List;

public class DisciplinaRowProcessor extends RowProcessor {

    private static final int COL_ID = 0;
    private static final int COL_FK_INSTITUICAO = 1;
    private static final int COL_NOME = 2;

    private final DisciplinaDao dao;
    private final List<Disciplina> buffer = new ArrayList<>();

    public DisciplinaRowProcessor(DisciplinaDao dao) { this.dao = dao; }

    @Override
    public boolean processAndSave(Row row) {
        Double id = getSafeDouble(row, COL_ID);
        Double fkInst = getSafeDouble(row, COL_FK_INSTITUICAO);
        String nome = getSafeString(row, COL_NOME);

        if (id == null || fkInst == null || nome == null) return false;

        Disciplina d = new Disciplina();
        d.setIdDisciplina(id.intValue());
        d.setFkInstituicao(fkInst.intValue());
        d.setNome(nome);

        buffer.add(d);
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