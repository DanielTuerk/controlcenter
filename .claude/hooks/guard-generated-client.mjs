#!/usr/bin/env node
// PreToolUse(Edit|Write|MultiEdit) guard: block hand-edits to the generated OpenAPI client.
// Exit 2 => block the tool call and feed stderr back to Claude.
import { readFileSync } from "node:fs";

let raw = "";
try {
  raw = readFileSync(0, "utf8");
} catch {
  process.exit(0);
}

let payload;
try {
  payload = JSON.parse(raw);
} catch {
  process.exit(0);
}

const path = (payload?.tool_input?.file_path ?? "").replace(/\\/g, "/");

if (path.includes("frontend/src/shared/openapi-gen/")) {
  process.stderr.write(
    "Blocked: frontend/src/shared/openapi-gen/ is a generated, committed client and must not be hand-edited.\n" +
      "To change it: edit the backend REST resource / shared DTO, then run `npm run generate:api-and-fetch` (or the /regen-api command) with the backend running.\n"
  );
  process.exit(2);
}

process.exit(0);
