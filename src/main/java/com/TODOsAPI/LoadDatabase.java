package com.TODOsAPI;

import com.TODOsAPI.Model.TodoNote;
import com.TODOsAPI.Model.TodoRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LoadDatabase {
    @Bean
    CommandLineRunner initDatabase(TodoRepo repo) {
        return args -> {
            log.info("Preloading default todo: " + repo.save(new TodoNote("A nice title", "A longer body", TodoNote.STATUS.CREATED)));
            log.info("Preloading default todo: " + repo.save(new TodoNote("Another title", "Another body", TodoNote.STATUS.CREATED)));
        };
    }
}
