package antifraud.DB.Repositories;

import antifraud.DB.Entities.Transaction;
import antifraud.DB.Entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    @Transactional
    List<Transaction> findAllByOrderByTransactionIdAsc();

    @Transactional
    List<Transaction> findAllByNumberOrderByTransactionIdAsc(String number);

    @Transactional
    boolean existsByNumber(String number);

/*
    @Transactional
    @Query("SELECT count(t) FROM Transaction t where timediff( :date , t.date) <= time('2:00:00') AND timediff(:date, t.date)  >= time('00:00:00') AND t.number = :number ")
    int countTransactionByTime (@Param("date") Date date, @Param("number") String number);

    @Transactional
    @Query("SELECT count(t) FROM Transaction t where t.region = :region AND t.number = :number")
    int countTransactionByRegion (@Param("region") String region, @Param("number") String number);
 */
    @Transactional
    @Query(value = "SELECT DISTINCT t.region FROM Transaction t where timediff(:date, t.date) <= time(\"1:00:00\") AND timediff(:date, t.date)  >= time(\"00:00:00\") AND t.region <> :region AND t.number = :number " , nativeQuery = true)
    List<String> regionsByTime (@Param("date") Date date, @Param("region") String region, @Param("number") String number);

    default int  countRegions(Transaction trans) {
        return regionsByTime(trans.getDate(), trans.getRegion(), trans.getNumber()).size();
    }

    @Transactional
    @Query(value = "SELECT DISTINCT t.ip FROM Transaction t WHERE timediff(:date, t.date) <= time('1:00:00') AND timediff(:date, t.date)  >= time('00:00:00') AND t.ip <> :ip AND t.number = :number " ,  nativeQuery = true )
    List<String> ipsByTime (@Param("date") Date date, @Param("ip") String ip, @Param("number") String number);

    default int  countIps(Transaction trans) {
        return ipsByTime(trans.getDate(), trans.getIp(), trans.getNumber()).size();
    }
}
