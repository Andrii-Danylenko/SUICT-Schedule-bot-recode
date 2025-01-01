package rozkladbot.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import rozkladbot.utils.deserializers.LocalDateDeserializer;

import java.time.LocalDate;


public class Lesson {
    @JsonProperty("date")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate date;
    @JsonProperty("number")
    private String pairNumber;
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
            String lectorShortName) {
        this.date = date;
        this.pairNumber = pairNumber;
        this.cabinet = cabinet;
        this.lessonShortName = lessonShortName;
        this.lessonFullName = lessonFullName;
        this.lectorFullName = lectorFullName;
        this.lectorShortName = lectorShortName;
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
}
