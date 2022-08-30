package antifraud.DB.Repositories;

import antifraud.DB.Entities.CardLimit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CardLimitRepository extends CrudRepository<CardLimit,String> {

    @Transactional
    Optional<CardLimit> findByNumber(String s);

}
