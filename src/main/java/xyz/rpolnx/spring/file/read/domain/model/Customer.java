package xyz.rpolnx.spring.file.read.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Customer {
  private String cnpj;
  private String name;
  private String businessArea;
}
