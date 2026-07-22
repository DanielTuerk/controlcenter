import { rmSync } from "node:fs";
import { execSync } from "node:child_process";

const OPENAPI_URL = "http://localhost:8080/q/openapi";
const OPENAPI_FILE = "openapi.yaml";
const OUTPUT_DIR = "src/shared/openapi-gen";

execSync(`curl -o ${OPENAPI_FILE} ${OPENAPI_URL}`, {
  stdio: "inherit",
});

rmSync(OUTPUT_DIR, { recursive: true, force: true });

execSync(
  `openapi-generator-cli generate \
   -i ${OPENAPI_FILE} \
   -g typescript-angular \
   -o ${OUTPUT_DIR}`,
  { stdio: "inherit" }
);
