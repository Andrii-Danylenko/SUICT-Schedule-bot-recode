package rozkladbot.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import rozkladbot.utils.deserializers.LocalDateDeserializer;

import java.time.LocalDate;
import java.util.Objects;


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

    public Lesson(
            LocalDate date,
            String pairNumber,
            String cabinet,
            String lessonShortName,
            String lessonFullName,
            String lectorFullName,
            String lectorShortName,
            String type) {
        this.date = date;
        this.pairNumber = pairNumber;
        this.cabinet = cabinet;
        this.lessonShortName = lessonShortName;
        this.lessonFullName = lessonFullName;
        this.lectorFullName = lectorFullName;
        this.lectorShortName = lectorShortName;
        this.type = type;
    }

    public Lesson() {
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

    public LocalDate getDate() {
        return date;
    }

    public String getPairNumber() {
        return pairNumber;
    }

    public String getCabinet() {
        return cabinet;
    }

    public String getLessonShortName() {
        return lessonShortName;
    }

    public String getLessonFullName() {
        return lessonFullName;
    }

    public String getLectorFullName() {
        return lectorFullName;
    }

    public String getLectorShortName() {
        return lectorShortName;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setPairNumber(String pairNumber) {
        this.pairNumber = pairNumber;
    }

    public void setCabinet(String cabinet) {
        this.cabinet = cabinet;
    }

    public void setLessonShortName(String lessonShortName) {
        this.lessonShortName = lessonShortName;
    }

    public void setLessonFullName(String lessonFullName) {
        this.lessonFullName = lessonFullName;
    }

    public void setLectorFullName(String lectorFullName) {
        this.lectorFullName = lectorFullName;
    }

    public void setLectorShortName(String lectorShortName) {
        this.lectorShortName = lectorShortName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public static String getPairTime(String pairNumber) {
        return switch (pairNumber) {
            case "1" -> "8:00-9:35";
            case "2" -> "9:45-11:20";
            case "3" -> "11:45-13:20";
            case "4" -> "13:30-15:05";
            case "5" -> "15:15-16:50";
            case "6" -> "17:00-18:35";
            default -> "18:45-20:15";
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return Objects.equals(date, lesson.date) && Objects.equals(pairNumber, lesson.pairNumber) && Objects.equals(type, lesson.type) && Objects.equals(cabinet, lesson.cabinet) && Objects.equals(lessonShortName, lesson.lessonShortName) && Objects.equals(lessonFullName, lesson.lessonFullName) && Objects.equals(lectorFullName, lesson.lectorFullName) && Objects.equals(lectorShortName, lesson.lectorShortName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, pairNumber, type, cabinet, lessonShortName, lessonFullName, lectorFullName, lectorShortName);
    }
}
