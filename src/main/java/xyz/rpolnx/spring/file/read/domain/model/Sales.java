package xyz.rpolnx.spring.file.read.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Sales {
  private String id;
  private String salesmanName;
  private List<Item> items;

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  public static class Item {
    private String id;
    private Long quantity;
    private BigDecimal price;
  }
}
