
import { defineConfig } from "vitest/config";
import { fileURLToPath } from "node:url";

export default defineConfig({
    resolve: {
        alias: {
            "@static": fileURLToPath(
                new URL("./src/main/resources/static", import.meta.url)
            )
        }
    },
    test: {
        environment: "jsdom",
        include: ["src/test/js/**/*.test.js"],
        restoreMocks: true,
        coverage: {
            provider: "v8",
            reporter: ["text", "lcov", "html"],
            reportsDirectory: "./coverage",
            include: ["src/main/resources/static/**/*.js"],
            exclude: [
                "src/main/resources/static/**/*.test.js"
            ]
        }
    }
});

