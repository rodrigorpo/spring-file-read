package xyz.rpolnx.spring.file.read.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.*;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FileWatcherConfig {
  @Value("${watch.path}")
  private String watchPath;

  @Bean
  public WatchService watchService() {
    log.info("Monitoring .dat files on folder: {}", watchPath);

    WatchService watchService = null;
    try {
      watchService = FileSystems.getDefault().newWatchService();
      Path path = Paths.get(watchPath);

      if (!Files.isDirectory(path)) {
        throw new RuntimeException("Incorrect monitoring folder: " + path);
      }

      path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

      return watchService;
    } catch (IOException e) {
      log.error("Unexpected exception thrown when creating WatchService context", e);
    }

    throw new RuntimeException("Cannot create instance of WatchService");
  }
}
