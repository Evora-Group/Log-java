package school.sptech;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.InputStream; // Importação corrigida para o tipo de retorno

public class LeituraS3 implements AutoCloseable {

    private final S3Client s3Client;

    public LeituraS3(Region region) {
        this.s3Client = S3Client.builder()
                .region(region)
                .build();
    }

    /**
     * Obtém um objeto do S3 como um InputStream.
     * Este método lê as variáveis de ambiente 'S3_BUCKET' e 'S3_FILE_KEY'
     * para localizar o arquivo.
     *
     * @return Um ResponseInputStream com os dados do objeto.
     * @throws IllegalStateException se as variáveis de ambiente não estiverem definidas.
     */
    public ResponseInputStream<GetObjectResponse> obterInputStream() {
        // CORREÇÃO: Lemos as variáveis de ambiente diretamente aqui.
        String bucketName = System.getenv("S3_BUCKET");
        String keyName = System.getenv("S3_FILE_KEY");

        // Validação crucial para garantir que a aplicação está configurada corretamente
        if (bucketName == null || bucketName.trim().isEmpty()) {
            throw new IllegalStateException("Erro de configuração: A variável de ambiente 'S3_BUCKET' não está definida.");
        }
        if (keyName == null || keyName.trim().isEmpty()) {
            throw new IllegalStateException("Erro de configuração: A variável de ambiente 'S3_FILE_KEY' não está definida.");
        }

        System.out.println("Lendo do Bucket: " + bucketName + ", Chave: " + keyName);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        return s3Client.getObject(getObjectRequest);
    }

    @Override
    public void close() {
        if (s3Client != null) {
            s3Client.close();
        }
    }
}