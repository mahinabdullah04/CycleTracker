package ca.umanitoba.cs.abdullmm.ui;

import ca.umanitoba.cs.abdullmm.exceptions.*;
import ca.umanitoba.cs.abdullmm.logic.ActivityManager;
import ca.umanitoba.cs.abdullmm.logic.PathFinder;
import ca.umanitoba.cs.abdullmm.logic.UserManager;
import ca.umanitoba.cs.abdullmm.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

/**
 * Main REPL interface for the CycleTracker application.
 * Handles user authentication, menu navigation, and high-level operations.
 *
 * Responsibilities (UI Layer):
 * - Input collection and formatting validation
 * - Output display and formatting
 * - Menu navigation and flow control
 *
 * Delegates business logic to appropriate manager classes.
 */
public class MainRepl {
    private static final String HARDCODED_MAP_NAME = "City Map";
    private static final int HARDCODED_MAP_WIDTH = 50;
    private static final int HARDCODED_MAP_HEIGHT = 50;

    private UserManager userManager;
    private ActivityManager activityManager;
    private PathFinder pathFinder;
    private Map globalMap;
    private String currentUserId;

    /**
     * Constructs the MainRepl with all necessary managers and the hardcoded map.
     */
    public MainRepl() {
        this.userManager = new UserManager();
        this.activityManager = new ActivityManager(userManager);
        this.pathFinder = new PathFinder(userManager, activityManager);
        this.currentUserId = null;

        // Initialize hardcoded world map
        try {
            Dimension mapDimension = new Dimension(HARDCODED_MAP_HEIGHT, HARDCODED_MAP_WIDTH);
            this.globalMap = new Map(mapDimension, HARDCODED_MAP_NAME);
        } catch (Exception e) {
            System.out.println("Failed to initialize map: " + e.getMessage());
        }
    }

    /**
     * Runs the main REPL loop.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to CycleTracker!");

        while (true) {
            if (currentUserId == null) {
                displayAuthenticationMenu();
                int choice = readMenuChoice(scanner, 1, 2);

                switch (choice) {
                    case 1 -> handleCreateProfile(scanner);
                    case 2 -> handleSignIn(scanner);
                }
            } else {
                displayMainMenu();
                int choice = readMenuChoice(scanner, 1, 9);

                switch (choice) {
                    case 1 -> handleViewFeed(scanner);
                    case 2 -> handleAddActivity(scanner);
                    case 3 -> handleManageGear(scanner);
                    case 4 -> handleFollowUsers(scanner);
                    case 5 -> handleFindRoute(scanner);
                    case 6 -> handleViewMap();
                    case 7 -> handleAddObstacle(scanner);
                    case 8 -> handleViewProfile();
                    case 9 -> handleSignOut();
                }
            }
        }
    }

    private void displayAuthenticationMenu() {
        System.out.println("\n=== Authentication ===");
        System.out.println("1) Create Profile");
        System.out.println("2) Sign In");
        System.out.print("Choose an option: ");
    }

    private void displayMainMenu() {
        System.out.println("\n=== CycleTracker Main Menu ===");
        System.out.println("Signed in as: " + currentUserId);
        System.out.println("1) View Activity Feed");
        System.out.println("2) Add Activity");
        System.out.println("3) Manage Gear");
        System.out.println("4) Follow Users");
        System.out.println("5) Find Route");
        System.out.println("6) View Map");
        System.out.println("7) Add Obstacle");
        System.out.println("8) View My Profile");
        System.out.println("9) Sign Out");
        System.out.print("Choose an option: ");
    }

    private void handleCreateProfile(Scanner scanner) {
        System.out.print("Enter username: ");
        String userId = scanner.nextLine().trim();

        if (userId.isEmpty()) {
            System.out.println("Error: Username cannot be empty.");
            return;
        }

        try {
            userManager.createUser(userId);
            System.out.println("Profile created successfully! You can now sign in.");
        } catch (DuplicateUserException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleSignIn(Scanner scanner) {
        System.out.print("Enter username: ");
        String userId = scanner.nextLine().trim();

        if (userId.isEmpty()) {
            System.out.println("Error: Username cannot be empty.");
            return;
        }

        try {
            userManager.getUser(userId);
            currentUserId = userId;
            System.out.println("Signed in successfully as " + userId);
        } catch (InvalidUserException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleViewFeed(Scanner scanner) {
        System.out.println("\n=== Activity Feed ===");

        try {
            ActivityFeed feed = activityManager.getActivityFeed(currentUserId);
            ArrayList<Activity> activities = feed.getAllActivities();

            if (activities.isEmpty()) {
                System.out.println("No activities in your feed yet.");
                return;
            }

            System.out.println("Activities in your feed:");
            for (int i = 0; i < activities.size(); i++) {
                Activity a = activities.get(i);
                System.out.printf("%d) %s | Date: %s | Distance: %.2f | Gear: %s\n",
                        i, a.getName(), a.getDate(), a.getDistance(), a.gearUsed().getName());
            }

            System.out.print("View detailed activity? (y/n): ");
            String response = scanner.nextLine().trim().toLowerCase();
            if (response.equals("y")) {
                System.out.print("Enter activity index: ");
                int idx = readInt(scanner, 0, activities.size() - 1);
                displayActivityDetails(activities.get(idx));
            }
        } catch (InvalidUserException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleAddActivity(Scanner scanner) {
        System.out.println("\n=== Add Activity ===");

        try {
            UserProfile profile = userManager.getUser(currentUserId);

            // Check if user has gear
            if (profile.getGearInventory().isEmpty()) {
                System.out.println("You don't have any gear. Create gear first.");
                return;
            }

            System.out.print("Activity name: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Error: Activity name cannot be empty.");
                return;
            }

            System.out.println("\nSelect or create a route:");
            System.out.println("1) Duplicate previous route");
            System.out.println("2) Create new route");
            System.out.print("Choose: ");

            int routeChoice = readMenuChoice(scanner, 1, 2);
            Route route;

            if (routeChoice == 1) {
                if (profile.getActivities().isEmpty()) {
                    System.out.println("No previous activities to duplicate from.");
                    return;
                }

                ArrayList<Activity> activities = profile.getActivities();
                System.out.println("Select activity to duplicate route from:");
                for (int i = 0; i < activities.size(); i++) {
                    Activity a = activities.get(i);
                    System.out.printf("%d) %s (points: %d)\n", i, a.getName(), a.getRoute().getPoints().size());
                }
                System.out.print("Choose: ");
                int actIdx = readMenuChoice(scanner, 0, activities.size() - 1);
                route = activities.get(actIdx).getRoute();
            } else {
                route = createNewRoute(scanner);
                if (route == null) {
                    System.out.println("Route creation cancelled.");
                    return;
                }
            }

            // Select gear
            ArrayList<Gear> gear = profile.getGearInventory();
            System.out.println("\nSelect gear:");
            for (int i = 0; i < gear.size(); i++) {
                Gear g = gear.get(i);
                System.out.printf("%d) [%s] %s\n", i, g.getType(), g.getName());
            }
            System.out.print("Choose: ");
            int gearIdx = readMenuChoice(scanner, 0, gear.size() - 1);

            // Get date and duration
            System.out.print("Date (YYYY-MM-DD) or press Enter for today: ");
            LocalDate date;
            String dateStr = scanner.nextLine().trim();
            try {
                date = dateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dateStr);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format, using today.");
                date = LocalDate.now();
            }

            System.out.print("Duration (minutes): ");
            int duration = readPositiveInt(scanner);

            // Create activity
            double distance = route.getDistance();
            activityManager.createActivity(currentUserId, route, gear.get(gearIdx), date, distance, duration, name);
            System.out.println("Activity '" + name + "' created successfully!");

        } catch (InvalidUserException | InvalidActivityException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleManageGear(Scanner scanner) {
        System.out.println("\n=== Manage Gear ===");
        System.out.println("1) Add Gear");
        System.out.println("2) List Gear");
        System.out.println("3) Remove Gear");
        System.out.print("Choose: ");

        int choice = readMenuChoice(scanner, 1, 3);

        try {
            UserProfile profile = userManager.getUser(currentUserId);

            switch (choice) {
                case 1 -> addGear(scanner, profile);
                case 2 -> listGear(profile);
                case 3 -> removeGear(scanner, profile);
            }
        } catch (InvalidUserException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void addGear(Scanner scanner, UserProfile profile) {
        System.out.println("\n--- Add Gear ---");
        System.out.println("1) Bike");
        System.out.println("2) Helmet");
        System.out.println("3) Shoe");
        System.out.print("Choose: ");

        int type = readMenuChoice(scanner, 1, 3);
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Error: Name cannot be empty.");
            return;
        }

        try {
            Gear gear;
            switch (type) {
                case 1 -> {
                    System.out.print("Bike type (e.g., Road, Mountain): ");
                    String bikeType = scanner.nextLine().trim();
                    if (bikeType.isEmpty()) {
                        System.out.println("Error: Bike type cannot be empty.");
                        return;
                    }
                    System.out.print("Number of gears: ");
                    int numGears = readPositiveInt(scanner);
                    gear = new Bike(name, bikeType, numGears);
                }
                case 2 -> {
                    System.out.print("Helmet size (e.g., S, M, L): ");
                    String size = scanner.nextLine().trim();
                    if (size.isEmpty()) {
                        System.out.println("Error: Helmet size cannot be empty.");
                        return;
                    }
                    gear = new Helmet(name, size);
                }
                case 3 -> {
                    System.out.print("Shoe size: ");
                    String size = scanner.nextLine().trim();
                    if (size.isEmpty()) {
                        System.out.println("Error: Shoe size cannot be empty.");
                        return;
                    }
                    System.out.print("Shoe type (e.g., Road, MTB): ");
                    String shoeType = scanner.nextLine().trim();
                    if (shoeType.isEmpty()) {
                        System.out.println("Error: Shoe type cannot be empty.");
                        return;
                    }
                    gear = new Shoe(name, size, shoeType);
                }
                default -> {
                    System.out.println("Invalid choice.");
                    return;
                }
            }

            profile.addGear(gear);
            System.out.println("Gear '" + name + "' added successfully!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Error creating gear: " + e.getMessage());
        }
    }

    private void listGear(UserProfile profile) {
        ArrayList<Gear> gearList = profile.getGearInventory();
        if (gearList.isEmpty()) {
            System.out.println("You have no gear.");
            return;
        }

        System.out.println("\n--- Your Gear ---");
        for (int i = 0; i < gearList.size(); i++) {
            Gear g = gearList.get(i);
            System.out.printf("%d) [%s] %s - %s\n", i, g.getType(), g.getName(), g.getDescription());
        }
    }

    private void removeGear(Scanner scanner, UserProfile profile) {
        ArrayList<Gear> gearList = profile.getGearInventory();
        if (gearList.isEmpty()) {
            System.out.println("You have no gear to remove.");
            return;
        }

        System.out.println("\n--- Remove Gear ---");
        listGear(profile);
        System.out.print("Choose gear to remove (index): ");
        int idx = readMenuChoice(scanner, 0, gearList.size() - 1);

        Gear removed = gearList.get(idx);
        profile.removeGear(removed);
        System.out.println("Removed gear: " + removed.getName());
    }

    private void handleFollowUsers(Scanner scanner) {
        System.out.println("\n=== Follow Users ===");
        System.out.println("1) Follow a user");
        System.out.println("2) View followed users");
        System.out.println("3) Unfollow a user");
        System.out.print("Choose: ");

        int choice = readMenuChoice(scanner, 1, 3);

        try {
            UserProfile profile = userManager.getUser(currentUserId);

            switch (choice) {
                case 1 -> followUser(scanner, profile);
                case 2 -> viewFollowedUsers(profile);
                case 3 -> unfollowUser(scanner, profile);
            }
        } catch (InvalidUserException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void followUser(Scanner scanner, UserProfile profile) {
        Set<String> allUsers = userManager.getAllUserIds();
        allUsers.remove(currentUserId); // Don't show current user

        if (allUsers.isEmpty()) {
            System.out.println("No other users available to follow.");
            return;
        }

        System.out.println("\nAvailable users:");
        ArrayList<String> userList = new ArrayList<>(allUsers);
        for (int i = 0; i < userList.size(); i++) {
            String userId = userList.get(i);
            boolean following = profile.isFollowing(userId);
            System.out.printf("%d) %s%s\n", i, userId, following ? " (already following)" : "");
        }

        System.out.print("Choose user to follow (index): ");
        int idx = readMenuChoice(scanner, 0, userList.size() - 1);

        String targetUserId = userList.get(idx);
        if (profile.isFollowing(targetUserId)) {
            System.out.println("You're already following this user.");
        } else {
            try {
                profile.followUser(targetUserId);
                System.out.println("Now following " + targetUserId);
            } catch (IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void viewFollowedUsers(UserProfile profile) {
        ArrayList<String> followedUsers = profile.getFollowedUserIds();
        if (followedUsers.isEmpty()) {
            System.out.println("You're not following anyone.");
            return;
        }

        System.out.println("\n--- Followed Users ---");
        for (int i = 0; i < followedUsers.size(); i++) {
            System.out.printf("%d) %s\n", i, followedUsers.get(i));
        }
    }

    private void unfollowUser(Scanner scanner, UserProfile profile) {
        ArrayList<String> followedUsers = profile.getFollowedUserIds();
        if (followedUsers.isEmpty()) {
            System.out.println("You're not following anyone.");
            return;
        }

        System.out.println("\n--- Unfollow User ---");
        viewFollowedUsers(profile);
        System.out.print("Choose user to unfollow (index): ");
        int idx = readMenuChoice(scanner, 0, followedUsers.size() - 1);

        try {
            String targetUserId = followedUsers.get(idx);
            profile.unfollowUser(targetUserId);
            System.out.println("Unfollowed " + targetUserId);
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleFindRoute(Scanner scanner) {
        System.out.println("\n=== Find Route ===");
        System.out.println("1) Search in my routes only");
        System.out.println("2) Search in my feed (including followed users)");
        System.out.print("Choose: ");

        int choice = readMenuChoice(scanner, 1, 2);

        System.out.print("Start X coordinate: ");
        int startX = readInt(scanner, 0, HARDCODED_MAP_WIDTH - 1);
        System.out.print("Start Y coordinate: ");
        int startY = readInt(scanner, 0, HARDCODED_MAP_HEIGHT - 1);
        System.out.print("End X coordinate: ");
        int endX = readInt(scanner, 0, HARDCODED_MAP_WIDTH - 1);
        System.out.print("End Y coordinate: ");
        int endY = readInt(scanner, 0, HARDCODED_MAP_HEIGHT - 1);

        GridPoint start = new GridPoint(startX, startY);
        GridPoint end = new GridPoint(endX, endY);

        try {
            Route foundRoute;
            if (choice == 1) {
                foundRoute = pathFinder.findPathUserOnly(currentUserId, start, end);
            } else {
                foundRoute = pathFinder.findPathFromFeed(currentUserId, start, end);
            }

            System.out.println("Route found with " + foundRoute.getPoints().size() + " points!");
            System.out.println("Points: " + foundRoute.getPoints());

            System.out.print("Use this route for a new activity? (y/n): ");
            String response = scanner.nextLine().trim().toLowerCase();
            if (response.equals("y")) {
                // Will be integrated with handleAddActivity
                System.out.println("Go to 'Add Activity' to use this found route.");
            }
        } catch (InvalidUserException | PathNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleViewMap() {
        System.out.println("\n=== Map ===");
        if (globalMap == null) {
            System.out.println("Map is not initialized.");
            return;
        }

        displayMap(globalMap);
    }

    private void handleAddObstacle(Scanner scanner) {
        System.out.println("\n=== Add Obstacle ===");
        if (globalMap == null) {
            System.out.println("Map is not initialized.");
            return;
        }

        System.out.print("Obstacle name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Error: Obstacle name cannot be empty.");
            return;
        }

        System.out.print("X position: ");
        int x = readInt(scanner, 0, HARDCODED_MAP_WIDTH - 1);
        System.out.print("Y position: ");
        int y = readInt(scanner, 0, HARDCODED_MAP_HEIGHT - 1);
        System.out.print("Width: ");
        int width = readPositiveInt(scanner);
        System.out.print("Height: ");
        int height = readPositiveInt(scanner);

        try {
            GridPoint position = new GridPoint(x, y);
            Dimension size = new Dimension(height, width);
            Obstacle obstacle = new Obstacle(name, position, size);
            globalMap.addObstacle(obstacle);
            System.out.println("Obstacle '" + name + "' added successfully!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Error adding obstacle: " + e.getMessage());
        }
    }

    private void handleViewProfile() {
        System.out.println("\n=== My Profile ===");
        System.out.println("Username: " + currentUserId);

        try {
            UserProfile profile = userManager.getUser(currentUserId);
            System.out.println("Gear count: " + profile.getGearInventory().size());
            System.out.println("Activities: " + profile.getActivities().size());
            System.out.println("Following: " + profile.getFollowedUserIds().size());
        } catch (InvalidUserException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleSignOut() {
        System.out.println("Signing out from " + currentUserId);
        currentUserId = null;
    }

    private Route createNewRoute(Scanner scanner) {
        System.out.print("Route name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Error: Route name cannot be empty.");
            return null;
        }

        Route route = new Route(name);
        System.out.println("Created route: " + name);
        System.out.println("Add points to the route (enter -1 to finish):");

        while (true) {
            System.out.print("X coordinate (or -1 to finish): ");
            int x;
            try {
                x = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
                continue;
            }

            if (x == -1) {
                if (route.getPoints().isEmpty()) {
                    System.out.println("Error: Route must have at least one point.");
                    return null;
                }
                break;
            }

            System.out.print("Y coordinate: ");
            int y;
            try {
                y = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
                continue;
            }

            try {
                GridPoint point = new GridPoint(x, y);
                route.addPoint(point);
                System.out.println("Added point (" + x + ", " + y + ")");
            } catch (Exception e) {
                System.out.println("Error adding point: " + e.getMessage());
            }
        }

        return route;
    }

    private void displayActivityDetails(Activity activity) {
        System.out.println("\n=== Activity Details ===");
        System.out.println("Name: " + activity.getName());
        System.out.println("Date: " + activity.getDate());
        System.out.println("Distance: " + activity.getDistance());
        System.out.println("Duration: " + activity.getDurationMinutes() + " minutes");
        System.out.println("Gear: " + activity.gearUsed().getName() + " (" + activity.gearUsed().getType() + ")");
        System.out.println("Route points: " + activity.getRoute().getPoints().size());
    }

    private void displayMap(Map map) {
        System.out.println("Map: " + map.getName());
        System.out.println("Size: " + map.getDimension().width() + "x" + map.getDimension().height());

        int width = map.getDimension().width();
        int height = map.getDimension().height();
        char[][] grid = new char[height][width];

        // Initialize grid
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = '.';
            }
        }

        // Mark obstacles
        for (Obstacle obs : map.getObstacles()) {
            int startX = obs.getPosition().x();
            int startY = obs.getPosition().y();
            int obsWidth = obs.getSize().width();
            int obsHeight = obs.getSize().height();

            for (int y = startY; y < startY + obsHeight && y < height; y++) {
                for (int x = startX; x < startX + obsWidth && x < width; x++) {
                    if (x >= 0 && y >= 0) {
                        grid[y][x] = '*';
                    }
                }
            }
        }

        // Print grid
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(grid[y][x] + " ");
            }
            System.out.println();
        }

        System.out.println("\nLegend: . = Empty, * = Obstacle");
    }

    /**
     * Reads a menu choice and validates it's within the given range.
     * Precondition: min <= max
     * Postcondition: returns a valid integer within [min, max]
     */
    private int readMenuChoice(Scanner scanner, int min, int max) {
        while (true) {
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.printf("Please enter a number between %d and %d: ", min, max);
            } catch (NumberFormatException e) {
                System.out.printf("Invalid input. Please enter a number between %d and %d: ", min, max);
            }
        }
    }

    /**
     * Reads an integer within the given range.
     * Precondition: min <= max
     * Postcondition: returns a valid integer within [min, max]
     */
    private int readInt(Scanner scanner, int min, int max) {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("Please enter a number between %d and %d: ", min, max);
            } catch (NumberFormatException e) {
                System.out.printf("Invalid input. Please enter a number between %d and %d: ", min, max);
            }
        }
    }

    /**
     * Reads a positive integer.
     * Postcondition: returns an integer > 0
     */
    private int readPositiveInt(Scanner scanner) {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value > 0) {
                    return value;
                }
                System.out.print("Please enter a positive number: ");
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a positive number: ");
            }
        }
    }

    public static void main(String[] args) {
        MainRepl repl = new MainRepl();
        repl.run();
    }
}

