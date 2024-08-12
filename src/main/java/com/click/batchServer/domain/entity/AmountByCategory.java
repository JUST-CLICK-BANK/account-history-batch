package com.click.batchServer.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "AMOUNT_BY_CATEGORY")
public class AmountByCategory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ABC_ID")
    private Long abcId;

    @Column(name = "ABC_ACCOUNT")
    private String abcAccount;

    @Column(name = "ABC_CATEGORY")
    private String abcCategory;

    @Column(name = "ABC_AMOUNT") @Setter
    private Long abcAmount;

    @Column(name = "ABC_DISABLE")
    private Boolean abcDisable;
}
