package uk.co.mrdaly.anagramator.jpa.entity;

import lombok.Data;
import uk.co.mrdaly.anagramator.source.InputSource;

import javax.persistence.*;

@Entity
@Data
public class SolverEntry {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;

    private String uri;
    private String trimmedText;
    private String text;
    private String pageGrouping;
    private long primeProduct;

    @Enumerated(EnumType.STRING)
    private InputSource inputSource;
}
