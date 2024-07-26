package com.click.batchServer.domain.repository;

import com.click.batchServer.domain.entity.AccountHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountHistoryRepository extends JpaRepository<AccountHistory, Long> {

}
