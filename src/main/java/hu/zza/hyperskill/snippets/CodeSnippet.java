package hu.zza.hyperskill.snippets;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@JsonIgnoreProperties({"id", "timeLimit"})
public class CodeSnippet {
  private static final LocalDateTime NO_EXPIRY =
      LocalDateTime.of(2000,1,1,0,0, 0, 0);

  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");

  @Id @GeneratedValue private long id = 0L;

  private final String uuid = UUID.randomUUID().toString();

  private String authorUuid = "-";

  @Column(columnDefinition = "text")
  private String code = "";

  private final LocalDateTime date = LocalDateTime.now();

  private long timeLimit = 0L;
  private LocalDateTime expiryDate = NO_EXPIRY;

  private long viewCount = 0L;
  private long viewLimit = 0L;


  public long getId() {
    return id;
  }

  public String getUuid() {
    return uuid;
  }

  public String getAuthorUuid() {
    return authorUuid;
  }

  public void setAuthorUuid(String authorUuid) {
    this.authorUuid = authorUuid;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDate() {
    return date.format(FORMATTER);
  }

  public long getTimeLimit() {
    return timeLimit;
  }

  public void setTimeLimit(long timeLimit) {
    if (0 < timeLimit) {
      this.timeLimit = timeLimit;
      this.expiryDate = date.plusMinutes(timeLimit);
    }
  }

  public LocalDateTime getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(LocalDateTime expiryDate) {
    this.expiryDate = expiryDate;
  }

  public long getViewCount() {
    return viewCount;
  }

  public void setViewCount(long viewCount) {
    this.viewCount = viewCount;
  }

  public void increaseViewCount() {
    viewCount++;
  }

  public long getViewLimit() {
    return viewLimit;
  }

  public void setViewLimit(long viewLimit) {
    if (0 < viewLimit) {
      this.viewLimit = viewLimit;
    }
  }

  //@JsonIgnore
  public boolean isRestrictedByViews() {
    return 0 < viewLimit;
  }

  //@JsonIgnore
  public boolean isRestrictedByTime() {
    return 0 < timeLimit;
  }

  @JsonIgnore
  public boolean isAccessible() {
    return (viewLimit == 0 || viewCount < viewLimit)
        && (timeLimit == 0 || date.isBefore(expiryDate));
  }

  @JsonGetter("remainingSeconds")
  public long getRemainingSeconds() {
    if (0 < timeLimit) {
      return 0;
    }
    return date.isBefore(expiryDate) ? Duration.between(date, expiryDate).getSeconds() : 0;
  }

  @JsonGetter("remainingViews")
  public long getRemainingViews() {
    if (isRestrictedByTime()) {
      return Math.max(viewLimit - viewCount, 0);
    }
    return 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CodeSnippet that = (CodeSnippet) o;

    return uuid.equals(that.uuid);
  }

  @Override
  public int hashCode() {
    return uuid.hashCode();
  }
}
