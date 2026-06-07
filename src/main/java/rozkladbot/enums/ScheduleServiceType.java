package rozkladbot.enums;

import java.util.Arrays;
import java.util.Set;
import rozkladbot.exceptions.UnsupportedScheduleServiceTypeException;

public enum ScheduleServiceType {
  SKEDY(
      Set.of(
          "Державний університет інформаційно-комунікаційних технологій",
          "Київський національний університет будівництва i архітектури",
          "Державний торговельно-економічний університет",
          "Національний медичний університет імені О. О. Богомольця",
          "Харківський національний університет міського господарства імені О.М. Бекетова",
          "Кременчуцький національний університет імені Михайла Остроградського",
          "Українська інженерно-педагогічна академія",
          "Національний фармацевтичний університет",
          "Сумський національний аграрний університет",
          "Національний університет цивільного захисту України",
          "Національна академія внутрішніх справ",
          "Харківський національний медичний університет",
          "Тернопільський національний медичний університет",
          "Університет імені Альфреда Нобеля",
          "Харківський національний університет внутрішніх справ",
          "Харківський національний автомобільно-дорожній університет",
          "Дніпропетровський державний університет внутрішніх справ"
      )
  );

  public static ScheduleServiceType scheduleServiceFromInstituteName(String instituteName) {
    return Arrays.stream(ScheduleServiceType.values()).filter(
        value -> value.institutions.contains(instituteName)
    ).findAny().orElseThrow(() -> new UnsupportedScheduleServiceTypeException(
        "Service type which supports institute %s does not exist".formatted(instituteName)
    ));
  }

  private final Set<String> institutions;

  ScheduleServiceType(Set<String> institutions) {
    this.institutions = institutions;
  }
}
