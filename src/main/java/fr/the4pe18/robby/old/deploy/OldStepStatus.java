package fr.the4pe18.robby.old.deploy;

/**
 * @author 4PE18
 * @deprecated
 */
public enum OldStepStatus {
    SUCCESS(":white_check_mark:", "SUCCESS!"),
    ERROR(":warning:", "ERROR"),
    FATAL_ERROR(":x:", "FATAL ERROR!"),
    RUNNING(":arrows_counterclockwise:", "RUNNING"),
    PENDING(":clock3:", "PENDING"),
    CANCELLED(":grey_exclamation:", "CANCELLED");


    private String emoji;
    private String statusText;

    OldStepStatus(String emoji, String statusText) {
        this.emoji = emoji;
        this.statusText = statusText;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getStatusText() {
        return statusText;
    }
}
