package fr.upec.episen.paas.cacheloader.listener;

import fr.upec.episen.paas.cacheloader.scheduler.CacheRefreshScheduler;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class PostgresNotificationListener implements InitializingBean, DisposableBean {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CacheRefreshScheduler cacheRefreshScheduler;

    private Connection conn;
    private Thread thread;
    private volatile boolean running = true;

    @Override
    public void afterPropertiesSet() {
        try {
            conn = dataSource.getConnection();
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("LISTEN people_changed");
            }

            thread = new Thread(this::listenLoop, "pg-listener");
            thread.setDaemon(true);
            thread.start();
            System.out.println("[PG-Listener] Started listening to people_changed");
        } catch (Exception e) {
            System.err.println("[PG-Listener] Failed to start listener: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void listenLoop() {
        try {
            PGConnection pgConnection = conn.unwrap(PGConnection.class);
            while (running && !Thread.currentThread().isInterrupted()) {
                // issue a simple query to receive any pending notifications from server
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("SELECT 1");
                }

                PGNotification[] notifications = pgConnection.getNotifications();
                if (notifications != null) {
                    for (PGNotification n : notifications) {
                        System.out.println("[PG-Listener] Notification received: " + n.getName() + " payload=" + n.getParameter());
                        // Trigger cache refresh
                        cacheRefreshScheduler.tableHasBeenUpdated();
                    }
                }

                // sleep briefly
                Thread.sleep(500L);
            }
        } catch (Exception e) {
            System.err.println("[PG-Listener] Listener error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        running = false;
        try {
            if (thread != null) thread.interrupt();
            if (conn != null) conn.close();
        } catch (Exception e) {
            // ignore
        }
        System.out.println("[PG-Listener] Stopped");
    }
}
