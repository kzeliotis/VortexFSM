package dc.gtest.vortex.api;

public abstract class PriorityTask
        implements Runnable, Comparable<PriorityTask> {

    public static final int HIGH   = 0;
    public static final int NORMAL = 1;
    public static final int LOW    = 2;

    private final int priority;

    protected PriorityTask(int priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(PriorityTask other) {
        return Integer.compare(this.priority, other.priority);
    }
}
