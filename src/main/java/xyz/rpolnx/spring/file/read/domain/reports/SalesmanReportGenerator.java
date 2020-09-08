package xyz.rpolnx.spring.file.read.domain.reports;

import xyz.rpolnx.spring.file.read.domain.model.Salesman;

import java.math.BigDecimal;

public class SalesmanReportGenerator extends ReportGenerator {
  private Long numberOfSalesman = 0L;

  @Override
  public void extractData(String[] data) {
    Salesman customer = new Salesman(data[1], data[2], new BigDecimal(data[3])); // treat if needed

    numberOfSalesman++;
  }

  @Override
  public String report() {
    return String.format("SALESMAN: %d", numberOfSalesman);
  }

  @Override
  public String getPattern() {
    return "001çCPFçNameçSalary";
  }

  @Override
  public String getHeaderIdentifier() {
    return "001";
  }
}
