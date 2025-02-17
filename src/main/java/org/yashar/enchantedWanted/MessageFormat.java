package org.yashar.enchantedWanted;

public class MessageFormat {
    public static final String ERROR_PERMISSION = "<red>You don't have permission!";
    public static final String ERROR_PLAYER_ONLY = "<red>This command is for players only!";
    public static final String ERROR_PLAYER_NOT_FOUND = "<red>Player not found!";
    public static final String ERROR_NUMBER_FORMAT = "<red>Invalid number format!";
    public static final String ERROR_INVALID_RANGE = "<red>Value must be between 0-1000!";
    public static final String ERROR_INVALID_ADD_RANGE = "<red>Value must be 1-500!";
    public static final String ERROR_USAGE = "<yellow>Usage: %usage%";
    public static final String WARNING_NO_WANTED = "<yellow>%player% has no wanted points!";
    public static final String SUCCESS_CLEAR = "<green>Cleared %player%'s wanted points!";
    public static final String SUCCESS_SET = "<green>Set %player%'s wanted to %value%!";
    public static final String SUCCESS_ADD = "<green>Added %value% wanted points to %player%!";
    public static final String INFO_WANTED_STATUS = "<gold>%player% <yellow>has <gold>%value% <yellow>wanted points";
    public static final String ERROR_NO_WANTED_ONLINE = "<red>No wanted players online!";
    public static final String SUCCESS_GPS_START = "<green>Tracking %player% (<gold>%distance%m<green>)";
    public static final String SUCCESS_GPS_STOP = "<green>GPS tracking stopped!";
    public static final String WARNING_NO_GPS_ACTIVE = "<yellow>No active GPS tracking!";
    public static final String ERROR_NO_PLAYER_IN_SIGHT = "<red>No player in sight!";
    public static final String ERROR_INSUFFICIENT_WANTED = "<red>Target needs at least 100 wanted points!";
    public static final String SUCCESS_RELOAD = "<green>Plugin reloaded successfully!";
    public static final String SUCCESS_ALERTS_ENABLED = "<green>Police alerts enabled!";
    public static final String SUCCESS_ALERTS_DISABLED = "<red>Police alerts disabled!";

    public static final String POLICE_CLEAR_ALERT = "<dark_aqua>[Police] <aqua>%player% cleared by %officer%";
    public static final String POLICE_SET_ALERT = "<dark_aqua>[Police] <aqua>%player% set to %value% by %officer%";
    public static final String POLICE_ADD_ALERT = "<dark_aqua>[Police] <aqua>%player% +%value% by %officer%";
    public static final String POLICE_ARREST_ALERT = "<dark_aqua>[Police] <aqua>%player% arrested by %officer%!";
    public static final String POLICE_JOIN_DUTY = "<dark_aqua>[Police] <aqua>%player% is now on duty!";

    public static final String HEADER_WANTED_COMMANDS = "<dark_gray>» <gold>Wanted Commands <dark_gray>«";
    public static final String HELP_TOP = "<yellow>/wanted top <gray>- Show wanted leaderboard";
    public static final String HELP_CLEAR = "<yellow>/wanted clear <player> <gray>- Clear wanted points";
    public static final String HELP_SET = "<yellow>/wanted set <player> <value> <gray>- Set wanted points";
    public static final String HELP_ADD = "<yellow>/wanted add <player> <value> <gray>- Add wanted points";
    public static final String HELP_FIND = "<yellow>/wanted find <player> <gray>- Check wanted status";
    public static final String HELP_GPS = "<yellow>/wanted gps <gray>- Track nearest wanted";
    public static final String HELP_GPS_STOP = "<yellow>/wanted gpsstop <gray>- Stop GPS tracking";
    public static final String HELP_ARREST = "<yellow>/wanted arrest <gray>- Arrest a wanted player";
    public static final String HELP_RELOAD = "<yellow>/wanted reload <gray>- Reload plugin config";
}