package com.click.batchServer.config;

import com.click.batchServer.domain.entity.AccountHistory;
import com.click.batchServer.domain.mongo.AccountHistoryDocument;
import com.click.batchServer.domain.mongo.CategoryDocument;
import com.click.batchServer.domain.repository.AccountHistoryRepository;
import jakarta.persistence.EntityManagerFactory;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
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
        // 현재 날짜 확인
        LocalDateTime now = LocalDateTime.now();

        JobBuilder jobBuilder = new JobBuilder("importAccountHistoryJob", jobRepository);
        SimpleJobBuilder simpleJobBuilder = jobBuilder.start(transferStep());

        // 매월 1일인지 확인하여 조건에 따라 Step 추가
        // if (now.getDayOfMonth() == 1) {
        if (now.getMinute() == 0) {
            return simpleJobBuilder.next(updateAmountByCategoryStep()).build();
        } else {
            return simpleJobBuilder.build();
        }
    }

    // @Bean
    // public Job importAccountHistoryJob() {
    //     return new JobBuilder("importAccountHistoryJob", jobRepository)
    //         .start(transferStep())
    //         // .next(deleteStep())
    //         .build();
    // }

    @Bean
    public Step transferStep() {
        return new StepBuilder("importAccountHistoryStep", jobRepository)
            .tasklet(tasklet(), transactionManager)
            .build();
    }

    @Bean
    public Tasklet tasklet() {
        return (contribution, chunkContext) -> {
            String collectionName = "PastRecord";

            List<AccountHistory> mysqlData = accountHistoryRepository.findAll(PageRequest.of(0, 100)).getContent();

            if(mysqlData.isEmpty()) {
                return RepeatStatus.FINISHED;
            }

            for (AccountHistory accountHistory : mysqlData) {
                AccountHistoryDocument mongoDBData = getAccountHistoryDocument(
                    accountHistory);
                mongoTemplate.save(mongoDBData,collectionName);
                accountHistoryRepository.deleteById(accountHistory.getHistoryId());
            }
            return RepeatStatus.FINISHED;
        };
    }

    private static AccountHistoryDocument getAccountHistoryDocument(AccountHistory accountHistory) {
        CategoryDocument categoryDocument
            = new CategoryDocument(accountHistory.getCategoryId().getCategoryId(), accountHistory.getCategoryId().getCategoryName());
        AccountHistoryDocument mongoDBData = new AccountHistoryDocument();
        mongoDBData.setHistoryId(accountHistory.getHistoryId().toString());
        mongoDBData.setBhAt(accountHistory.getBhAt());
        mongoDBData.setBhName(accountHistory.getBhName());
        mongoDBData.setBhAmount(accountHistory.getBhAmount());
        mongoDBData.setMyAccount(accountHistory.getMyAccount());
        mongoDBData.setBhStatus(accountHistory.getBhStatus());
        mongoDBData.setBhBalance(accountHistory.getBhBalance());
        mongoDBData.setBhOutType(accountHistory.getBhOutType().getName());
        mongoDBData.setCardId(accountHistory.getCardId());
        mongoDBData.setBhMemo(accountHistory.getBhMemo());
        mongoDBData.setCategoryId(categoryDocument);
        return mongoDBData;
    }

    @Bean
    public Step updateAmountByCategoryStep() {
        return new StepBuilder("updateAmountByCategoryStep", jobRepository)
            .tasklet(updateAmountByCategoryTasklet(), transactionManager)
            .build();
    }

    @Bean
    public Tasklet updateAmountByCategoryTasklet() {
        return (contribution, chunkContext) -> {
            int updatedRows = entityManagerFactory.createEntityManager()
                .createQuery("UPDATE AmountByCategory a SET a.abcDisable = false WHERE a.abcDisable = true")
                .executeUpdate();
            System.out.println("Updated rows: " + updatedRows);
            return RepeatStatus.FINISHED;
        };
    }

}
