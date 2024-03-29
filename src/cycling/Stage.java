package cycling;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

/**
 * Stage stores information regarding a stage
 * such as checkpoints and the properties associated with it
 * 
 * @author Jake Klar
 * @author Tamanna Kar
 * @version 2.0
 *
 */
public class Stage implements Serializable{
    /**
     * The unique id of a stage
     */
    private int id;
    /**
     * The name of a stage
     */
    private String name;
    @SuppressWarnings("unused")
    /**
     * The start-time of a stage
     */
    private LocalDateTime startTime;
    /**
     * The length (in km) of a stage
     */
    private double length;
    /**
     * The race a stage belongs to
     */
    private Race race;
    @SuppressWarnings("unused")
    /**
     * The description of a stage
     */
    private String description;
    /**
     * The type of a stage
     */
    private StageType type;
    /**
     * A map with checkpointIds as keys and checkpoint objects as values
     */
    private Map<Integer, Checkpoint> checkpoints;
    /**
     * The state a race is currently in
     */
    private String state;
    /**
     * A map with riderIds as keys and their times for all checkpoints as values
     */
    private Map<Integer, LocalTime[]> riderTimes;
    /**
     * A map with riderIds as keys and their adjusted elapsed time as values
     */
    private Map<Integer, LocalTime> adjustedTimes;
    /**
     * A map with riderIds as keys and their sprint points as values
     */
    private Map<Integer, Integer> sprinterPoints;
    /**
     * A map with riderIds as keys and their mountain points as values
     */
    private Map<Integer, Integer> mountainPoints;
    /**
     * A list of riderIds ordered by finish position
     */
    private ArrayList<Integer> riderPositions;
    /**
     * A list of sprintCheckpointIds
     */
    private ArrayList<Integer> sprintCheckpointIds;
    /**
     * A list of reference indexes for sprintCheckpoints
     */
    private ArrayList<Integer> sprintCheckpointRef;
    /**
     * A list of mountainCheckpointIds
     */
    private ArrayList<Integer> mountainCheckpointIds;
    /**
     * A list of reference indexes for mountainCheckpoints
     */
    private ArrayList<Integer> mountainCheckpointRef;
    private static final int[] FLATSTAGEPOINTS = {50,30,20,18,16,14,12,10,8,7,6,5,4,3,2};
    private static final int[] MEDIUMSTAGEPOINTS = {30,25,22,19,17,15,13,11,9,7,6,5,4,3,2};
    private static final int[] HIGHMOUNTAINPOINTS = {20,17,15,13,11,10,9,8,7,6,5,4,3,2,1};
    private static final int[] TIMETRIALPOINTS = {20,17,15,13,11,10,9,8,7,6,5,4,3,2,1};
    /**
     * An empty constructor used
     * to make a temporarily empty stage
     */
    public Stage() {
        
    }
    /**
     * Creates a new stage with the provided parameters
     * @param name The name of this stage
     * @param description The description of this stage
     * @param length The distance of this stage
     * @param startTime The time this stage begins
     * @param type The type of stage {@link StageType#FLAT},
     * {@link StageType#MEDIUM_MOUNTAIN}, {@link StageType#HIGH_MOUNTAIN} or
     * {@link StageType#TT}
     * @param id The unique id of this stage
     * @param race The list of all races in CyclingPortalImpl
     */
    public Stage(String name, String description, double length, LocalDateTime startTime, StageType type, int id, Race race) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.length = length;
        this.startTime = startTime;
        this.type = type;
        this.race = race;
        this.state = "in preparation";
        this.sprinterPoints = new HashMap<Integer, Integer>();
        this.mountainPoints = new HashMap<Integer, Integer>();
        this.riderPositions = new ArrayList<Integer>();
        this.sprintCheckpointIds = new ArrayList<Integer>();
        this.sprintCheckpointRef = new ArrayList<Integer>();
        this.mountainCheckpointIds = new ArrayList<Integer>();
        this.mountainCheckpointRef = new ArrayList<Integer>();
        this.riderTimes = new HashMap<Integer, LocalTime[]>();
        this.adjustedTimes = new HashMap<Integer, LocalTime>();
        this.checkpoints = new HashMap<Integer, Checkpoint>();
    }
    /**
     * Calculates the mountain points for every rider in a stage
     * @return The mountain points for every rider in this stage
     */
    public Map<Integer, Integer> getMountainPoints() {
        //Initalizing each riders points at 0 to avoid null access errors
        for(int i=0; i< riderPositions.size(); i++) {
            mountainPoints.put(riderPositions.get(i), 0);
        }
        //Adding all results of every rider to each mountain checkpoint
        for(int i=0; i < mountainCheckpointIds.size(); i++) {
            for(int j=0; j<riderPositions.size(); j++) {
                int currRider = riderPositions.get(j);
                checkpoints.get(mountainCheckpointIds.get(i)).addResult(currRider,
                riderTimes.get(currRider)[mountainCheckpointRef.get(i)]);
            }
        checkpoints.get(mountainCheckpointIds.get(i)).sortResults();
        
        //Adding the points for each checkpoint for each rider to their total
        for(int k=0; k<riderPositions.size(); k++) {
            int points = checkpoints.get(mountainCheckpointIds.get(i))
            .getRiderPointReward(riderPositions.get(k));
            mountainPoints.put(riderPositions.get(k), 
            riderPositions.get(k) + points);
            }
        }
        
        return this.mountainPoints;
    }
    /**
     * Calculates the sprinter points for every rider in a stage
     * @return The sprinter points for each rider in this stage
     */
    public Map<Integer, Integer> getSprinterPoints() {
        //Points for the stage itself
        for(int i=0; i<riderPositions.size(); i++) {
            if(i < 15) {
                switch(this.type) {
                    case StageType.FLAT:
                        sprinterPoints.put(riderPositions.get(i), FLATSTAGEPOINTS[i]);
                        break;
                    case StageType.MEDIUM_MOUNTAIN:
                        sprinterPoints.put(riderPositions.get(i), MEDIUMSTAGEPOINTS[i]);
                        break;
                    case StageType.HIGH_MOUNTAIN:
                        sprinterPoints.put(riderPositions.get(i), HIGHMOUNTAINPOINTS[i]);
                        break;
                    case StageType.TT:
                        sprinterPoints.put(riderPositions.get(i), TIMETRIALPOINTS[i]);
                } 
            }
            //If placing over 15th place, riders are given 0 points for stage finish
            else {
                sprinterPoints.put(riderPositions.get(i), 0);
            }
        }
        //Congregating all rider times for the all the sprint checkpoints in the stage
        for(int i=0; i < sprintCheckpointIds.size(); i++) {
            for(int j=0; j<riderPositions.size(); j++) {
                int currRider = riderPositions.get(j);
                checkpoints.get(sprintCheckpointIds.get(i)).addResult(currRider,
                riderTimes.get(currRider)[sprintCheckpointRef.get(i)]);
            }
            checkpoints.get(sprintCheckpointIds.get(i)).sortResults();
            
            //Calculates the amount of points to be rewarded for this checkpoint
            for(int k=0; k<riderPositions.size(); k++) {
                int points = checkpoints.get(sprintCheckpointIds.get(i))
                .getRiderPointReward(riderPositions.get(k));
                sprinterPoints.put(riderPositions.get(k), 
                riderPositions.get(k) + points);
            }
        }
        return this.sprinterPoints;
    }
    /**
     * Gets the mountain points for a rider in a stage
     * @param riderId The unique id of the rider
     * @return The mountain points for the rider in this stage
     */
    public int getRiderMountainPoints(int riderId) {
        return this.mountainPoints.get(riderId);
    }
    /**
     * Gets the sprinter points for a rider in a stage
     * @param riderId The unique id of the rider
     * @return The sprinter points for the rider in this stage
     */
    public int getRiderSprinterPoints(int riderId) {
        return this.sprinterPoints.get(riderId);
    }
    /**
     * Adjusts the times of riders who finish within 1 second
     * of each other to both have the adjusted time that was
     * the fastest between the two
     * @param index What position to begin sorting from
     * @return The Map of riderIds and their newly adjusted times for this stage
     */
    public Map<Integer, LocalTime> adjustTimes(int index) {
        System.out.println(riderPositions.size());
        for(int i: riderPositions) {
            System.out.println(i);
        }

        LocalTime oldTime = riderTimes.get(riderPositions.get(0))[checkpoints.size()-1];
        System.out.println(oldTime);
        adjustedTimes.put(riderPositions.get(0), oldTime);
        for(int i=0; i<riderPositions.size(); i++) {
            LocalTime newTime = riderTimes.get(riderPositions.get(i))[checkpoints.size()-1];
            if(oldTime.plusSeconds(1).isAfter(newTime)) {
                adjustedTimes.put(riderPositions.get(i), oldTime);
            }else {
                oldTime = riderTimes.get(riderPositions.get(i))[checkpoints.size()-1];
                adjustedTimes.put(riderPositions.get(i), oldTime);
            }
        }
        return adjustedTimes;
    }
    /**
     * Gets the adjusted times of a race
     * @return The adjusted times of this race
     */
    public Map<Integer, LocalTime> getAdjustedTimes() {
        return this.adjustedTimes;
    }
    /**
     * Gets the checkpoints within a stage
     * @return A map of the checkpoint objects and ids within this stage
     */
    public Map<Integer, Checkpoint> getCheckpoints() {
        return this.checkpoints;
    }
    /**
     * Gets the id of a stage
     * @return The unique id of this stage
     */
    public int getId() {
        return this.id;
    }
    /**
     * Gets the name of a stage
     * @return The name of this stage
     */
    public String getName() {
        return this.name;
    }
    /**
     * Gets the length of a stage
     * @return The length of this stage
     */
    public double getLength() {
        return this.length;
    }
    /**
     * Gets the state of a tage
     * @return The state of this stage
     */
    public String getState() {
        return this.state;
    }
    /**
     * Sets the current state of a stage to "waiting for results".
     * Applies after this stage has finished being configured
     */
    public void setState() {
        this.state = "waiting for results";
    }
    /**
     * Gets the type of a stage
     * @return The type of stage: {@link StageType#FLAT},
     * {@link StageType#MEDIUM_MOUNTAIN}, {@link StageType#HIGH_MOUNTAIN} or
     * {@link StageType#TT} this is
     */
    public StageType getType() {
        return this.type;
    }
    /**
     * Adds the results of a stage for a specific rider
     * @param id The unique id of the rider these times belongs to
     * @param times The array of times each checkpoint was passed
     * inside this stage for that rider
     */
    public void addResults(int id, LocalTime[] times) {
        this.riderTimes.put(id, times);
    }
    /**
     * Gets the results for a rider in a stage
     * @param id The unique id of the rider whose results are being returned
     * @return The array of times each checkpoint was passed
     * inside this stage for the rider
     */
    public LocalTime[] getResults(int id) {
        return this.riderTimes.get(id);
    }
    /**
     * Adds a new mountain checkpoint to a stage
     * @param location Where in the stage this checkpoint is
     * @param type The category of the climb - {@link CheckpointType#C4},
	 *                        {@link CheckpointType#C3}, {@link CheckpointType#C2},
	 *                        {@link CheckpointType#C1}, or {@link CheckpointType#HC}
     * @param gradient The average gradient of the climb
     * @param length The length (in km) of the climb
     * @return The unique id of the checkpoint created
     */
    public int addMountainCheckpoint(double location, CheckpointType type, double gradient, double length) {
        int id = 0;
		boolean used = true;
		while(used) {
            id = (int)(Math.random() * 9000) + 1000;
            if(!checkpoints.containsKey(id)) {
                used = false;
            }
        }
        checkpoints.put(id, new MountainCheckpoint(location, type, gradient, length, id));
        mountainCheckpointIds.add(id);
        mountainCheckpointRef.add(checkpoints.size()-1);
        return id;
    }
    /**
     * Adds an intermediate sprint checkpoint to a stage
     * @param location Where in the stage this checkpoint is
     * @return The unique id of the checkpoint created
     */
    public int addSprintCheckpoint(double location) {
        int id = 0;
		boolean used = true;
		while(used) {
            id = (int)(Math.random() * 9000) + 1000;
            if(!checkpoints.containsKey(id)) {
                used = false;
            }
        }
        checkpoints.put(id, new SprintCheckpoint(location, id));
        sprintCheckpointIds.add(id);
        sprintCheckpointRef.add(checkpoints.size()-1);
        return id;
    }
    /**
     * Finds the stage a checkpoint is in
     * @param checkpointId The unique id of the checkpoint being referenced
     * @param races The list of races in CyclingPortalImpl
     * @param stageIds The list of all stage ids in CyclingPortalImpl
     * @return The stage that the checkpoint that checkpointId belongs to is in
     */
    public static Stage findCheckpointsStage(int checkpointId, Map<Integer, Race> races, ArrayList<Integer> stageIds) {
        Stage stage = new Stage();
        int[] raceIds = races.keySet().stream().mapToInt(Integer::intValue).toArray();
        for(int i=0; i<races.size(); i++) {
            for(int j=0; j<races.get(raceIds[i]).getStages().size(); j++) {
                if(races.get(raceIds[i]).getStages()
                .get(stageIds.get(j)).getCheckpoints().containsKey(checkpointId)) {
                    stage = races.get(raceIds[i]).getStages().get(stageIds.get(j));
                }
            }
        }
        return stage;
    }
    /**
     * Gets all of the times for every rider in a stage
     * @return A map containing all of the rider ids and their
     * times for this stage
     */
    public Map<Integer, LocalTime[]> getRiderTimes() {
        return this.riderTimes;
    }
    /**
     * Gets the finishing positions of every rider in a stage
     * @return An ordered list of the rider ids corresponding
     * to where they finished for this stage
     */
    public ArrayList<Integer> getRiderPositions() {
        return this.riderPositions;
    }
    /**
     * Gets the race that a stage belongs to
     * @return The race that this stage belongs to
     */
    public Race getRace() {
        return this.race;
    }
}
