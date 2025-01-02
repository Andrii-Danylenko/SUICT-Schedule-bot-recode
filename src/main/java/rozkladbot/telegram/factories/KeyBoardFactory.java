package rozkladbot.telegram.factories;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import rozkladbot.entities.Faculty;
import rozkladbot.entities.Institute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class KeyBoardFactory {
    private static List<InlineKeyboardButton> getBackButtonAsList() {
        return new ArrayList<>() {{
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText("Назад");
            inlineKeyboardButton.setCallbackData("НАЗАД");
            add(inlineKeyboardButton);
        }};
    }

    public static InlineKeyboardMarkup getInstitutesKeyboardInline(List<Institute> institutes) {
        List<List<InlineKeyboardButton>> buttons =
                buildKeyBoardFromData(institutes.stream().map(Institute::getInstituteName).toList(), 1);
        buttons.add(getLinkButton("https://t.me/optionalOfNullable", "Немає твого інститута?"));
        return new InlineKeyboardMarkup(buttons);
    }
    public static InlineKeyboardMarkup getFacultiesKeyboardInline(List<Faculty> institutes) {
        List<List<InlineKeyboardButton>> buttons =
                buildKeyBoardFromData(institutes.stream().map(Faculty::getFacultyName).toList(), 1);
        buttons.add(getLinkButton("https://t.me/optionalOfNullable", "Немає твого факультета?"));
        return new InlineKeyboardMarkup(buttons);
    }
    private static List<InlineKeyboardButton> getLinkButton(String url, String text) {
        return new ArrayList<>() {{
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(text);
            inlineKeyboardButton.setUrl(url);
            add(inlineKeyboardButton);
        }};
    }

    private static List<List<InlineKeyboardButton>> buildKeyBoardFromData(Collection<String> collection, int separator) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
        int i = 0;
        for (String value : collection) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setCallbackData(value);
            inlineKeyboardButton.setText(value);
            keyboardRow.add(inlineKeyboardButton);
            if (++i % separator == 0) {
                buttons.add(keyboardRow);
                keyboardRow = new ArrayList<>();
            }
        }
        if (!keyboardRow.isEmpty()) {
            buttons.add(keyboardRow);
        }
        buttons.add(getBackButtonAsList());
        return buttons;
    }
    public static InlineKeyboardMarkup getCommandsList() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton dayButton = new InlineKeyboardButton();
        dayButton.setText("Розклад на сьогодні");
        dayButton.setCallbackData("DAY");
        row1.add(dayButton);
        buttons.add(row1);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton ndayButton = new InlineKeyboardButton();
        ndayButton.setText("Розклад на завтра");
        ndayButton.setCallbackData("NDAY");
        row2.add(ndayButton);
        buttons.add(row2);
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        InlineKeyboardButton weekButton = new InlineKeyboardButton();
        weekButton.setText("Розклад на тиждень");
        weekButton.setCallbackData("WEEK");
        row3.add(weekButton);
        buttons.add(row3);
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        InlineKeyboardButton nweekButton = new InlineKeyboardButton();
        nweekButton.setText("Розклад на наступний тиждень");
        nweekButton.setCallbackData("NWEEK");
        row4.add(nweekButton);
        buttons.add(row4);
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        InlineKeyboardButton customButton = new InlineKeyboardButton();
        customButton.setText("Розклад за власний проміжок часу");
        customButton.setCallbackData("CUSTOM");
        row5.add(customButton);
        buttons.add(row5);
        List<InlineKeyboardButton> row6 = new ArrayList<>();
        InlineKeyboardButton settingsButton = new InlineKeyboardButton();
        settingsButton.setText("Налаштування");
        settingsButton.setCallbackData("НЛ");
        row6.add(settingsButton);
        buttons.add(row6);
        return new InlineKeyboardMarkup(buttons);
    }
    private KeyBoardFactory() {
    }
}
