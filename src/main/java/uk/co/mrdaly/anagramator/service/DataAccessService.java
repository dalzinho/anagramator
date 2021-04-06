package uk.co.mrdaly.anagramator.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import uk.co.mrdaly.anagramator.jpa.entity.SolverEntry;
import uk.co.mrdaly.anagramator.jpa.repository.SolverEntryRepository;

import java.util.List;

@Service
public class DataAccessService {

    private final SolverEntryRepository solverEntryRepository;

    public DataAccessService(SolverEntryRepository solverEntryRepository) {
        this.solverEntryRepository = solverEntryRepository;
    }


    @Cacheable(value = "wordsByPrimeSumIn", key = "#primeProducts")
    public List<SolverEntry> findWordsByPrimeProductIn(List<Long> primeProducts) {
        return solverEntryRepository.findAllByPrimeProductIn(primeProducts);
    }
}
