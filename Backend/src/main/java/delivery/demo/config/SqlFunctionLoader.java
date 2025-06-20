package delivery.demo.config;
import jakarta.annotation.PostConstruct;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class SqlFunctionLoader implements ApplicationRunner {

    private final DataSource dataSource;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String sql = Files.readString(Path.of("src/main/resources/sql/funciones_postgresql.sql"));

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            System.err.println("Error al ejecutar funciones SQL: " + e.getMessage());
            throw e;
        }
    }

    public SqlFunctionLoader(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
