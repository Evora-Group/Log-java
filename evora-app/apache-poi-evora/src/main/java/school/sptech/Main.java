package school.sptech;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        Region region = Region.US_EAST_1;
        ConexaoBanco conexaoBanco = new ConexaoBanco();
        InstituicaoDao instituicaoDao = new InstituicaoDao(conexaoBanco.getJdbcTemplate());
        LeituraExcel leituraExcel = new LeituraExcel(instituicaoDao);

        System.out.println("Iniciando processo de carga a partir do S3...");

        // Usamos try-with-resources para garantir que o leitor S3 e o stream sejam fechados.
        try (LeituraS3 leitorS3 = new LeituraS3(region);
             ResponseInputStream<GetObjectResponse> s3ObjectStream = leitorS3.obterInputStream()) {

            leituraExcel.processarPlanilha(s3ObjectStream, 2023, "SP");

        } catch (Exception e) {
            System.err.println("Ocorreu um erro fatal durante o processo.");
            e.printStackTrace();
        }

        System.out.println("Processo finalizado.");
    }
}