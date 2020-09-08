package xyz.rpolnx.spring.file.read.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import xyz.rpolnx.spring.file.read.service.ReportHandler;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportListener {
  private final WatchService watchService;
  private final ReportHandler handler;

  @Value("${watch.path}")
  private String watchPath;

  @PostConstruct
  public void launchMonitoring() {
    log.debug("Creating EventListener");
    try {
      WatchKey key;
      while ((key = watchService.take()) != null) {

        for (WatchEvent<?> event : key.pollEvents()) {
          String fileName = event.context().toString();

          if (!fileName.endsWith(".dat")) {
            continue;
          }

          Path filePath = Path.of(watchPath, fileName);

          if (isLocked(filePath)) {
            continue;
          }

          handler.processFile(filePath);
        }
        key.reset();
      }

      log.debug("EventListener created");
    } catch (InterruptedException e) {
      log.warn("Got Interruption exception on watcher service", e);
    }
  }

  @PreDestroy
  public void stopMonitoring() {
    log.info("Finalizing listener");

    if (watchService != null) {
      try {
        watchService.close();
      } catch (IOException e) {
        log.error("Exception while closing the monitoring service");
      }
    }
  }

  private boolean isLocked(Path path) {
    try (FileChannel ch = FileChannel.open(path, StandardOpenOption.WRITE)) {
      FileLock lock = ch.tryLock();

      return lock == null;
    } catch (IOException e) {
      return true;
    }
  }
}
