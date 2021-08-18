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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@JsonIgnoreProperties({"id", "timeLimit"})
public class CodeSnippet {

  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");

  @Id @GeneratedValue private long id = 0L;

  private final String uuid = UUID.randomUUID().toString();

  @ManyToOne
  @JoinColumn(name = "author_uuid")
  private Author author = null;

  @Column(columnDefinition = "text")
  private String code = "";

  private final LocalDateTime date = LocalDateTime.now();

  private long timeLimit = 0L;
  private LocalDateTime expiryDate = LocalDateTime.MIN;

  private long viewCount = 0L;
  private long viewLimit = 0L;


  public long getId() {
    return id;
  }

  public String getUuid() {
    return uuid;
  }

  public Author getAuthor() {
    return author;
  }

  public void setAuthor(Author author) {
    this.author = author;
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
    this.timeLimit = timeLimit;
    this.expiryDate = date.plusMinutes(timeLimit);
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
    this.viewLimit = viewLimit;
  }

  //@JsonIgnore
  public boolean isRestrictedByViews() {
    return 0 < viewLimit;
  }

  //@JsonIgnore
  public boolean isRestrictedByTime() {
    return expiryDate != LocalDateTime.MIN;
  }

  @JsonIgnore
  public boolean isAccessible() {
    return (viewLimit == 0 || viewCount < viewLimit)
        && (expiryDate == LocalDateTime.MIN || date.isBefore(expiryDate));
  }

  @JsonGetter("remainingSeconds")
  public long getRemainingSeconds() {
    if (expiryDate == LocalDateTime.MIN) {
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
