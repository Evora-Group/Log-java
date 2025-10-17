package school.sptech;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;



public class LeituraExcel {

    private static final Logger logger = LoggerFactory.getLogger(LeituraExcel.class);
    private static final Logger loggerDao = LoggerFactory.getLogger(InstituicaoDao.class);
    ConexaoBanco conexaoBanco = new ConexaoBanco();
    InstituicaoDao instituicaoDao = new InstituicaoDao(conexaoBanco.getJdbcTemplate());
    public LeituraExcel() throws InterruptedException {

    }

    public void lerInstituicoes(InputStream caminhoArquivoInstituicao){
        logger.info("Iniciando a leitura do excel: " + caminhoArquivoInstituicao);

        Thread logThread = new Thread(() -> {
            try {
                int contador = 0;
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(5000);
                    contador += 5;
                    logger.info("Carregando arquivo Excel... (" + contador + "s)");
                }
            } catch (InterruptedException e) {
                // Thread interrompida: fim do log periódico
            }
        });
        logThread.start();

        try (Workbook planinhas = new XSSFWorkbook(caminhoArquivoInstituicao)) {
             // operação lenta

            logThread.interrupt(); // para o log de progresso

            Sheet planinha = planinhas.getSheetAt(0);
            logger.info("Selecionando a primeira planilha das intituições");

            for (int i = 1; i <= planinha.getLastRowNum(); i++) {
                if (i == 1) {
                    logger.info("Iniciando a varredura das linhas");
                }

                Row linha = planinha.getRow(i);
                if (linha == null) continue;

                if (linha.getCell(0).getNumericCellValue() == 2023 &&
                        linha.getCell(1).getStringCellValue().equalsIgnoreCase("SP")) {

                    Instituicao instituicao = new Instituicao();
                    instituicao.setUf(linha.getCell(1).getStringCellValue().toUpperCase());
                    instituicao.setIdMunicipio((int) linha.getCell(2).getNumericCellValue());
                    instituicao.setIdInstituicao((int) linha.getCell(7).getNumericCellValue());
                    instituicao.setNome(linha.getCell(8).getStringCellValue().toUpperCase());

                    instituicaoDao.save(instituicao);


                }
            }

            logger.info("Finalizada a varredura das linhas");
            logger.info("Carga finalizada");

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            logThread.interrupt(); // garante interrupção mesmo em erro
        }

//        return instituicoes;
    }

//    public List<Curso> lerCursos(String caminhoArquivo, Integer idIes) {
//        List<Curso> cursos = new ArrayList<>();
//        try (FileInputStream leitura = new FileInputStream(caminhoArquivo);
//             Workbook planinhas = new XSSFWorkbook(leitura)) {
//
//            Sheet planinha = planinhas.getSheetAt(0);
//
//            for (int i = 1; i <= planinha.getLastRowNum(); i++) {
//
//                Row linha = planinha.getRow(i);
//                if (linha == null) continue;
//                if (linha.getCell(6).getNumericCellValue() == idIes){
//
//                    Cell modalidadeCell = linha.getCell(4);
//                    Cell idCurso = linha.getCell(8);
//                    Cell idInstituicaoCell = linha.getCell(6);
//                    Cell nomeCell = linha.getCell(7);
//
//                    if (idCurso == null || modalidadeCell == null || idInstituicaoCell == null || nomeCell == null) {
//                        continue;
//                    }
//
//                    Curso curso = new Curso();
//
//                    curso.setDescricao(nomeCell.getStringCellValue().toUpperCase());
//                    curso.setIdInstituicao((int) idInstituicaoCell.getNumericCellValue());
//                    curso.setIdCurso((int) idCurso.getNumericCellValue());
//                    curso.setModalidade(modalidadeCell.getStringCellValue().toUpperCase());
//
//                    cursos.add(curso);
//
//                }
//
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return cursos;
//    }
//



}
