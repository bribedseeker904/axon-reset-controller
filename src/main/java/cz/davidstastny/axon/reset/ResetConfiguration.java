package cz.davidstastny.axon.reset;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Map;

@Configuration
public class ResetConfiguration {

    @Bean
    public ResetController resetController(org.axonframework.config.Configuration configuration,
                                           Map<String, JpaRepository<?, ?>> repositories) {
        return new ResetController(configuration, repositories);
    }
}
