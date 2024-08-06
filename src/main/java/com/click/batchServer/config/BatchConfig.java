package com.click.batchServer.config;

import com.click.batchServer.domain.entity.AccountHistory;
import com.click.batchServer.domain.mongo.AccountHistoryDocument;
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
// import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
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
        return new JobBuilder("importAccountHistoryJob", jobRepository)
            .start(transferStep())
            // .next(deleteStep())
            .build();
    }

    @Bean
    public Step transferStep() {
        return new StepBuilder("importAccountHistoryStep", jobRepository)
            .tasklet(tasklet(), transactionManager)
            .build();
    }

    @Bean
    public Tasklet tasklet() {
        return (contribution, chunkContext) -> {
            String dateString = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String collectionName = "data_" + dateString;

            List<AccountHistory> mysqlData = accountHistoryRepository.findAll(PageRequest.of(0, 100)).getContent();

            if(mysqlData.isEmpty()) {
                return RepeatStatus.FINISHED;
            }

            for (AccountHistory accountHistory : mysqlData) {
                AccountHistoryDocument mongoDBData = new AccountHistoryDocument();
                mongoDBData.setBhId(accountHistory.getHistoryId().toString());
                mongoDBData.setBhAt(accountHistory.getBhAt());
                mongoDBData.setBhName(accountHistory.getBhName());
                mongoDBData.setBhAmount(accountHistory.getBhAmount());
                mongoDBData.setMyAccount(accountHistory.getMyAccount());
                mongoDBData.setBhStatus(accountHistory.getBhStatus());
                mongoDBData.setBhBalance(accountHistory.getBhBalance());
                mongoDBData.setBhOutType(accountHistory.getBhOutType().getName());
                mongoDBData.setCardId(accountHistory.getCardId());
                mongoDBData.setBhMemo(accountHistory.getBhMemo());
                mongoDBData.setCategoryName(accountHistory.getCategoryId().getCategoryName());
                mongoTemplate.save(mongoDBData,collectionName);
                accountHistoryRepository.deleteById(accountHistory.getHistoryId());
            }
            return RepeatStatus.FINISHED;
        };
    }

    // @Bean
    // public Step transferStep() {
    //     return new StepBuilder("transferStep", jobRepository)
    //         .<AccountHistory, AccountHistoryDocument>chunk(100, transactionManager)
    //         .reader(reader())
    //         .processor(processor())
    //         .writer(writer())
    //         .allowStartIfComplete(true)
    //         .build();
    // }

    // @Bean
    // public Step deleteStep() {
    //     return new StepBuilder("deleteStep", jobRepository)
    //         .tasklet(deleteTasklet(), transactionManager)
    //         .allowStartIfComplete(true)
    //         .build();
    // }
    //
    // @Bean
    // public JpaPagingItemReader<AccountHistory> reader() {
    //     return new JpaPagingItemReaderBuilder<AccountHistory>()
    //         .name("accountHistoryReader")
    //         .entityManagerFactory(entityManagerFactory)
    //         .queryString("SELECT a FROM AccountHistory a")
    //         .pageSize(20)
    //         .build();
    // }
    //
    // @Bean
    // public ItemProcessor<AccountHistory, AccountHistoryDocument> processor() {
    //     return item -> {
    //         AccountHistoryDocument document = new AccountHistoryDocument();
    //         document.setBhAt(item.getBhAt());
    //         document.setBhName(item.getBhName());
    //         document.setBhAmount(item.getBhAmount());
    //         document.setMyAccount(item.getMyAccount());
    //         document.setBhStatus(item.getBhStatus());
    //         document.setBhBalance(item.getBhBalance());
    //         document.setBhOutType(item.getBhOutType().name());
    //         document.setCardId(item.getCardId());
    //         document.setBhMemo(item.getBhMemo());
    //         document.setCategoryName(item.getCategoryId().getCategoryName());
    //         return document;
    //     };
    // }
    //
    // // writer()를 transferStep() 내에서 동적으로 생성하여 반환
    // private MongoItemWriter<AccountHistoryDocument> writer() {
    //     MongoItemWriter<AccountHistoryDocument> writer = new MongoItemWriter<>();
    //     writer.setTemplate(mongoTemplate);
    //     writer.setCollection(getYesterdayCollectionName());
    //     return writer;
    // }
    //
    // // 각 배치 실행 시점마다 새로운 컬렉션 이름을 생성
    // private String getYesterdayCollectionName() {
    //     LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
    //     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HH:mm");
    //     return yesterday.format(formatter);
    // }
    //
    // @Bean
    // public Tasklet deleteTasklet() {
    //     return (contribution, chunkContext) -> {
    //         accountHistoryRepository.deleteAll();
    //         return RepeatStatus.FINISHED;
    //     };
    // }
}
