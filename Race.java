public class Race {
    private static int[][] point_leaderboard;
    private static int id;
    private static int[][] mountainCheckpointValues;
    private static Stage[] stages;
    private static Checkpoint[] checkpoints;
    private static Rider[] riders;
    private static int generalClassification;
    private static int sprinterClassification;
    private static int mountainClassification;
    private static String name;
    private static String description;

    public Race(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}

