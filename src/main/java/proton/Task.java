package proton;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    /** Type icon used for deadline tasks. */
    private static final String DEADLINE_TYPE_ICON = "D";
    /** Type icon used for event tasks. */
    private static final String EVENT_TYPE_ICON = "E";
    /** Type icon used for tasks that do not have a specific type. */
    private static final String GENERIC_TYPE_ICON = "";
    /** Type icon used for ToDo tasks. */
    private static final String TODO_TYPE_ICON = "T";

    /** Description of this task. */
    private final String description;
    /** Date or time by which this task should be completed, if any. */
    private final String dueDateTime;
    /** Date or time at which this task starts, if any. */
    private final String startDateTime;
    /** Date or time at which this task ends, if any. */
    private final String endDateTime;
    /** Icon identifying the type of this task. */
    private final String typeIcon;
    /** Whether this task has been completed. */
    private boolean isDone;

    /**
     * Creates a task that is initially not completed.
     *
     * @param description Description of the task.
     */
    public Task(String description) {
        this(description, GENERIC_TYPE_ICON, "", "", "");
    }

    /**
     * Creates a task of the specified type that is initially not completed.
     *
     * @param description Description of the task.
     * @param typeIcon Icon identifying the type of the task.
     * @param dueDateTime Date or time by which the task should be completed.
     * @param startDateTime Date or time at which the task starts.
     * @param endDateTime Date or time at which the task ends.
     */
    private Task(String description, String typeIcon, String dueDateTime,
            String startDateTime, String endDateTime) {
        this.description = description;
        this.typeIcon = typeIcon;
        this.dueDateTime = dueDateTime;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.isDone = false;
    }

    /**
     * Creates a ToDo task that is initially not completed.
     *
     * @param description Description of the ToDo task.
     * @return A new ToDo task.
     */
    public static Task createTodo(String description) {
        return new Task(description, TODO_TYPE_ICON, "", "", "");
    }

    /**
     * Creates a deadline task that is initially not completed.
     *
     * @param description Description of the deadline task.
     * @param dueDateTime Date or time by which the task should be completed.
     * @return A new deadline task.
     */
    public static Task createDeadline(String description, String dueDateTime) {
        return new Task(description, DEADLINE_TYPE_ICON, dueDateTime, "", "");
    }

    /**
     * Creates an event task that is initially not completed.
     *
     * @param description Description of the event task.
     * @param startDateTime Date or time at which the event starts.
     * @param endDateTime Date or time at which the event ends.
     * @return A new event task.
     */
    public static Task createEvent(String description, String startDateTime, String endDateTime) {
        return new Task(description, EVENT_TYPE_ICON, "", startDateTime, endDateTime);
    }

    /**
     * Returns the icon that represents the completion status of this task.
     *
     * @return {@code "X"} when completed, or a space otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as not completed.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    @Override
    public String toString() {
        String typeMarker = typeIcon.isEmpty() ? "" : "[" + typeIcon + "]";
        String deadlineDetails = dueDateTime.isEmpty() ? "" : " (by: " + dueDateTime + ")";
        String eventDetails = startDateTime.isEmpty()
                ? ""
                : " (from: " + startDateTime + " to: " + endDateTime + ")";
        return typeMarker + "[" + getStatusIcon() + "] " + description + deadlineDetails + eventDetails;
    }
}
