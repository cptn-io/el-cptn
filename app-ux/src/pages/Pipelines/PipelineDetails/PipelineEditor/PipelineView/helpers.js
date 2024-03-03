
import forEach from 'lodash/forEach';
import filter from 'lodash/filter';

const START_POS_X = 250;
const START_POS_Y = 50;

const getTransformationNodes = (draft, connectedNodes = [], unconnectedNodes = []) => {
    const { source, destination: target, transformationMap = {} } = draft;
    const numberOfTransformations = (connectedNodes.length + unconnectedNodes.length);
    const nodes = [{
        id: source.id,
        type: 'input',
        data: { label: source.name },
        connectable: true,
        deletable: false,
        position: transformationMap?.positions?.[source.id] || { x: START_POS_X, y: START_POS_Y },
        style: { backgroundColor: `oklch(var(--a))`, fontSize: '1.1rem' },
    }, ...connectedNodes.map((transformation, i) => ({
        id: transformation.id,
        data: { label: transformation.name },
        connectable: true,
        position: transformationMap?.positions?.[transformation.id] || { x: (START_POS_X + (i + 1) * 50), y: (START_POS_Y + (i + 1) * 100) },
        style: { backgroundColor: `oklch(var(--in))`, fontSize: '1.1rem' }
    })), ...unconnectedNodes.map((transformation, i) => ({
        id: transformation.id,
        data: { label: transformation.name },
        connectable: true,
        position: transformationMap?.positions?.[transformation.id] || { x: START_POS_X + 400, y: (START_POS_Y + (i + 1) * 100) },
        style: { backgroundColor: `oklch(var(--in))`, fontSize: '1.1rem' },
    })), {
        id: target.id,
        type: 'output',
        data: { label: target.name },
        position: transformationMap?.positions?.[target.id] || { x: START_POS_X + (numberOfTransformations + 1) * 50, y: (START_POS_Y + (numberOfTransformations + 1) * 100) },
        style: { backgroundColor: `oklch(var(--er))`, fontSize: '1.1rem' },
    }];
    return nodes;
}

const processConnectionMap = (data) => {
    const edges = [], connectedNodes = [], unconnectedNodes = [];

    const { transformations = [], transformationMap = {} } = data;
    const { edgeMap = [] } = transformationMap;
    edgeMap.forEach(edge => {
        //are source and target nodes valid for this edge
        if ((data.source.id === edge.source || transformations.find(elem => elem.id === edge.source)) && (data.destination.id === edge.target || transformations.find(elem => elem.id === edge.target))) {
            edges.push({
                id: edge.source + edge.target,
                source: edge.source,
                target: edge.target,
                type: 'buttonedge'
            })
        }
    });

    forEach(transformations, transformation => {
        if (filter(edges, edge => edge.source === transformation.id || edge.target === transformation.id).length > 0) {
            connectedNodes.push(transformation);
        } else {
            unconnectedNodes.push(transformation);
        }
    });

    return {
        edges, connectedNodes, unconnectedNodes
    };
}

export {
    getTransformationNodes,
    processConnectionMap
}