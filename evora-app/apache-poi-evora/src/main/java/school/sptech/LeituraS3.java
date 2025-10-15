package school.sptech;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Classe responsável por gerenciar a conexão com o AWS S3 e fornecer
 * streams de dados de objetos.
 * Implementa AutoCloseable para garantir que o cliente S3 seja fechado.
 */
public class LeituraS3 implements AutoCloseable {

    private final S3Client s3Client;

    public LeituraS3(Region region) {
        this.s3Client = S3Client.builder()
                .region(region)
                .build();
    }


    public ResponseInputStream<GetObjectResponse> obterInputStream(String s3Path) throws URISyntaxException {
        URI s3Uri = new URI(s3Path);
        String bucketName = s3Uri.getHost();
        String keyName = s3Uri.getPath().substring(1); // Remove a '/' inicial

        if (bucketName == null || keyName.isEmpty()) {
            throw new URISyntaxException(s3Path, "Bucket ou chave inválidos no caminho S3.");
        }

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