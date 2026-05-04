package rozkladbot.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.Objects;
import rozkladbot.utils.deserializers.LocalDateDeserializer;


@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Lesson {

  @JsonProperty("date")
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate date;
  @JsonProperty("number")
  private String pairNumber;
  @JsonProperty("type")
  private String type;
  @JsonProperty("cabinet")
  private String cabinet;
  @JsonProperty("shortName")
  private String lessonShortName;
  @JsonProperty("name")
  private String lessonFullName;
  @JsonProperty("who")
  private String lectorFullName;
  @JsonProperty("whoShort")
  private String lectorShortName;
  @JsonProperty("timeStart")
  private String timeStart;
  @JsonProperty("timeEnd")
  private String timeEnd;
  private String pairLink;

  public Lesson(
      LocalDate date,
      String pairNumber,
      String cabinet,
      String lessonShortName,
      String lessonFullName,
      String lectorFullName,
      String lectorShortName,
      String type,
      String timeStart,
      String timeEnd) {
    this.date = date;
    this.pairNumber = pairNumber;
    this.cabinet = cabinet;
    this.lessonShortName = lessonShortName;
    this.lessonFullName = lessonFullName;
    this.lectorFullName = lectorFullName;
    this.lectorShortName = lectorShortName;
    this.type = type;
    this.timeStart = timeStart;
    this.timeEnd = timeEnd;
  }

  @Override
  public String toString() {
    return "Lesson{" +
        "date=" + date +
        ", pairNumber=" + pairNumber +
        ", cabinet='" + cabinet + '\'' +
        ", lessonShortName='" + lessonShortName + '\'' +
        ", lessonFullName='" + lessonFullName + '\'' +
        ", lectorFullName='" + lectorFullName + '\'' +
        ", lectorShortName='" + lectorShortName + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Lesson lesson = (Lesson) o;
    return Objects.equals(date, lesson.date) && Objects.equals(pairNumber, lesson.pairNumber)
        && Objects.equals(type, lesson.type) && Objects.equals(cabinet, lesson.cabinet)
        && Objects.equals(lessonShortName, lesson.lessonShortName) && Objects.equals(lessonFullName,
        lesson.lessonFullName) && Objects.equals(lectorFullName, lesson.lectorFullName)
        && Objects.equals(lectorShortName, lesson.lectorShortName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(date, pairNumber, type, cabinet, lessonShortName, lessonFullName,
        lectorFullName, lectorShortName);
  }
}
