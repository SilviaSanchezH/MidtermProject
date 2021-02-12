package com.example.midtermproject.repository;

import com.example.midtermproject.model.Accounts.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query(value = "SELECT * FROM transaction WHERE origin_account = :originAccount AND transaction_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    public List<Transaction> findByTransactionDateBetween(@Param("originAccount") Integer originAccount, @Param("startDate") Date startDate, @Param("endDate")Date endDate);

    @Query(value = "SELECT MAX(t.sum) FROM (SELECT SUM(amount) AS sum, DAY(transaction_date), origin_account FROM transaction " +
            "WHERE origin_account = :originAccount GROUP BY DAY(transaction_date)) t", nativeQuery = true)
    public BigDecimal transactionsValueInAny24Hours(@Param("originAccount") Integer originAccount);

    @Query(value = "SELECT SUM(amount) AS sum FROM transaction WHERE origin_account = :originAccount AND transaction_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    public BigDecimal transactionsValueInRange(@Param("originAccount") Integer originAccount, @Param("startDate") Date startDate, @Param("endDate")Date endDate);
}
