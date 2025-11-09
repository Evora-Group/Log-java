package school.sptech;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public class LeituraS3 implements AutoCloseable {

    private final S3Client s3Client;

    public LeituraS3(Region region) {
        this.s3Client = S3Client.builder()
                .region(region)
                .build();
    }

    /**
     * Obtém um objeto do S3 como um InputStream.
     *
     * @param keyName A chave (caminho) do arquivo no bucket.
     * @return Um ResponseInputStream com os dados do objeto.
     * @throws IllegalStateException se as variáveis de ambiente não estiverem definidas.
     */
    public ResponseInputStream<GetObjectResponse> obterInputStream(String keyName) {
        String bucketName = System.getenv("S3_BUCKET");

        if (bucketName == null || bucketName.trim().isEmpty()) {
            throw new IllegalStateException("Erro de configuração: A variável 'S3_BUCKET' não está definida.");
        }
        if (keyName == null || keyName.trim().isEmpty()) {
            throw new IllegalStateException("Erro de lógica: A chave (keyName) do arquivo não foi fornecida.");
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