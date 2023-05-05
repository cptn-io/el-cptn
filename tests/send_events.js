//to run: k6 run -u 10 --duration 30m send_events.js

import http from 'k6/http';
import {sleep} from 'k6';

const url = 'http://localhost/event/source/385d483e-cf5e-4d22-beaa-1059015da38e';

export default function () {
    const payload = {name: 'foo'};

    let res = http.post(url, JSON.stringify(payload), {
        headers: {'Content-Type': 'application/json'},
    });
    sleep(1);
}

