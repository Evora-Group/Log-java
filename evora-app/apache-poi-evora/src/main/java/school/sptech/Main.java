package school.sptech;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        // A classe Main não precisa mais saber sobre os detalhes do S3.
        // A configuração é lida pela classe LeituraS3.

        // Defina a região correta do seu bucket
        Region region = Region.US_EAST_1;

        ConexaoBanco conexaoBanco = new ConexaoBanco();
        LeituraExcel leituraExcel = new LeituraExcel();
        InstituicaoDao instituicaoDao = new InstituicaoDao(conexaoBanco.getJdbcTemplate());
        // CursosDao cursoDao = new CursosDao(conexaoBanco.getJdbcTemplate()); // Descomente se for usar

        System.out.println("Iniciando processo de carga a partir do S3...");

        // Usamos try-with-resources para garantir que o leitor S3 e o stream sejam fechados.
        try (LeituraS3 leitorS3 = new LeituraS3(region);
             ResponseInputStream<GetObjectResponse> s3ObjectStream = leitorS3.obterInputStream()) { // A chamada agora é sem argumentos

            List<Instituicao> instituicoes = leituraExcel.lerInstituicoes(s3ObjectStream);

//            for (Instituicao instituicao : instituicoes) {
//                instituicaoDao.save(instituicao);
//            }

        } catch (Exception e) {
            System.err.println("Ocorreu um erro fatal durante o processo.");
            e.printStackTrace();
        }

        System.out.println("Processo finalizado.");
    }
}