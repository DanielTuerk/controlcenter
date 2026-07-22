#!/usr/bin/env node
// PostToolUse(Edit|Write|MultiEdit) reminder: changing a Panache entity needs a Flyway migration.
// Hibernate runs in `validate` mode, so an unmatched entity change fails app startup.
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

if (/\/persist\/entity\/.*\.java$/.test(path)) {
  // additionalContext is surfaced to Claude without blocking.
  const out = {
    hookSpecificOutput: {
      hookEventName: "PostToolUse",
      additionalContext:
        "Reminder: you edited a JPA entity. Hibernate runs in `validate` mode — if you added/changed/removed a persisted field, add a matching Flyway migration in src/main/resources/db/migration/ (next V1.0.x) or the app will fail to start. The /new-migration command scaffolds one.",
    },
  };
  process.stdout.write(JSON.stringify(out));
}

process.exit(0);
