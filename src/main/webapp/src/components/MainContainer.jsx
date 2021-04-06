import React, {useEffect} from 'react';
import {Form} from "react-bootstrap";
import {ResultsComponent} from "./ResultsComponent";

const PATH_BASE = "/solve";

export const MainContainer = () => {

    const [pattern, setPattern] = React.useState("");
    const [anagram, setAnagram] = React.useState("");
    const [matches, setMatches] = React.useState([]);
    const [partial, setPartial] = React.useState(false);

    const buildAnagramPath = () => {
        let path = `${PATH_BASE}/anagram/${anagram}`;

        const params = {pattern: pattern, partial: partial};

        if (pattern || partial) {
            path += "?";
            const mappedParams = Object.keys(params)
                .filter(param => params[param])
                .map(param => param + "=" + params[param]);
            const paramsString = mappedParams.join("&");
            path += paramsString;
        }

        return path;
    };

    const triggerLookup = async () => {
        setMatches([]);

        let path;


        if ((!anagram || anagram.length < 3) && (!pattern || pattern.length < 3)) {
            setMatches([]);
            return;
        } else if (anagram) {
            path = buildAnagramPath();
        } else if (pattern) {
            path = `${PATH_BASE}/pattern/${pattern}`;
        }

        let response = await fetch(path);

        if (response.ok) {
            let json = await response.json();
            setMatches(json);
        } else {
            setMatches([])
        }
    };

    useEffect(() => {
        triggerLookup()
    }, [pattern]);
    useEffect(() => {
        triggerLookup()
    }, [anagram]);

    return <div>
        <Form>
            <Form.Group controlId={"formPattern"}>
                <Form.Label>Pattern</Form.Label>
                <Form.Control
                    type="text"
                    placeholder="p*tte*n"
                    value={pattern}
                    onChange={(e) => {
                        setPattern(e.target.value)
                        triggerLookup();
                    }}
                />
                <Form.Text className="text-muted">Enter the pattern, using * as a wildcard, e.g. CR*S*W*RD. Case is
                    immaterial</Form.Text>
            </Form.Group>
            <Form.Group controlId={"formAnagram"}>
                <Form.Label>Anagram</Form.Label>
                <Form.Control
                    type="text"
                    placeholder="nag a ram"
                    value={anagram}
                    onChange={(e) => {
                        setAnagram(e.target.value)
                        triggerLookup();
                    }}
                />
                <Form.Text className="text-muted">Enter the letters you want to unscramble.</Form.Text>
            </Form.Group>
            <Form.Group controlId={"partialToggle"}>
                <Form.Check
                    type="checkbox"
                    label="return partial matches"
                    value={partial}
                    onClick={() => setPartial(!partial)}
                />
            </Form.Group>
        </Form>


        <ResultsComponent matches={matches} />


    </div>
};