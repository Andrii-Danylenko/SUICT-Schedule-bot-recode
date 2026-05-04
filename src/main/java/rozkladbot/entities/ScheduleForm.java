package rozkladbot.entities;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScheduleForm {
    private Long institute;
    private Long faculty;
    private Long course;
    private Long group;

    public ScheduleForm(Long institute, Long faculty, Long course, Long group) {
        this.institute = institute;
        this.faculty = faculty;
        this.course = course;
        this.group = group;
    }

    public ScheduleForm() {
    }

  @Override
    public String toString() {
        return "ScheduleForm{" +
               "institute='" + institute + '\'' +
               ", faculty='" + faculty + '\'' +
               ", course='" + course + '\'' +
               ", group='" + group + '\'' +
               '}';
    }
}
