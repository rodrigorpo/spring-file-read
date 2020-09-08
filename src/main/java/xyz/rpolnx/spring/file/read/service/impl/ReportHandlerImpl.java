package xyz.rpolnx.spring.file.read.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.rpolnx.spring.file.read.domain.reports.CustomerReportGenerator;
import xyz.rpolnx.spring.file.read.domain.reports.ReportGenerator;
import xyz.rpolnx.spring.file.read.domain.reports.SalesReportGenerator;
import xyz.rpolnx.spring.file.read.domain.reports.SalesmanReportGenerator;
import xyz.rpolnx.spring.file.read.service.ReportHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Slf4j
public class ReportHandlerImpl implements ReportHandler {
  @Value("${publish.path}")
  private String publishPath;

  private final String FINAL_FILE_PATTERN = "%s.done.dat";

  private static final Map<String, ReportGenerator> reportMap = new HashMap<>();

  public static void init() {
    ReportGenerator salesman = new SalesmanReportGenerator();
    ReportGenerator customer = new CustomerReportGenerator();
    ReportGenerator sales = new SalesReportGenerator();

    reportMap.put(salesman.getHeaderIdentifier(), salesman);
    reportMap.put(customer.getHeaderIdentifier(), customer);
    reportMap.put(sales.getHeaderIdentifier(), sales);
  }

  public void processFile(Path path) {
    init();

    log.info("Processing file {}", path.toString());
    long initial = System.currentTimeMillis();

    String processedData = processData(path);

    String fileName = path.getFileName().toString().substring(0, path.getFileName().toString().lastIndexOf("."));
    Path outPath = Path.of(publishPath, "/", String.format(FINAL_FILE_PATTERN, fileName));

    generateReport(outPath, processedData);

    long end = System.currentTimeMillis();

    log.info("File processed successfully in {} ms", end - initial);
  }

  private String processData(Path path) {
    try (FileInputStream inputStream = new FileInputStream(String.valueOf(path))) {
      Scanner sc = new Scanner(inputStream, UTF_8);

      while (sc.hasNextLine()) {
        String line = sc.nextLine();

        ReportGenerator reportGenerator = getReportGenerator(line);

        reportGenerator.processLine(line);
      }

      if (sc.ioException() != null) {
        throw sc.ioException();
      }
      inputStream.close();
      sc.close();

      return reportMap.values().stream()
          .map(ReportGenerator::report)
          .reduce("", (acc, it) -> acc + it + System.lineSeparator());

    } catch (IOException e) {
      e.printStackTrace();
    }
    throw new RuntimeException("Error processing file");
  }

  private ReportGenerator getReportGenerator(String line) {
    String reportIdentifier = line.substring(0, 3);

    return reportMap.get(reportIdentifier);
  }

  private void generateReport(Path path, String data) {
    try {
      File directory = new File(String.valueOf(path.getParent()));

      if (!directory.exists()) {
        directory.mkdir();
      }

      FileOutputStream outputStream = new FileOutputStream(String.valueOf(path));
      outputStream.write(data.getBytes());

      outputStream.flush();
      outputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
