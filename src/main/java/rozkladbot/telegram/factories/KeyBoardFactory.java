package rozkladbot.telegram.factories;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import rozkladbot.constants.AppConstants;
import rozkladbot.constants.BotButtons;
import rozkladbot.constants.BotMessageConstants;
import rozkladbot.constants.EmojiList;
import rozkladbot.entities.Faculty;
import rozkladbot.entities.Group;
import rozkladbot.entities.Institute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public final class KeyBoardFactory {
    public static InlineKeyboardMarkup getBackButton() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>() {{
            add(getCustomButtonAsList(BotButtons.BACK, BotButtons.BACK_DATA));
        }};
        return new InlineKeyboardMarkup(rows);
    }

    public static InlineKeyboardMarkup getCustomButton(String text, String data) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>() {{
            add(getCustomButtonAsList(text, data));
        }};
        return new InlineKeyboardMarkup(rows);
    }

    private static List<InlineKeyboardButton> getCustomButtonAsList(String text, String data) {
        return new ArrayList<>() {{
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(text);
            inlineKeyboardButton.setCallbackData(data);
            add(inlineKeyboardButton);
        }};
    }

    public static InlineKeyboardMarkup getYesOrNoInline() {
        InlineKeyboardButton yesButton = new InlineKeyboardButton();
        yesButton.setText(EmojiList.TRUE);
        yesButton.setCallbackData(BotButtons.YES_DATA);
        InlineKeyboardButton noButton = new InlineKeyboardButton();
        noButton.setText(EmojiList.FALSE);
        noButton.setCallbackData(BotButtons.NO_DATA);
        List<InlineKeyboardButton> row = new ArrayList<>() {{
            add(yesButton);
            add(noButton);
        }};
        List<List<InlineKeyboardButton>> rows = new ArrayList<>() {{
            add(row);
        }};
        return new InlineKeyboardMarkup(rows);
    }

    public static InlineKeyboardMarkup getInstitutesKeyboardInline(List<Institute> institutes, boolean isRegistered) {
        List<String> institutes1 = institutes.stream().sorted(Comparator.comparingLong(Institute::getId))
                .map(value -> String.valueOf(value.getId()))
                .toList();
        List<List<InlineKeyboardButton>> buttons =
                buildKeyBoardFromData(institutes1, 1, isRegistered);
        buttons.add(getLinkButton(AppConstants.DEVELOPER_TG_LINK, BotMessageConstants.DOES_NOT_CONTAIN_INSTITUTE));
        return new InlineKeyboardMarkup(buttons);
    }

    public static InlineKeyboardMarkup getFacultiesKeyboardInline(List<Faculty> institutes) {
        List<List<InlineKeyboardButton>> buttons =
                buildKeyBoardFromData(institutes.stream()
                        .sorted(Comparator.comparingLong(Faculty::getFacultyId))
                        .map(faculty -> String.valueOf(faculty.getFacultyId()))
                        .toList(), 1, false);
        buttons.add(getLinkButton(AppConstants.DEVELOPER_TG_LINK, BotMessageConstants.DOES_NOT_CONTAIN_FACULTY));
        buttons.add(getBackButton().getKeyboard().getFirst());
        return new InlineKeyboardMarkup(buttons);
    }

    public static InlineKeyboardMarkup getCourseKeyboardInline(List<String> courses, boolean isRegistered) {
        List<List<InlineKeyboardButton>> buttons =
                buildKeyBoardFromData(courses.stream().sorted().toList(), 1, isRegistered);
        buttons.add(getLinkButton(AppConstants.DEVELOPER_TG_LINK, BotMessageConstants.DOES_NOT_CONTAIN_COURSE));
        buttons.add(getBackButton().getKeyboard().getFirst());
        return new InlineKeyboardMarkup(buttons);
    }

    public static InlineKeyboardMarkup getGroupKeyboardInline(List<Group> groups, boolean isRegistered) {
        List<List<InlineKeyboardButton>> buttons =
                buildKeyBoardFromData(groups.stream().map(Group::getName).sorted().toList(), 4, isRegistered);
        buttons.add(getLinkButton(AppConstants.DEVELOPER_TG_LINK, BotMessageConstants.DOES_NOT_CONTAIN_GROUP));
        buttons.add(getBackButton().getKeyboard().getFirst());
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

    private static List<List<InlineKeyboardButton>> buildKeyBoardFromData(
            Collection<String> collection,
            int separator,
            boolean addBackToMenuButton
    ) {
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
        if (addBackToMenuButton) {
            buttons.add(getCustomButtonAsList(BotButtons.BACK_TO_MENU, BotButtons.BACK_TO_MENU_DATA));
        }
        return buttons;
    }

    public static InlineKeyboardMarkup getCommandsList() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton dayButton = new InlineKeyboardButton();
        dayButton.setText("Розклад на сьогодні");
        dayButton.setCallbackData("/day");
        row1.add(dayButton);
        buttons.add(row1);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton ndayButton = new InlineKeyboardButton();
        ndayButton.setText("Розклад на завтра");
        ndayButton.setCallbackData("/nextday");
        row2.add(ndayButton);
        buttons.add(row2);
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        InlineKeyboardButton weekButton = new InlineKeyboardButton();
        weekButton.setText("Розклад на тиждень");
        weekButton.setCallbackData("/week");
        row3.add(weekButton);
        buttons.add(row3);
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        InlineKeyboardButton nweekButton = new InlineKeyboardButton();
        nweekButton.setText("Розклад на наступний тиждень");
        nweekButton.setCallbackData("/nextweek");
        row4.add(nweekButton);
        buttons.add(row4);
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        InlineKeyboardButton customButton = new InlineKeyboardButton();
        customButton.setText("Розклад за власний проміжок часу");
        customButton.setCallbackData("/custom");
        row5.add(customButton);
        buttons.add(row5);
        List<InlineKeyboardButton> row6 = new ArrayList<>();
        InlineKeyboardButton settingsButton = new InlineKeyboardButton();
        settingsButton.setText("Налаштування");
        settingsButton.setCallbackData("/settings");
        row6.add(settingsButton);
        buttons.add(row6);
        return new InlineKeyboardMarkup(buttons);
    }

    public static InlineKeyboardMarkup getSettings(boolean isInBroadCast) {
        return new InlineKeyboardMarkup(new ArrayList<>() {{
            add(new ArrayList<>() {{
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(BotButtons.CHANGE_GROUP_BUTTON);
                inlineKeyboardButton.setCallbackData(BotButtons.CHANGE_GROUP_DATA);
                add(inlineKeyboardButton);
            }});
            add(new ArrayList<>() {{
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(isInBroadCast ? BotButtons.DISABLE_BROADCASTING : BotButtons.ENABLED_BROADCASTING);
                inlineKeyboardButton.setCallbackData(isInBroadCast ? BotButtons.DISABLED_DATA : BotButtons.ENABLED_DATA);
                add(inlineKeyboardButton);
            }});
            add(new ArrayList<>() {{
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(BotButtons.REPORT_BUG);
                inlineKeyboardButton.setUrl(AppConstants.DEVELOPER_TG_LINK);
                add(inlineKeyboardButton);
            }});
            add(new ArrayList<>() {{
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
                inlineKeyboardButton.setText(BotButtons.BACK);
                inlineKeyboardButton.setCallbackData(BotButtons.BACK_TO_MENU_DATA);
                add(inlineKeyboardButton);
            }});
        }});
    }

    private KeyBoardFactory() {
    }
}
