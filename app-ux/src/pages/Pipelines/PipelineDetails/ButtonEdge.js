import React from 'react';
import { getBezierPath } from 'reactflow';

import './edgeButton.scss';

const foreignObjectSize = 40;

//ref: https://reactflow.dev/docs/examples/edges/edge-with-button/

export default function ButtonEdge({
    id,
    sourceX,
    sourceY,
    targetX,
    targetY,
    sourcePosition,
    targetPosition,
    data,
    style = {},
    markerEnd,
}) {
    const [edgePath, labelX, labelY] = getBezierPath({
        sourceX,
        sourceY,
        sourcePosition,
        targetX,
        targetY,
        targetPosition,
    });
    const { removeEdge } = data;
    return (
        <>
            <path
                id={id}
                style={style}
                className="react-flow__edge-path"
                d={edgePath}
                markerEnd={markerEnd}
            />
            <foreignObject
                width={foreignObjectSize}
                height={foreignObjectSize}
                x={labelX - foreignObjectSize / 2}
                y={labelY - foreignObjectSize / 2}
                className="edgebutton-foreignobject"
                requiredExtensions="http://www.w3.org/1999/xhtml"
            >
                <div>
                    <button className="edgebutton" onClick={(event) => removeEdge(event, id)}>
                        ×
                    </button>
                </div>
            </foreignObject>
        </>
    );
}