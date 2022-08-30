package antifraud.DB.Repositories;

import antifraud.DB.Entities.StolenCard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface StolenCardRepository extends CrudRepository<StolenCard,Long> {
    @Transactional
    boolean existsByNumber (String number);

    @Transactional
    Optional<StolenCard> findStolenCardByNumber(String number);

    @Transactional
    void deleteByNumber(String number);

    @Transactional
    List<StolenCard> findAllByOrderByIdAsc();
}

