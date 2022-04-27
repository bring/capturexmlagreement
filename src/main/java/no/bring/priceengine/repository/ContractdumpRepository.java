package no.bring.priceengine.repository;

import no.bring.priceengine.dao.Contractdump;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractdumpRepository extends JpaRepository<Contractdump, Long > {
    List<Contractdump> findByUdpdatedIsFalse();
}
