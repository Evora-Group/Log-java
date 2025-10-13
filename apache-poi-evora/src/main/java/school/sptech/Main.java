package school.sptech;

import java.util.ArrayList;
import java.util.List;

public class Main {

  public static void main(String[] args) {

      ConexaoBanco conexaoBanco = new ConexaoBanco();
      LeituraExcel leituraExcel = new LeituraExcel();
      InstituicaoDao instituicaoDao = new InstituicaoDao(conexaoBanco.getJdbcTemplate());
      CursosDao cursoDao = new CursosDao(conexaoBanco.getJdbcTemplate());

      List<Instituicao> instituicoes = leituraExcel.lerInstituicoes("br_inep_censo_educacao_superior_ies.xlsx");

      for (Instituicao instituicao : instituicoes){

          instituicaoDao.save(instituicao);

//          for (Curso curso : instituicao.getCursos()){
//              cursoDao.save(curso);
//          }


          System.out.println(instituicao);

      }


  }
}