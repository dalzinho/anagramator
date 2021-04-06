package uk.co.mrdaly.anagramator.jpa.entity;

import lombok.Data;
import uk.co.mrdaly.anagramator.source.InputSource;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Data
public class SolverEntry {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    private String uri;
    private String trimmedText;
    private String text;
    private String pageGrouping;

    @Column(precision = 100, scale = 0)
    private BigInteger primeProduct;

    @Enumerated(EnumType.STRING)
    private InputSource inputSource;
}
