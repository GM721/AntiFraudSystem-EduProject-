package antifraud.DB.Repositories;

import antifraud.DB.Entities.SuspiciousIp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SuspiciousIpRepository extends CrudRepository<SuspiciousIp, Long> {
    @Transactional
    boolean existsByIp (String ip);

    @Transactional
    void removeByIp(String ip);

    @Transactional
    Optional<SuspiciousIp> findSuspiciousIpByIp (String ip);

    @Transactional
    List<SuspiciousIp> findAllByOrderByIdAsc();

}
