import { rmSync } from "node:fs";
import { execSync } from "node:child_process";

rmSync("src/shared/openapi-gen", { recursive: true, force: true });

execSync("npm run openapi:generate", { stdio: "inherit" });
