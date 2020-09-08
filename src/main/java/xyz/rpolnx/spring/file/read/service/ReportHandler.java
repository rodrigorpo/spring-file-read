package xyz.rpolnx.spring.file.read.service;

import java.nio.file.Path;

public interface ReportHandler {
  void processFile(Path changedFile);
}
