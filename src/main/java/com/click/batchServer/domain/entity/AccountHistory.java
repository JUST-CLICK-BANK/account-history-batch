package com.click.batchServer.domain.entity;

import com.click.batchServer.type.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ACCOUNT_HISTORIES")
public class AccountHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HISTORY_ID")
    private Long historyId;

    @Column(name = "BH_AT")
    private LocalDateTime bhAt;

    @Column(name = "BH_NAME")
    private String bhName;

    @Column(name = "BH_AMOUNT")
    private Long bhAmount;

    @Column(name = "MY_ACCOUNT")
    private String myAccount;

    // @Column(name = "YOUR_ACCOUNT")
    // private String yourAccount;

    @Column(name = "BH_STATUS")
    private String bhStatus;

    @Column(name = "BH_BALANCE")
    private Long bhBalance;

    @Column(name = "BH_OUT_TYPE")
    @Enumerated(EnumType.STRING)
    private TransactionType bhOutType;

    @Column(name = "CARD_ID")
    private Long cardId;

    // @Column(name = "BH_RECEIVE")
    // private String bhReceive;

    @Column(name = "BH_MEMO")
    @Setter
    private String bhMemo;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private Category categoryId;
}
