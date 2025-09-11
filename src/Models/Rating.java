package Models;

public class Rating {
    private enum StarValue {
        WORST,
        BAD,
        AVERAGE,
        GOOD,
        GREAT
    }
    private MediaEntry mediaEntry;
    private User user;
}
