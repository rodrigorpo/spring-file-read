package xyz.rpolnx.spring.file.read.domain.reports;

import lombok.Getter;
import xyz.rpolnx.spring.file.read.domain.model.Customer;

@Getter
public class CustomerReportGenerator extends ReportGenerator {
  private Long numberOfClients = 0L;

  @Override
  public void extractData(String[] data) {
    Customer customer = new Customer(data[1], data[2], data[3]); // treat if needed

    numberOfClients++;
  }

  @Override
  public String report() {
    return String.format("CUSTOMERS: %d", numberOfClients);
  }

  @Override
  public String getPattern() {
    return "002çCNPJçNameçBusiness Area";
  }

  @Override
  public String getHeaderIdentifier() {
    return "002";
  }

  public void addClient() {
    this.numberOfClients++;
  }
}
