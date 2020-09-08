package xyz.rpolnx.spring.file.read.domain.reports;

import lombok.AllArgsConstructor;
import xyz.rpolnx.spring.file.read.domain.model.Sale;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class SalesReportGenerator extends ReportGenerator {
  private ExpansiveSale expansiveSale;
  private WorstSalesman worstSalesman;
  private final Map<String, List<Sale>> salesmanSales = new HashMap<>();

  @AllArgsConstructor
  private static class ExpansiveSale {
    private final String id;
    private final BigDecimal totalValue;
  }

  @AllArgsConstructor
  private static class WorstSalesman {
    private final String name;
    private final BigDecimal soldValue;
  }

  @Override
  public void extractData(String[] data) {
    String rawItems = data[2].replaceAll("[\\[\\]]", ""); // exclude [ and ]

    List<Sale.Item> items =
        Arrays.stream(rawItems.split(","))
            .map(this::buildItemFromRawData)
            .collect(Collectors.toList());

    String salesmanName = data[3];
    String salesId = data[1];
    Sale currentSale = new Sale(salesId, salesmanName, items);

    Optional.ofNullable(salesmanSales.get(salesmanName))
        .ifPresentOrElse(
            it -> it.add(currentSale), this.initializeSaleListWithValue(salesmanName, currentSale));
  }

  @Override
  public String report() {
    processExtractedData();
    // performance on string pool
    //noinspection StringBufferReplaceableByString
    return new StringBuilder()
        .append(
            String.format(
                "EXPANSIVE SALE'S ID: %s with value %s", expansiveSale.id, expansiveSale.totalValue))
        .append(System.lineSeparator())
        .append(
            String.format(
                "WORST SALESMAN'S NAME: %s with value %s",
                worstSalesman.name, worstSalesman.soldValue))
        .toString();
  }

  @Override
  public String getPattern() {
    return "003çSale IDç[Item ID-Item Quantity-Item Price]çSalesman name";
  }

  @Override
  public String getHeaderIdentifier() {
    return "003";
  }

  private Sale.Item buildItemFromRawData(String itemData) {
    String[] attributes = itemData.split("-");
    return new Sale.Item(attributes[0], Long.valueOf(attributes[1]), new BigDecimal(attributes[2]));
  }

  private Runnable initializeSaleListWithValue(String salesmanName, Sale sale) {
    return () -> {
      ArrayList<Sale> sales = new ArrayList<>();
      sales.add(sale);
      salesmanSales.put(salesmanName, sales);
    };
  }

  private void processExtractedData() {
    for (Map.Entry<String, List<Sale>> salesmanSale : salesmanSales.entrySet()) {

      BigDecimal salesmanSoldValue = BigDecimal.ZERO;

      for (Sale currentSale : salesmanSale.getValue()) {
        BigDecimal currentSaleValue = currentSale.getTotalPrice();

        if (isNull(expansiveSale) || isCurrentSaleMoreExpensive(currentSaleValue)) {
          expansiveSale = new ExpansiveSale(currentSale.getId(), currentSaleValue);
        }

        salesmanSoldValue = salesmanSoldValue.add(currentSaleValue);
      }

      if (isNull(worstSalesman) || isCurrentSalesmanWorseThanOthers(salesmanSoldValue)) {
        worstSalesman = new WorstSalesman(salesmanSale.getKey(), salesmanSoldValue);
      }
    }
  }

  private boolean isCurrentSaleMoreExpensive(BigDecimal currentSaleValue) {
    return currentSaleValue.compareTo(expansiveSale.totalValue) > 0;
  }

  private boolean isCurrentSalesmanWorseThanOthers(BigDecimal salesmanSoldValue) {
    return worstSalesman.soldValue.compareTo(salesmanSoldValue) > 0;
  }
}
