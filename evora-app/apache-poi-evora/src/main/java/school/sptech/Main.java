package school.sptech;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Main {

  public static void main(String[] args) throws InterruptedException, URISyntaxException {

      ConexaoBanco conexaoBanco = new ConexaoBanco();
      LeituraExcel leituraExcel = new LeituraExcel();
      InstituicaoDao instituicaoDao = new InstituicaoDao(conexaoBanco.getJdbcTemplate());
      CursosDao cursoDao = new CursosDao(conexaoBanco.getJdbcTemplate());

      Region region = Region.SA_EAST_1;
      String s3Path = System.getenv("S3_FILE_KEY");


      LeituraS3 leitorS3 = new LeituraS3(region);

      ResponseInputStream<GetObjectResponse> s3ObjectStream = leitorS3.obterInputStream(s3Path);



      List<Instituicao> instituicoes = leituraExcel.lerInstituicoes(s3ObjectStream);

      for (Instituicao instituicao : instituicoes){

          instituicaoDao.save(instituicao);

//          for (Curso curso : instituicao.getCursos()){
//              cursoDao.save(curso);
//          }


//          System.out.println(instituicao);

      }


  }
}