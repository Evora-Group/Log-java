package school.sptech.alunos;

import org.apache.poi.ss.usermodel.Row;
import school.sptech.RowProcessor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlunoRowProcessor extends RowProcessor {

    // Índices baseados no arquivo base_alunos_1500.xlsx
    private static final int COL_RA = 0;
    private static final int COL_NOME = 1;
    private static final int COL_EMAIL = 2;


    private final AlunoDao alunoDao;
    private final List<Aluno> buffer = new ArrayList<>();
    private static final int TAMANHO_LOTE = 1000;

    public AlunoRowProcessor(AlunoDao alunoDao) {
        this.alunoDao = alunoDao;
    }

    @Override
    public boolean processAndSave(Row row) throws Exception {
        Double raDouble = getSafeDouble(row, COL_RA);
        String nome = getSafeString(row, COL_NOME);
        String email = getSafeString(row, COL_EMAIL);

        // Validação Mínima (RA e Nome são obrigatórios)
        if (raDouble == null || nome == null) {
            // Debug para entender se algo for ignorado
            System.out.println("❌ Aluno Ignorado (Dados nulos) Linha " + row.getRowNum() +
                    " | RA: " + raDouble + " | Nome: " + nome);
            return false;
        }

        Aluno aluno = new Aluno();
        aluno.setRa(raDouble.intValue());
        aluno.setNome(nome);
        aluno.setEmail(email);


        buffer.add(aluno);

        if (buffer.size() >= TAMANHO_LOTE) {
            flush();
        }
        return true;
    }

    public void flush() {
        if (!buffer.isEmpty()) {
            System.out.println("💾 Salvando lote de " + buffer.size() + " alunos...");
            alunoDao.saveAll(buffer);
            buffer.clear();
        }
    }
}