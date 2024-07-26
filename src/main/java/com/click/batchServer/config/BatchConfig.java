package com.click.batchServer.config;

import com.click.batchServer.domain.entity.AccountHistory;
import com.click.batchServer.domain.mongo.AccountHistoryDocument;
import com.click.batchServer.domain.repository.AccountHistoryRepository;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class BatchConfig {

    private final EntityManagerFactory entityManagerFactory;
    private final MongoTemplate mongoTemplate;
    private final AccountHistoryRepository accountHistoryRepository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job importAccountHistoryJob() {
        return new JobBuilder("importAccountHistoryJob", jobRepository)
            .start(transferStep())
            .next(deleteStep())
            .build();
    }

    @Bean
    public Step transferStep() {
        return new StepBuilder("transferStep", jobRepository)
            .<AccountHistory, AccountHistoryDocument>chunk(100, transactionManager)
            .reader(reader())
            .processor(processor())
            .writer(writer())
            .allowStartIfComplete(true)
            .build();
    }

    @Bean
    public Step deleteStep() {
        return new StepBuilder("deleteStep", jobRepository)
            .tasklet(deleteTasklet(), transactionManager)
            .allowStartIfComplete(true)
            .build();
    }

    @Bean
    public JpaPagingItemReader<AccountHistory> reader() {
        return new JpaPagingItemReaderBuilder<AccountHistory>()
            .name("accountHistoryReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT a FROM AccountHistory a")
            .pageSize(20)
            .build();
    }

    @Bean
    public ItemProcessor<AccountHistory, AccountHistoryDocument> processor() {
        return item -> {
            AccountHistoryDocument document = new AccountHistoryDocument();
            document.setBhAt(item.getBhAt());
            document.setBhName(item.getBhName());
            document.setBhAmount(item.getBhAmount());
            document.setMyAccount(item.getMyAccount());
            document.setYourAccount(item.getYourAccount());
            document.setBhStatus(item.getBhStatus());
            document.setBhBalance(item.getBhBalance());
            document.setBhOutType(item.getBhOutType().name());
            document.setCardId(item.getCardId());
            document.setBhReceive(item.getBhReceive());
            document.setBhMemo(item.getBhMemo());
            document.setCategoryName(item.getCategoryId().getCategoryName());
            return document;
        };
    }

    @Bean
    public MongoItemWriter<AccountHistoryDocument> writer() {
        MongoItemWriter<AccountHistoryDocument> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        writer.setCollection(getYesterdayCollectionName());
        return writer;
    }

    private String getYesterdayCollectionName() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return yesterday.format(formatter);
    }

    @Bean
    public Tasklet deleteTasklet() {
        return (contribution, chunkContext) -> {
            accountHistoryRepository.deleteAll();
            return null;
        };
    }
}
