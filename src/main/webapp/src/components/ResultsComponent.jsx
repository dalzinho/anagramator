import React from 'react';
import {ListGroup} from "react-bootstrap";

export const ResultsComponent = (props) => {
    return <ListGroup>
        {props.matches && renderMatchers(props.matches)}
    </ListGroup>
};

const renderMatchers = (matches) => {
    const rendered = [];
    for (let i = 0; i < matches.length; i++) {
        const match = matches[i];
        rendered.push(<ListGroup.Item key={match.text + i}><a href={match.uri}>{match.text}</a></ListGroup.Item>)
    }
    return rendered;
};