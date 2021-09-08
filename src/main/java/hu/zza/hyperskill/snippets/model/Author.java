package hu.zza.hyperskill.snippets.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
@JsonIgnoreProperties({"id"})
public class Author {
  @Transient
  public static final Author UNKNOWN = new Author();
  static {
    UNKNOWN.name = "Unknown";
  }

  @Id @GeneratedValue private long id = 0L;

  private final String uuid = UUID.randomUUID().toString();
  private String name = "";
  private String email = "";
  private String passwordHash = "";
  private String personal = "";
  private String github = "";
  private String linkedin = "";

  public long getId() {
    return id;
  }

  public String getUuid() {
    return uuid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean checkPassword(String password) {
    return getHexStringFromByteArray(getPasswordHashArray(password)).equals(passwordHash);
  }

  public void setPassword(String password) {
    this.passwordHash = getHexStringFromByteArray(getPasswordHashArray(password));
  }

  // An ugly fix to prepend there is a password field.
  public String getPassword() {
    return "";
  }

  public String getPersonal() {
    return personal;
  }

  public void setPersonal(String personal) {
    this.personal = personal;
  }

  public String getGithub() {
    return github;
  }

  public void setGithub(String github) {
    this.github = github;
  }

  public String getLinkedin() {
    return linkedin;
  }

  public void setLinkedin(String linkedin) {
    this.linkedin = linkedin;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Author author = (Author) o;

    return uuid.equals(author.uuid);
  }

  @Override
  public int hashCode() {
    return uuid.hashCode();
  }

  private String getHexStringFromByteArray(byte[] array) {
    return new BigInteger(1, array).toString(16);
  }

  private byte[] getPasswordHashArray(String password) {
    try {
      return MessageDigest.getInstance("SHA3-512").digest(password.getBytes(StandardCharsets.UTF_8));
    } catch (NoSuchAlgorithmException ignored) {
      return new byte[0];
    }
  }
}
