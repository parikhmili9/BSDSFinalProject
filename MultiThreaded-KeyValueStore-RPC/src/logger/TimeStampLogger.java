package logger;

import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeStampLogger implements Logger {

  private final Writer log;

  public TimeStampLogger(Writer log) {
    if(log == null) {
      throw new IllegalArgumentException("The appendable is null");
    }
    this.log = log;
  }

  @Override
  public void log(String msg) {
    if(msg == null) {
      throw new IllegalArgumentException("The message is null");
    }
    try {
      this.log.append(String.format("%s --> %s%s", timeStamp(), msg, System.lineSeparator()));
      this.log.flush();
    } catch (IOException ioe) {
      throw new IllegalArgumentException("The logger encountered I/O issue.");
    }
  }

  private String timeStamp() {
    DateFormat dateFormatter = new SimpleDateFormat("dd MM yyyy HH:mm:ss:SSS Z");
    long millis = System.currentTimeMillis();
    Date date = new Date(millis);
    return dateFormatter.format(date);
  }
}
