package be.bendem.chatformatter;

import org.bukkit.ChatColor;

/**
 * Chat formatter
 *
 * @author bendem
 */
public class ChatFormatter {

    public static String format(String str, ChatColor format) {
        return format(str, format, true);
    }
    public static String format(String str, ChatColor format, Boolean reset) {
        StringBuilder finalString = new StringBuilder().append(ChatColor.COLOR_CHAR).append(format.getChar()).append(str);
        if(reset) {
            finalString.append(ChatColor.COLOR_CHAR).append(ChatColor.RESET.getChar());
        }
        return finalString.toString();
    }

    public static String stripColors(String str) {
        return ChatColor.stripColor(str);
    }

    public static String reset(String str) {
        return format(str, ChatColor.RESET, false);
    }
    public static String aqua(String str) {
        return format(str, ChatColor.AQUA);
    }
    public static String black(String str) {
        return format(str, ChatColor.BLACK);
    }
    public static String blue(String str) {
        return format(str, ChatColor.BLUE);
    }
    public static String bold(String str) {
        return format(str, ChatColor.BOLD);
    }
    public static String darkAqua(String str) {
        return format(str, ChatColor.DARK_AQUA);
    }
    public static String darkBlue(String str) {
        return format(str, ChatColor.DARK_BLUE);
    }
    public static String darkGray(String str) {
        return format(str, ChatColor.DARK_GRAY);
    }
    public static String darkGreen(String str) {
        return format(str, ChatColor.DARK_GREEN);
    }
    public static String darkPurple(String str) {
        return format(str, ChatColor.DARK_PURPLE);
    }
    public static String darkRed(String str) {
        return format(str, ChatColor.DARK_RED);
    }
    public static String gold(String str) {
        return format(str, ChatColor.GOLD);
    }
    public static String gray(String str) {
        return format(str, ChatColor.GRAY);
    }
    public static String green(String str) {
        return format(str, ChatColor.GREEN);
    }
    public static String italic(String str) {
        return format(str, ChatColor.ITALIC);
    }
    public static String lightPurple(String str) {
        return format(str, ChatColor.LIGHT_PURPLE);
    }
    public static String magic(String str) {
        return format(str, ChatColor.MAGIC);
    }
    public static String red(String str) {
        return format(str, ChatColor.RED);
    }
    public static String strikeThrough(String str) {
        return format(str, ChatColor.STRIKETHROUGH);
    }
    public static String underline(String str) {
        return format(str, ChatColor.UNDERLINE);
    }
    public static String white(String str) {
        return format(str, ChatColor.WHITE);
    }
    public static String yellow(String str) {
        return format(str, ChatColor.YELLOW);
    }

}
