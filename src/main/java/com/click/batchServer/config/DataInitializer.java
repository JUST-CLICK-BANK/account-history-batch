package com.click.batchServer.config;

import com.click.batchServer.domain.entity.Category;
import com.click.batchServer.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final CategoryRepository categoryRepository;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            if (categoryRepository.count() == 0) {
                categoryRepository.save(new Category(1L, "식비"));
                categoryRepository.save(new Category(2L, "생활"));
                categoryRepository.save(new Category(3L, "쇼핑"));
                categoryRepository.save(new Category(4L, "교통"));
                categoryRepository.save(new Category(5L, "의료/건강"));
                categoryRepository.save(new Category(6L, "문화/여가"));
                categoryRepository.save(new Category(7L, "교육"));
                categoryRepository.save(new Category(8L, "경조/선물"));
                categoryRepository.save(new Category(9L, "수입"));
            }
        };
    }
}
