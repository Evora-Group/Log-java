package school.sptech;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConexaoBanco {

    private final JdbcTemplate jdbcTemplate;
    private final BasicDataSource basicDataSource;

    private static final Logger logger = LoggerFactory.getLogger(ConexaoBanco.class);

    public ConexaoBanco() throws InterruptedException {
        BasicDataSource basicDataSource = new BasicDataSource();

//        basicDataSource.setUrl("jdbc:mysql://localhost:3306/evora");
//        basicDataSource.setUsername("root");
//        basicDataSource.setPassword("Urubu100");

        String dbHost = System.getenv("DB_HOST");
        String dbName = System.getenv("DB_NAME");
        String url = String.format("jdbc:mysql://%s/%s?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true", dbHost, dbName);
        basicDataSource.setUrl(url);
        basicDataSource.setUsername(System.getenv("DB_USER"));
        basicDataSource.setPassword(System.getenv("DB_PASSWORD"));

        basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        this.basicDataSource = basicDataSource;
        this.jdbcTemplate = new JdbcTemplate(basicDataSource);

        logger.info("Conexão entre Java e Banco de Dados");
        Thread.sleep(1000);
    }

    public BasicDataSource getBasicDataSource() {
        return basicDataSource;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
