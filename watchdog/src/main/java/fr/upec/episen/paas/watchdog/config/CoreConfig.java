package fr.upec.episen.paas.watchdog.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;

@Configuration
public class CoreConfig {

    @Bean
    public List<String> coreList() throws IOException {
        Yaml yaml = new Yaml();

        var data = yaml.load(
                Files.newInputStream(Paths.get(System.getenv("shared_file_path"))));

        @SuppressWarnings("unchecked")
        List<String> cores = (List<String>) ((java.util.Map<?, ?>) data).get("cores");

        return cores;
    }
}