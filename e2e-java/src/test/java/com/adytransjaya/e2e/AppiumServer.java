package com.adytransjaya.e2e;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Menjalankan server Appium otomatis bila belum ada, sama seperti
 * penanganan di e2e/wdio.conf.js versi JavaScript.
 */
final class AppiumServer {

    static final String HOST = System.getProperty("appium.host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("appium.port", "4723"));

    private static Process process;

    private AppiumServer() {
    }

    static String statusUrl() {
        return "http://" + HOST + ":" + PORT + "/status";
    }

    static synchronized void ensureRunning() {
        if (isUp()) {
            System.out.println("[e2e] Memakai server Appium yang sudah berjalan di " + HOST + ":" + PORT);
            return;
        }

        Path log = Path.of(System.getProperty("user.dir"), "build", "appium-server.log")
                .toAbsolutePath();
        try {
            Files.createDirectories(log.getParent());
            process = new ProcessBuilder(
                    "appium",
                    "--address", HOST,
                    "--port", String.valueOf(PORT),
                    "--log-level", "warn")
                    .redirectErrorStream(true)
                    .redirectOutput(log.toFile())
                    .start();
            Runtime.getRuntime().addShutdownHook(new Thread(AppiumServer::stop));
            System.out.println("[e2e] Menjalankan server Appium (log: " + log + ")");
        } catch (IOException error) {
            throw new IllegalStateException(
                    "Gagal menjalankan perintah 'appium'. Pastikan Appium 3 terpasang di PATH.", error);
        }

        long deadline = System.nanoTime() + Duration.ofSeconds(60).toNanos();
        while (System.nanoTime() < deadline) {
            if (isUp()) return;
            try {
                Thread.sleep(500);
            } catch (InterruptedException error) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        throw new IllegalStateException(
                "Server Appium tidak merespons di " + statusUrl() + " setelah 60 detik. Lihat " + log);
    }

    static synchronized void stop() {
        if (process == null) return;
        process.destroy();
        try {
            if (!process.waitFor(10, TimeUnit.SECONDS)) {
                process.destroyForcibly();
            }
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
        }
        process = null;
    }

    static boolean isUp() {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(2))
                    .build();
            HttpRequest request = HttpRequest.newBuilder(URI.create(statusUrl()))
                    .timeout(Duration.ofSeconds(2))
                    .GET()
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode() == 200;
        } catch (Exception error) {
            return false;
        }
    }
}
