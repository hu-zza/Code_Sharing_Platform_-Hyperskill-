package hu.zza.hyperskill.snippets;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@JsonIgnoreProperties({"id", "uuid", "viewCount"})
public class CodeSnippet {

  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");

  // Not for JSON...
  @Id @GeneratedValue private long id = 0L;
  private String uuid = UUID.randomUUID().toString();
  private long viewCount = 0L;

  // JSON attributes
  private String code = "";
  private LocalDateTime date = LocalDateTime.now();

  // JSON attributes, but getters provide the value
  private long time = 0L;
  private long views = 0L;

  // GENERATED GETTERS

  @JsonGetter("time")
  public long getTime() {
    if (time == 0) {
      return 0;
    }
    long elapsedSeconds = Duration.between(date, LocalDateTime.now()).getSeconds();
    return time > elapsedSeconds ? time - elapsedSeconds : 0;
  }

  @JsonGetter("views")
  public long getViews() {
    if (views == 0) {
      return 0;
    }
    return views > viewCount ? views - viewCount : 0;
  }

  @JsonIgnore
  public boolean isAccessible() {
    long elapsedSeconds = Duration.between(date, LocalDateTime.now()).getSeconds();

    return (views == 0 || views > viewCount) && (time == 0 || time > elapsedSeconds);
  }

  @JsonIgnore
  public boolean isRestrictedByViews() {
    return 0 < views;
  }

  @JsonIgnore
  public boolean isRestrictedByTime() {
    return 0 < time;
  }

  // USUAL GETTERS

  public long getId() {
    return id;
  }

  public String getUuid() {
    return uuid;
  }

  public String getCode() {
    return code;
  }

  public String getDate() {
    return date.format(FORMATTER);
  }

  // INSTANCE METHODS

  public void increaseViewCount() {
    viewCount += 1;
  }
}
