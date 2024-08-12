package com.click.batchServer.domain.mongo;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Document(collection = "PastRecord")
public class AccountHistoryDocument {
    @Id
    @Field("_id")
    private String historyId;
    private LocalDateTime bhAt;
    private String bhName;
    private Long bhAmount;
    private String myAccount;
    private String bhStatus;
    private Long bhBalance;
    private String bhOutType;
    private Long cardId;
    private String bhMemo;
    private CategoryDocument categoryId;
}
