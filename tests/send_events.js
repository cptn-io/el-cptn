//to run: k6 run -u 10 --duration 30m send_events.js

import http from 'k6/http';
import {sleep} from 'k6';
import {uuidv4} from 'https://jslib.k6.io/k6-utils/1.4.0/index.js';

const url = 'http://localhost/event/source/9252af7d-dcc9-4bec-a0dc-d48f9a8c262c';

export default function () {
    const payload = {operation: uuidv4(), details: uuidv4()};

    let res = http.post(url, JSON.stringify(payload), {
        headers: {'Content-Type': 'application/json'},
    });
    sleep(1);
}

