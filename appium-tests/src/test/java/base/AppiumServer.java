package base;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

import java.net.URL;

public final class AppiumServer {

    private static AppiumDriverLocalService service;

    private AppiumServer() {
    }

    public static synchronized URL start() {
        if (service == null) {
            service = new AppiumServiceBuilder()
                    .withIPAddress("127.0.0.1")
                    .usingAnyFreePort()
                    .build();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (service.isRunning()) {
                    service.stop();
                }
            }));
        }

        if (!service.isRunning()) {
            service.start();
        }

        return service.getUrl();
    }
}