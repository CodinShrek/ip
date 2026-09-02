package proton;

/**
 * Represents a task that should be completed by a specific date or time.
 */
public class Deadline extends Task {
    /** Date or time by which this task should be completed. */
    private final String dueDateTime;

    /**
     * Creates a deadline task that is initially not completed.
     *
     * @param description Description of the deadline task.
     * @param dueDateTime Date or time by which the task should be completed.
     */
    public Deadline(String description, String dueDateTime) {
        super(description);
        this.dueDateTime = dueDateTime;
    }

    /**
     * Returns this task with its deadline type marker and due date or time.
     *
     * @return The formatted deadline task.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + dueDateTime + ")";
    }
}
