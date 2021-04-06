package uk.co.mrdaly.anagramator.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.mrdaly.anagramator.jpa.entity.SolverEntry;

import java.math.BigInteger;
import java.util.List;

public interface SolverEntryRepository extends JpaRepository<SolverEntry, Long> {

    List<SolverEntry> findAllByTrimmedTextLikeOrderByText(String like);

    List<SolverEntry> findAllByPrimeProductEquals(BigInteger primeSum);

    List<SolverEntry> findAllByPrimeProductIn(List<Long> primeSums);

    List<SolverEntry> findAllByPrimeProductGreaterThanEqual(BigInteger primeProduct);
}
