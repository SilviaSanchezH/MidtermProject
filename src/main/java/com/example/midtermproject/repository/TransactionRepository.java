package com.example.midtermproject.repository;

import com.example.midtermproject.model.Accounts.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query(value = "SELECT * FROM transaction WHERE origin_account = :originAccount AND transaction_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    public List<Transaction> findByTransactionDateBetween(@Param("originAccount") Integer originAccount, @Param("startDate") Date startDate, @Param("endDate")Date endDate);

    @Query(value = "SELECT MAX(t.transactions) FROM (SELECT COUNT(*) AS transactions, DAY(transaction_date), origin_account FROM transaction GROUP BY origin_account, DAY(transaction_date)) t", nativeQuery = true)
    public Integer transactionsIn24HoursForAnyAccount();
}
