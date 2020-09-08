package xyz.rpolnx.spring.file.read.domain.reports;

public abstract class ReportGenerator {
  protected final String DATA_SEPARATOR = "ç";

  public void processLine(String data) {
    String pattern = getPattern();
    //if not match throw exception

    String[] dataPart = data.split(DATA_SEPARATOR);

    this.extractData(dataPart);
  }

  public abstract void extractData(String[] data);

  public abstract String report();

  public abstract String getPattern();

  public abstract String getHeaderIdentifier();
}
