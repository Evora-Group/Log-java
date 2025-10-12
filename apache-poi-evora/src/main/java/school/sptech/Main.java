package school.sptech;

import java.util.ArrayList;
import java.util.List;

public class Main {

  public static void main(String[] args) {

      ConexaoBanco conexaoBanco = new ConexaoBanco();
      LeituraExcel leituraExcel = new LeituraExcel();
      InstituicaoDao instituicaoDao = new InstituicaoDao(conexaoBanco.getJdbcTemplate());

      List<Instituicao> instituicoes = leituraExcel.lerInstituicoes("br_inep_censo_educacao_superior_ies.xlsx");

      for (Instituicao instituicao : instituicoes){

          instituicaoDao.save(instituicao);

          System.out.println(instituicao);

      }


  }
}