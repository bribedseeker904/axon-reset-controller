package cz.davidstastny.axon.reset;

import org.axonframework.config.Configuration;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.axonframework.eventhandling.replay.ResetContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping
public class ResetController {
    private static final Logger log = LoggerFactory.getLogger(ResetController.class);

    private final Configuration configuration;
    private final Map<String, JpaRepository<?, ?>> repositories;

    public ResetController(Configuration configuration, Map<String, JpaRepository<?, ?>> repositories) {
        this.configuration = configuration;
        this.repositories = repositories;
    }

    @DeleteMapping("/reset")
    public void reset(@RequestParam("reason") String reason) {
        configuration.eventProcessingConfiguration().eventProcessors().values().stream()
                .filter(TrackingEventProcessor.class::isInstance)
                .map(TrackingEventProcessor.class::cast)
                .forEach(it -> {
                    it.shutDown();
                    it.resetTokens(reason);
                    it.start();
                });
    }

    @SuppressWarnings("unused")
    @ResetHandler
    public void purge(ResetContext<String> context) {
        repositories.forEach((name, repo) -> {
            log.warn("Purging {} ...", name);
            repo.deleteAllInBatch();
        });
    }

    @SuppressWarnings("unused")
    @EventHandler(payloadType = Dummy.class)
    public void handle(Dummy dummy) {
        // empty handler just to let Axon Framework to pick up the reset handler
    }

    public interface Dummy {

    }
}
