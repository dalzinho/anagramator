package uk.co.mrdaly.anagramator.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.mrdaly.anagramator.jpa.entity.SolverEntry;

import java.util.List;

public interface SolverEntryRepository extends JpaRepository<SolverEntry, Long> {

    List<SolverEntry> findSolverEntryBySortedTextStartsWith(String startsWith);

    List<SolverEntry> findAllByTrimmedTextLike(String like);
}
