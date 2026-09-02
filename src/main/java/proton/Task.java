package proton;

/**
 * Represents a task and whether it has been completed.
 */
public class Task {
    /** Type icon used for tasks that do not have a specific type. */
    private static final String GENERIC_TYPE_ICON = "";
    /** Type icon used for ToDo tasks. */
    private static final String TODO_TYPE_ICON = "T";

    /** Description of this task. */
    private final String description;
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
        this(description, GENERIC_TYPE_ICON);
    }

    /**
     * Creates a task of the specified type that is initially not completed.
     *
     * @param description Description of the task.
     * @param typeIcon Icon identifying the type of the task.
     */
    private Task(String description, String typeIcon) {
        this.description = description;
        this.typeIcon = typeIcon;
        this.isDone = false;
    }

    /**
     * Creates a ToDo task that is initially not completed.
     *
     * @param description Description of the ToDo task.
     * @return A new ToDo task.
     */
    public static Task createTodo(String description) {
        return new Task(description, TODO_TYPE_ICON);
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
        return typeMarker + "[" + getStatusIcon() + "] " + description;
    }
}
