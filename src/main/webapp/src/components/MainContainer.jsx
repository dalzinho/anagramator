import React from 'react';
import {Form, ListGroup, ListGroupItem} from "react-bootstrap";

export const MainContainer = () => {

    const [pattern, setPattern] = React.useState("");
    const [anagram, setAnagram] = React.useState("");
    const [matches, setMatches] = React.useState([]);

    const triggerLookup = async () => {
        const PATH_BASE = "/solve";

        let path;
        if (anagram) {
            path = `${PATH_BASE}/anagram/${anagram}`;
            if (pattern) {
                path += `?pattern=${pattern}`;
            }
        } else if (pattern) {
            path =`${PATH_BASE}/pattern/${pattern}`;
        }

        let response = await fetch(path);

        if (response.ok) {
            let json = await response.json();
            setMatches(json);
        }
    };

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
                <Form.Text className="text-muted">Enter the pattern, using * as a wildcard, e.g. CR*S*W*RD. Case is
                    immaterial</Form.Text>
            </Form.Group>
        </Form>

        <div>
            <ListGroup>
                {matches && matches.forEach(match => {return <ListGroupItem>{match.text}</ListGroupItem>})}
            </ListGroup>
        </div>

    </div>
};