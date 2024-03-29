package cycling;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalTime;
import java.io.Serializable;
/**
 * Race stores information regarding a race
 * such as stages and the properties associated with it
 * 
 * @author Jake Klar
 * @author Tamanna Kar
 * @version 2.0
 *
 */
public class Race implements Serializable{
    /**
     * The unique id of a race
     */
    private int id;
    /**
     * The list of rider ids for a race
     */
    private ArrayList<Rider> riders;
    /**
     * The name of a race
     */
    private String name;
    /**
     * The description of a race
     */
    private String description;
    /**
     * A map with stageIds as keys and stage objects as values
     */
    private Map<Integer, Stage> stages;
    /**
     * A list of the stageIds by order of creation
     */
    private ArrayList<Integer> orderedStageIds;
    /**
     * A map with elapsed durations as keys and riderIds as values
     */
    private Map<LocalTime, Integer> adjustedTimes;
    /**
     * A map with riderIds as keys and sprint points as values
     */
    private Map<Integer, Integer> sprinterPoints;
    /**
     * A map with riderIds as keys and mountain points as values
     */
    private Map<Integer, Integer> mountainPoints;
    /**
     * A map with sprint points as keys and riderIds as values
     */
    private Map<Integer, Integer> sprinterClassification;
    /**
     * A map with mountain points as keys and mountainPoints as values
     */
    private Map<Integer, Integer> mountainClassification;

    /**
     * Constructs an empty temporary race
     */
    public Race() {

    }
    /**
     * Constructs a new race with the provided parameters. It will
     * also generate a unique random ID for the race.
     * @param name The name of the race
     * @param description The description of the race
     * @param races All of the races currently in CyclingPortalImpl
     */
    public Race(String name, String description, Map<Integer, Race> races) {
        int id = 0;
		boolean used = true;
		while(used) {
            id = (int)(Math.random() * 9000) + 1000;
            if(!races.containsKey(id)) {
                used = false;
            }
        }
        this.name = name;
        this.description = description;
        this.id = id;
        this.stages = new HashMap<Integer, Stage>();
        this.adjustedTimes = new HashMap<LocalTime, Integer>();
        this.sprinterPoints = new HashMap<Integer, Integer>();
        this.mountainPoints = new HashMap<Integer, Integer>();
        this.sprinterClassification = new HashMap<Integer, Integer>();
        this.mountainClassification = new HashMap<Integer, Integer>();
        this.orderedStageIds = new ArrayList<Integer>();
        this.riders = new ArrayList<Rider>();
    }
    /**
     * Gets the id of a race
     * @return The id of this race.
     */
    public int getId() {
        return this.id;
    }
    /**
     * Gets the name of a race
     * @return The name of this race.
     */
    public String getName() {
        return this.name;
    }
    /**
     * Gets the description of a race
     * @return The description of this race
     */
    public String getDescription() {
        return this.description;
    }
    /**
     * Gets the sprinter classification map of a race
     * @return The sprinter classfication map of this race
     */
    public Map<Integer, Integer> getSprinterClassification() {
        return this.sprinterClassification;
    }
    /**
     * Gets the mountain calssification map of a race
     * @return The mountain classifcaiton map of this race
     */
    public Map<Integer, Integer> getMountainClassification() {
        return this.mountainClassification;
    }
    /**
     * Adds an adjusted race finish time for a rider
     * @param time The time they finished in
     * @param riderId The id of the rider
     */
    public void addAdjustedTime(LocalTime time, int riderId) {
        this.adjustedTimes.put(time, riderId);
    }
    /**
     * Gets the adjusted times of a race
     * @return The adjusted times of this race
     */
    public Map<LocalTime, Integer> getAdjustedTimes() {
        return this.adjustedTimes;
    }
    /**
     * Gets the mountain points for every rider in a race
     * @return The mountain points for every rider in this race
     */
    public Map<Integer, Integer> getMountainPoints() {
        //Initalizing each riders points at 0 to avoid null access errors
        for(int i=0; i<riders.size(); i++) {
            mountainPoints.put(riders.get(i).getId(), 0);
            mountainClassification.put(0, riders.get(i).getId());
        }
        //Adding the points for each rider for each stage in the race
        for(int i=0; i<stages.size(); i++) {
            for(int j=0; j<riders.size(); j++) {
                mountainPoints.put(riders.get(j).getId(), mountainPoints.get(j)
                 + stages.get(i).getRiderMountainPoints(riders.get(j).getId()));
                mountainClassification.put(mountainPoints.get(j), riders.get(j).getId());
            }
        }
        return this.mountainPoints;
    }
    /**
     * Gets the sprinter points for every rider in a race
     * @return The sprinter points for every rider in this race
     */
    public Map<Integer, Integer> getSprinterPoints() {
        //Initalizing each riders points at 0 to avoid null access errors
        for(int i=0; i<riders.size(); i++) {
            sprinterPoints.put(riders.get(i).getId(), 0);
            sprinterClassification.put(0, riders.get(i).getId());
        }
        //Adding the points for each rider for each stage in the race
        for(int i=0; i<stages.size(); i++) {
            for(int j=0; j<riders.size(); j++) {
                sprinterPoints.put(riders.get(j).getId(), sprinterPoints.get(j)
                 + stages.get(i).getRiderSprinterPoints(riders.get(j).getId()));
                sprinterClassification.put(sprinterPoints.get(j), riders.get(j).getId());
            }
        }
        return this.sprinterPoints;
    }
    /**
     * Creates an array of the sprinter points for a race sorted
     * @return The array of the sprinter points for this race sorted
     */
    public ArrayList<Integer> sprinterPointsSorted() {
        ArrayList<Integer> sortedPoints = new ArrayList<>(this.sprinterPoints.values());
        Collections.sort(sortedPoints);
        return sortedPoints;
    }
    /**
     * Creates an array of the mountain points for a race sorted
     * @return The array of the mountain points for this race sorted
     */
    public ArrayList<Integer> mountainPointsSorted() {
        ArrayList<Integer> sortedPoints = new ArrayList<>(this.mountainPoints.values());
        Collections.sort(sortedPoints);
        return sortedPoints;
    }
    /**
     * Creates a new stage with the given parameters.
     * Also generates a unique id for this stage
     * @param name The name of this stage
     * @param description The description of this stage
     * @param length The distance of this stage
     * @param startTime The time this stage starts
     * @param type The type of race {@link StageType#FLAT},
     * {@link StageType#MEDIUM_MOUNTAIN}, {@link StageType#HIGH_MOUNTAIN} or
     * {@link StageType#TT}
     * @return The unique ID of this created stage
     */
    public int addStage(String name, String description, double length, LocalDateTime startTime, StageType type) {
        int id = 0;
		boolean used = true;
		while(used) {
            id = (int)(Math.random() * 9000) + 1000;
            if(!stages.containsKey(id)) {
                used = false;
            }
        }
        this.orderedStageIds.add(id);
        this.stages.put(id, new Stage(name, description, length, startTime, type, id, this));
        return id;
    }
    /**
     * Gets the ordered stage ids of a race
     * @return The ordered stage ids of this race
     */
    public ArrayList<Integer> getOrderedStageIds() {
        return this.orderedStageIds;
    }
    /**
     * Gets the riders in a race
     * @return An ArrayList of rider ids of those competing in this race
     */
    public ArrayList<Rider> getRiders() {
        return this.riders;
    }
    /**
     * Gets the stages in a race
     * @return A map containing the stage ids and stage objects of those in this race
     */
    public Map<Integer, Stage> getStages() {
        return this.stages;
    }
    /**
     * Finds a stage using a stageId
     * @param stageId The unique id representing the stage being searched for
     * @param races The list of all races in CyclingPortalImpl
     * @return The stage that stageId belongs to
     */
    public static Stage findStage(int stageId, Map<Integer, Race> races) {
        int correctId = 0;
        int[] raceIds = races.keySet().stream().mapToInt(Integer::intValue).toArray();
        for(int i=0; i<races.size(); i++) {
            if(races.get(raceIds[i]).getStages().containsKey(stageId)) {
                correctId = raceIds[i];
            }
        }
        return races.get(correctId).getStages().get(stageId);
    }
    /**
     * Finds the race a stage is apart of from the stage's id
     * @param stageId The unique id representing the stage inside the race being searched for
     * @param races The list of all races in CyclingPortalImpl
     * @return The race that the stage represented by stageId belongs to
     */
    public static Race findStagesRace(int stageId, Map<Integer, Race> races) {
        int correctId = 0;
        int[] raceIds = races.keySet().stream().mapToInt(Integer::intValue).toArray();
        for(int i=0; i<races.size(); i++) {
            if(races.get(raceIds[i]).getStages().containsKey(stageId)) {
                correctId = raceIds[i];
            }
        }
        return races.get(correctId);
    }
    /**
     * Calculates the length of a race from the length of all
     * its stages
     * @return The length of this race
     */
    public double getLength() {
        double length = 0;
        int[] stageIds = stages.keySet().stream().mapToInt(Integer::intValue).toArray();

        for(int i = 0; i<stageIds.length; i++) {
            length = length + stages.get(stageIds[i]).getLength();
        }
        return length;
    }
    /**
     * Writes a formatted string containing information about a race
     * @return A formatted string containing the id,
     * name, description, number of stages and 
     * length of this race
     */
    @Override
    public String toString() {
        return "{" +
            ", id='" + this.id + "'" +
            ", name='" + this.name + "'" +
            ", description='" + this.description + "'" +
            ", stages='" + this.stages.size() + "'" +
            ", length=' " + this.getLength() + "'" +
            "}";
    }
}

