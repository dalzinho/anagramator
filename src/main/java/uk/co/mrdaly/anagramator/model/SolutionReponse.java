package uk.co.mrdaly.anagramator.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class SolutionReponse implements Serializable {

    private String text;
    private String uri;
}
