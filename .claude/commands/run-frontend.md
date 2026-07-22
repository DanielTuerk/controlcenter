---
description: Start the Angular dev server with the backend proxy
---

Start the frontend dev server.

From the `frontend/` directory, run `npm run start:dev` **in the background** (long-running). This serves Angular with `proxyconfig.json`, which proxies `/api` to the backend on `http://localhost:8080`.

The backend should be running for API calls and the websocket to work — if it isn't, mention that `/run-backend` starts it. After launching, report the local URL the dev server prints.
