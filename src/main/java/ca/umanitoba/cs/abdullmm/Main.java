package ca.umanitoba.cs.abdullmm;

import ca.umanitoba.cs.abdullmm.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ArrayList<Gear> gearInventory = new ArrayList<>();
        ArrayList<Route> routes = new ArrayList<>();
        ArrayList<Activity> activities = new ArrayList<>();
        Map currentMap = null;  // Only one map in Phase 1

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Choose an option: ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                switch (input) {
                    case "1" -> createBike(scanner, gearInventory);
                    case "2" -> createHelmet(scanner, gearInventory);
                    case "3" -> createShoe(scanner, gearInventory);
                    case "4" -> createRoute(scanner, routes);
                    case "5" -> addPointToRoute(scanner, routes);
                    case "6" -> createActivity(scanner, routes, gearInventory, activities);
                    case "7" -> currentMap = createMap(scanner, currentMap);
                    case "8" -> addObstacle(scanner, currentMap);
                    case "9" -> showMap(currentMap);
                    case "10" -> listActivities(activities);
                    case "11" -> listGear(gearInventory);
                    case "12" -> listRoutes(routes);
                    case "13" -> listObstacles(currentMap);
                    case "14" -> showActivity(scanner, activities, currentMap);
                    case "15" -> removeGear(scanner, gearInventory);
                    case "16" -> removeActivity(scanner, activities);
                    case "17" -> removeObstacle(scanner, currentMap);
                    case "18" -> currentMap = removeMap(scanner, currentMap);
                    case "0" -> {
                        running = false;
                        System.out.println("Exiting. Goodbye.");
                    }
                    default -> System.out.println("Unknown option. Enter a number from the menu.");
                }
            }
            System.out.println();
        }

        scanner.close();
    }

    private static void printMenu() {
        System.out.println("=== CycleTracker REPL ===");
        System.out.println("1) Create Bike");
        System.out.println("2) Create Helmet");
        System.out.println("3) Create Shoe");
        System.out.println("4) Create Route");
        System.out.println("5) Add Point to Route");
        System.out.println("6) Create Activity");
        System.out.println("7) Create Map");
        System.out.println("8) Add Obstacle to Map");
        System.out.println("9) Show Map");
        System.out.println("10) List Activities");
        System.out.println("11) List Gear");
        System.out.println("12) List Routes");
        System.out.println("13) List Obstacles");
        System.out.println("14) Show Specific Activity");
        System.out.println("15) Remove Gear");
        System.out.println("16) Remove Activity");
        System.out.println("17) Remove Obstacle");
        System.out.println("18) Remove Map");
        System.out.println("0) Exit");
    }

    private static void createBike(Scanner scanner, ArrayList<Gear> gearInventory) {
        System.out.print("Bike name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Bike type (e.g., Road, Mountain): ");
        String type = scanner.nextLine().trim();
        System.out.print("Number of gears: ");
        try {
            int num = Integer.parseInt(scanner.nextLine().trim());
            Bike bike = new Bike(name, type, num);
            gearInventory.add(bike);
            System.out.println("Created bike: " + bike.getDescription());
        } catch (Exception e) {
            System.out.println("Failed to create bike: " + e.getMessage());
        }
    }

    private static void createHelmet(Scanner scanner, ArrayList<Gear> gearInventory) {
        System.out.print("Helmet name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Helmet size (e.g., M, L): ");
        String size = scanner.nextLine().trim();
        try {
            Helmet helmet = new Helmet(name, size);
            gearInventory.add(helmet);
            System.out.println("Created helmet: " + helmet.getDescription());
        } catch (Exception e) {
            System.out.println("Failed to create helmet: " + e.getMessage());
        }
    }

    private static void createShoe(Scanner scanner, ArrayList<Gear> gearInventory) {
        System.out.print("Shoe name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Shoe size: ");
        String size = scanner.nextLine().trim();
        System.out.print("Shoe type (e.g., Road, MTB): ");
        String shoeType = scanner.nextLine().trim();
        try {
            // Shoe constructor expects size as String
            Shoe shoe = new Shoe(name, size, shoeType);
            gearInventory.add(shoe);
            System.out.println("Created shoe: " + shoe.getDescription());
        } catch (Exception e) {
            System.out.println("Failed to create shoe: " + e.getMessage());
        }
    }

    private static void createRoute(Scanner scanner, ArrayList<Route> routes) {
        System.out.print("Route name: ");
        String name = scanner.nextLine().trim();
        try {
            Route route = new Route(name);
            routes.add(route);
            System.out.println("Created route: " + route.getName());
        } catch (Exception e) {
            System.out.println("Failed to create route: " + e.getMessage());
        }
    }

    private static void addPointToRoute(Scanner scanner, ArrayList<Route> routes) {
        if (routes.isEmpty()) {
            System.out.println("No routes available. Create a route first.");
            return;
        }
        listRoutes(routes);
        System.out.print("Choose route index: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim());
            if (idx < 0 || idx >= routes.size()) {
                System.out.println("Invalid route index.");
                return;
            }
            System.out.print("Point X (int): ");
            int x = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Point Y (int): ");
            int y = Integer.parseInt(scanner.nextLine().trim());
            GridPoint p = new GridPoint(x, y);
            routes.get(idx).addPoint(p);
            System.out.println("Added point (" + x + "," + y + ") to route " + routes.get(idx).getName());
        } catch (Exception e) {
            System.out.println("Failed to add point: " + e.getMessage());
        }
    }

    private static void createActivity(Scanner scanner, ArrayList<Route> routes, ArrayList<Gear> gearInventory, ArrayList<Activity> activities) {
        if (routes.isEmpty()) {
            System.out.println("No routes available. Create a route first.");
            return;
        }
        if (gearInventory.isEmpty()) {
            System.out.println("No gear available. Create gear first.");
            return;
        }
        listRoutes(routes);
        try {
            System.out.print("Choose route index: ");
            int rIdx = Integer.parseInt(scanner.nextLine().trim());
            if (rIdx < 0 || rIdx >= routes.size()) {
                System.out.println("Invalid route index.");
                return;
            }
            Route route = routes.get(rIdx);
            if (route.getPoints().isEmpty()) {
                System.out.println("Selected route has no points. Add points first.");
                return;
            }

            listGear(gearInventory);
            System.out.print("Choose gear index: ");
            int gIdx = Integer.parseInt(scanner.nextLine().trim());
            if (gIdx < 0 || gIdx >= gearInventory.size()) {
                System.out.println("Invalid gear index.");
                return;
            }
            Gear gear = gearInventory.get(gIdx);

            System.out.print("Activity name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Date (YYYY-MM-DD) or blank for today: ");
            String dateStr = scanner.nextLine().trim();
            LocalDate date;
            if (dateStr.isEmpty()) date = LocalDate.now();
            else {
                try {
                    date = LocalDate.parse(dateStr);
                } catch (Exception e) {
                    System.out.println("Invalid date, using today.");
                    date = LocalDate.now();
                }
            }
            System.out.print("Duration minutes (int): ");
            int duration = Integer.parseInt(scanner.nextLine().trim());
            double distance = route.getDistance();

            Activity activity = new Activity(route, gear, date, distance, duration, name);
            activities.add(activity);
            System.out.println("Created activity: " + activity.getName() + " on " + activity.getDate());
        } catch (Exception e) {
            System.out.println("Failed to create activity: " + e.getMessage());
        }
    }

    // NEW MAP METHODS
    private static Map createMap(Scanner scanner, Map currentMap) {
        if (currentMap != null) {
            System.out.println("A map already exists. Remove it first if you want to create a new one.");
            return currentMap;
        }
        try {
            System.out.print("Map name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Map width: ");
            int width = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Map height: ");
            int height = Integer.parseInt(scanner.nextLine().trim());

            Dimension dimension = new Dimension(width, height);
            Map map = new Map(dimension, name);
            System.out.println("Created map: " + map.getName() + " (" + width + "x" + height + ")");
            return map;
        } catch (Exception e) {
            System.out.println("Failed to create map: " + e.getMessage());
            return currentMap;
        }
    }

    private static void addObstacle(Scanner scanner, Map map) {
        if (map == null) {
            System.out.println("No map exists. Create a map first.");
            return;
        }
        try {
            System.out.print("Obstacle name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Obstacle position X: ");
            int x = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Obstacle position Y: ");
            int y = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Obstacle width: ");
            int width = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Obstacle height: ");
            int height = Integer.parseInt(scanner.nextLine().trim());

            GridPoint position = new GridPoint(x, y);
            Dimension size = new Dimension(width, height);
            Obstacle obstacle = new Obstacle(name, position, size);
            map.addObstacle(obstacle);
            System.out.println("Added obstacle: " + name + " at (" + x + "," + y + ")");
        } catch (Exception e) {
            System.out.println("Failed to add obstacle: " + e.getMessage());
        }
    }

    private static void showMap(Map map) {
        if (map == null) {
            System.out.println("No map exists. Create a map first.");
            return;
        }

        System.out.println("=== Map: " + map.getName() + " ===");
        System.out.println("Size: " + map.getDimension().width() + "x" + map.getDimension().height());
        System.out.println();

        // Create 2D grid
        int width = map.getDimension().width();
        int height = map.getDimension().height();
        char[][] grid = new char[height][width];

        // Fill with empty spaces
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

        System.out.println();
        System.out.println("Legend:");
        System.out.println("  . = Empty space");
        System.out.println("  * = Obstacle");
    }

    private static void listObstacles(Map map) {
        if (map == null) {
            System.out.println("No map exists.");
            return;
        }
        if (map.getObstacles().isEmpty()) {
            System.out.println("No obstacles on the map.");
            return;
        }
        for (int i = 0; i < map.getObstacles().size(); i++) {
            Obstacle obs = map.getObstacles().get(i);
            System.out.printf("%d) %s at (%d,%d) size %dx%d\n",
                    i, obs.getName(),
                    obs.getPosition().x(), obs.getPosition().y(),
                    obs.getSize().width(), obs.getSize().height());
        }
    }

    private static void showActivity(Scanner scanner, ArrayList<Activity> activities, Map map) {
        if (activities.isEmpty()) {
            System.out.println("No activities available.");
            return;
        }
        listActivities(activities);
        System.out.print("Choose activity index: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim());
            if (idx < 0 || idx >= activities.size()) {
                System.out.println("Invalid activity index.");
                return;
            }

            Activity activity = activities.get(idx);
            System.out.println("\n=== Activity: " + activity.getName() + " ===");
            System.out.println("Date: " + activity.getDate());
            System.out.println("Distance: " + activity.getDistance());
            System.out.println("Duration: " + activity.getDurationMinutes() + " minutes");
            System.out.println("Gear: " + activity.gearUsed().getName());
            System.out.println();

            // Show route on map
            if (map != null) {
                showActivityRoute(activity, map);
            } else {
                System.out.println("No map available to display route.");
            }
        } catch (Exception e) {
            System.out.println("Failed to show activity: " + e.getMessage());
        }
    }

    private static void showActivityRoute(Activity activity, Map map) {
        int width = map.getDimension().width();
        int height = map.getDimension().height();
        char[][] grid = new char[height][width];

        // Fill with empty spaces
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

        // Mark route
        for (GridPoint p : activity.getRoute().getPoints()) {
            if (p.x() >= 0 && p.x() < width && p.y() >= 0 && p.y() < height) {
                grid[p.y()][p.x()] = '>';
            }
        }

        // Print grid
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(grid[y][x] + " ");
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("Legend:");
        System.out.println("  . = Empty space");
        System.out.println("  * = Obstacle");
        System.out.println("  > = Route");
    }

    // REMOVE METHODS
    private static void removeGear(Scanner scanner, ArrayList<Gear> gearInventory) {
        if (gearInventory.isEmpty()) {
            System.out.println("No gear to remove.");
            return;
        }
        listGear(gearInventory);
        System.out.print("Choose gear index to remove: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim());
            if (idx < 0 || idx >= gearInventory.size()) {
                System.out.println("Invalid gear index.");
                return;
            }
            Gear removed = gearInventory.remove(idx);
            System.out.println("Removed gear: " + removed.getName());
        } catch (Exception e) {
            System.out.println("Failed to remove gear: " + e.getMessage());
        }
    }

    private static void removeActivity(Scanner scanner, ArrayList<Activity> activities) {
        if (activities.isEmpty()) {
            System.out.println("No activities to remove.");
            return;
        }
        listActivities(activities);
        System.out.print("Choose activity index to remove: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim());
            if (idx < 0 || idx >= activities.size()) {
                System.out.println("Invalid activity index.");
                return;
            }
            Activity removed = activities.remove(idx);
            System.out.println("Removed activity: " + removed.getName());
        } catch (Exception e) {
            System.out.println("Failed to remove activity: " + e.getMessage());
        }
    }

    private static void removeObstacle(Scanner scanner, Map map) {
        if (map == null) {
            System.out.println("No map exists.");
            return;
        }
        if (map.getObstacles().isEmpty()) {
            System.out.println("No obstacles to remove.");
            return;
        }
        listObstacles(map);
        System.out.print("Choose obstacle index to remove: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim());
            if (idx < 0 || idx >= map.getObstacles().size()) {
                System.out.println("Invalid obstacle index.");
                return;
            }
            Obstacle removed = map.getObstacles().get(idx);
            map.removeObstacle(removed);
            System.out.println("Removed obstacle: " + removed.getName());
        } catch (Exception e) {
            System.out.println("Failed to remove obstacle: " + e.getMessage());
        }
    }

    private static Map removeMap(Scanner scanner, Map currentMap) {
        if (currentMap == null) {
            System.out.println("No map to remove.");
            return null;
        }
        System.out.print("Are you sure you want to remove the map? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("yes")) {
            System.out.println("Removed map: " + currentMap.getName());
            return null;
        } else {
            System.out.println("Map removal cancelled.");
            return currentMap;
        }
    }

    private static void listActivities(ArrayList<Activity> activities) {
        if (activities.isEmpty()) {
            System.out.println("No activities recorded.");
            return;
        }
        for (int i = 0; i < activities.size(); i++) {
            Activity a = activities.get(i);
            System.out.printf("%d) %s | date=%s | distance=%.2f | duration=%d | gear=%s\n",
                    i, a.getName(), a.getDate(), a.getDistance(), a.getDurationMinutes(), a.gearUsed().getName());
        }
    }

    private static void listGear(ArrayList<Gear> gearInventory) {
        if (gearInventory.isEmpty()) {
            System.out.println("No gear available.");
            return;
        }
        for (int i = 0; i < gearInventory.size(); i++) {
            Gear g = gearInventory.get(i);
            System.out.printf("%d) [%s] %s - %s\n", i, g.getType(), g.getName(), g.getDescription());
        }
    }

    private static void listRoutes(ArrayList<Route> routes) {
        if (routes.isEmpty()) {
            System.out.println("No routes available.");
            return;
        }
        for (int i = 0; i < routes.size(); i++) {
            Route r = routes.get(i);
            System.out.printf("%d) %s | points=%d\n", i, r.getName(), r.getPoints().size());
        }
    }
}