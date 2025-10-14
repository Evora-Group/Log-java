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

        basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        basicDataSource.setUrl("jdbc:mysql://localhost:3306/evora");
        basicDataSource.setUsername("user");
        basicDataSource.setPassword("Urubu100");

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
