package school.sptech;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class LeituraExcel {

    public LeituraExcel() {

    }

    public List<Instituicao> lerInstituicoes(String caminhoArquivo) {
        List<Instituicao> instituicoes = new ArrayList<>();
        try (FileInputStream leitura = new FileInputStream(caminhoArquivo);
             Workbook planinhas = new XSSFWorkbook(leitura)) {

            Sheet planinha = planinhas.getSheetAt(0);

            for (int i = 1; i <= planinha.getLastRowNum(); i++) {
                Row linha = planinha.getRow(i);
                if (linha == null) continue;
                if (((linha.getCell(0).getNumericCellValue()) == 2023) && (linha.getCell(1).getStringCellValue().equalsIgnoreCase("SP"))){

                    Cell ufCell = linha.getCell(1);
                    Cell idMunicipioCell = linha.getCell(2);
                    Cell idIesCell = linha.getCell(7);
                    Cell nomeCell = linha.getCell(8);

                    if (ufCell == null || idMunicipioCell == null || idIesCell == null) {
                        continue;
                    }
                    // ------------------------------------

                    Instituicao instituicao = new Instituicao();
                    instituicao.setNome(nomeCell.getStringCellValue().toUpperCase());
                    instituicao.setIdInstituicao((int) idIesCell.getNumericCellValue());
                    instituicao.setIdMunicipio((int) idMunicipioCell.getNumericCellValue());
                    instituicao.setUf(ufCell.getStringCellValue().toUpperCase());

                    instituicoes.add(instituicao);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return instituicoes;
    }




}
