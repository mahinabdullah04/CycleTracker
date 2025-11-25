---
Title: Cycle Tracker - Phase 2
Author: Md Mahin Abdullah (abdullmm@myumanitoba.ca)(7999890)
Date: Fall 2025
---

# CycleTracker - Phase 2: Multi-User Activity Tracking with Social Features

## Overview

CycleTracker Phase 2 extends the domain model from Phase 1 to support a multi-user cycling activity tracker with social networking features, intelligent route-finding, and proper layered architecture following SOLID principles.

### Key Features

* **Multi-user profiles**: Create accounts and sign in to access personalized activity tracking
* **Activity management**: Log cycling activities with routes, gear, and statistics
* **Social features**: Follow other users and view their activities in a unified feed
* **Route finding**: Discover new routes using depth-first search pathfinding algorithm based on existing routes
* **Gear management**: Manage personal cycling equipment (bikes, helmets, shoes)
* **Map management**: Track obstacles on a grid-based map system
* **Layered architecture**: Separation of concerns with UI, Logic, Model, and Exception layers

## Running the Application

The application can be started by running:
```bash
cd F:\git-projects\CycleTracker\CycleTracker
mvn clean compile exec:java -Dexec.mainClass="ca.umanitoba.cs.abdullmm.ui.MainRepl"
```

Or compile and run directly:
```bash
mvn clean package
java -cp target/classes ca.umanitoba.cs.abdullmm.ui.MainRepl
```

## Flows of Interaction

### Resources Consulted

* Java Collections Framework - ArrayList for managing collections
* Design patterns for layered architecture
* Guava preconditions for contract validation

### Diagrams

#### User Authentication and Profile Creation

```mermaid
flowchart
  subgraph USER_AUTHENTICATION
    start[[Start]]
    menu[Authentication Menu]
    createChoice{Create or Sign In?}

    createProfile[Create Profile]
    createInput[Input: username]
    checkDuplicate{Username exists?}
    createSuccess[[Profile Created]]
    createError[[Error: Username taken]]

    signIn[Sign In]
    signInput[Input: username]
    checkExists{Username exists?}
    signSuccess[[Signed In Successfully]]
    signError[[Error: User not found]]

    mainMenu[[Main Menu]]

    start --> menu
    menu --> createChoice
    createChoice -->|Create| createProfile
    createChoice -->|Sign In| signIn

    createProfile --> createInput
    createInput --> checkDuplicate
    checkDuplicate -->|Yes| createError
    checkDuplicate -->|No| createSuccess
    createSuccess --> menu
    createError --> menu

    signIn --> signInput
    signInput --> checkExists
    checkExists -->|No| signError
    checkExists -->|Yes| signSuccess
    signSuccess --> mainMenu
    signError --> menu
  end
```

#### Add Activity

```mermaid
flowchart
  subgraph ADD_ACTIVITY
    start[[User Signed In]]
    menu[[Main Menu]]
    addActivity[Add Activity]
    checkGear{Has Gear?}
    noGear[[Error: No Gear]]
    nameInput[Input: Activity Name]
    routeChoice{Route Source?}

    routeDuplicate[Duplicate Previous Route]
    checkActivities{Has Previous Activities?}
    selectRoute[Select Route from Previous Activities]

    routeNew[Create New Route]
    addPoints[Add Route Points - X, Y coordinates]

    selectGear[Select Gear to Use]
    dateInput[Input: Date - optional]
    durationInput[Input: Duration - minutes]
    validate{All Valid?}
    createSuccess[[Activity Created]]
    createError[[Error: Invalid Parameters]]

    start --> menu
    menu --> addActivity
    addActivity --> checkGear
    checkGear -->|No| noGear
    noGear --> menu
    checkGear -->|Yes| nameInput
    nameInput --> routeChoice

    routeChoice -->|Duplicate| routeDuplicate
    routeDuplicate --> checkActivities
    checkActivities -->|No| menu
    checkActivities -->|Yes| selectRoute

    routeChoice -->|New| routeNew
    routeNew --> addPoints

    selectRoute --> selectGear
    addPoints --> selectGear
    selectGear --> dateInput
    dateInput --> durationInput
    durationInput --> validate

    validate -->|No| createError
    validate -->|Yes| createSuccess
    createError --> menu
    createSuccess --> menu
  end
```

#### Find Route with Pathfinding

```mermaid
flowchart
  subgraph FIND_ROUTE
    start[[User Signed In]]
    menu[[Main Menu]]
    findRoute[Find Route]
    scopeChoice{Search Scope?}

    userOnly[Search in My Routes]
    feedBased[Search in My Feed]

    startInput[Input: Start Coordinates - X, Y]
    endInput[Input: End Coordinates - X, Y]
    validate{Coordinates Valid?}
    invalidCoords[[Error: Invalid Coordinates]]

    search[Run DFS Pathfinding Algorithm using Stack]
    checkResult{Path Found?}
    pathFound[[Route Found! Display Points]]
    pathNotFound[[Error: No Path Available]]

    useRoute{Use Route for Activity?}
    redirectAdd[[Go to Add Activity]]

    start --> menu
    menu --> findRoute
    findRoute --> scopeChoice

    scopeChoice -->|My Routes| userOnly
    scopeChoice -->|My Feed| feedBased

    userOnly --> startInput
    feedBased --> startInput

    startInput --> endInput
    endInput --> validate
    validate -->|No| invalidCoords
    invalidCoords --> menu
    validate -->|Yes| search

    search --> checkResult
    checkResult -->|No| pathNotFound
    checkResult -->|Yes| pathFound

    pathNotFound --> menu
    pathFound --> useRoute
    useRoute -->|No| menu
    useRoute -->|Yes| redirectAdd
    redirectAdd --> menu
  end
```

#### View Activity Feed

```mermaid
flowchart
  subgraph VIEW_FEED
    start[[User Signed In]]
    menu[[Main Menu]]
    viewFeed[View Activity Feed]
    checkActivities{Activities in Feed?}
    empty[[Feed is Empty]]
    list[Display List of Own + Followed Users Activities]
    selectActivity{View Details?}

    detail[[Display Activity Details and Route]]

    start --> menu
    menu --> viewFeed
    viewFeed --> checkActivities
    checkActivities -->|No| empty
    checkActivities -->|Yes| list
    empty --> menu
    list --> selectActivity
    selectActivity -->|No| menu
    selectActivity -->|Yes| detail
    detail --> menu
  end
```

#### Follow Users

```mermaid
flowchart
  subgraph FOLLOW_USERS
    start[[User Signed In]]
    menu[[Main Menu]]
    followMenu[Follow Menu]
    action{Action?}

    follow[Follow User]
    listUsers[Display Available Users]
    selectUser[Select User to Follow]
    checkFollowing{Already Following?}
    followSuccess[[Now Following]]
    alreadyFollowing[[Already Following]]

    view[View Followed Users]
    listFollowed[Display Followed Users]

    unfollow[Unfollow User]
    selectUnfollow[Select User to Unfollow]
    unfollowSuccess[[Unfollowed]]

    start --> menu
    menu --> followMenu
    followMenu --> action

    action -->|Follow| follow
    follow --> listUsers
    listUsers --> selectUser
    selectUser --> checkFollowing
    checkFollowing -->|Yes| alreadyFollowing
    checkFollowing -->|No| followSuccess
    alreadyFollowing --> menu
    followSuccess --> menu

    action -->|View| view
    view --> listFollowed
    listFollowed --> menu

    action -->|Unfollow| unfollow
    unfollow --> selectUnfollow
    selectUnfollow --> unfollowSuccess
    unfollowSuccess --> menu
  end
```

## Domain Model

### Changes from Phase 1

1. **Added UserProfile class**: Represents a user's personal gear inventory, activity history, and followed users. Maintains invariants about non-null collections and valid user IDs.

2. **Added ActivityFeed class**: Aggregates activities from a user and all users they follow. Provides methods to retrieve own activities, followed activities, or all activities visible in the feed.

3. **Added Stack interface and LinkedStack implementation**: Required by Phase 2 specification for pathfinding algorithm. LinkedStack uses linked list structure with proper invariants.

4. **Refactored Route.getDistance()**: Changed return type from int to double for consistency with Activity distance tracking.

5. **Added Guava precondition checks**: Enhanced Shoe and Helmet classes with proper validation.

6. **Restructured package organization**: Separated code into ui, logic, model, and exceptions packages following layered architecture.

### Class Diagrams

```mermaid
classDiagram
    class GridPoint {
        <<record>>
        -int x
        -int y
    }
    note for GridPoint "Invariants:
     * x and y are integers"

    class Dimension {
        -int width
        -int height
        +Dimension(int height, int width)
        +int width()
        +int height()
    }
    note for Dimension "Invariants:
     * width > 0
     * height > 0"

    class Route {
        -ArrayList<GridPoint> points
        -String name
        +Route(String name)
        +ArrayList<GridPoint> getPoints()
        +String getName()
        +void addPoint(GridPoint point)
        +double getDistance()
        +boolean contains(GridPoint point)
    }
    note for Route "Invariants:
     * points is not null
     * name is not null and not empty
     * Activity validates non-empty points"

    class Gear {
        <<interface>>
        +String getName()
        +String getType()
        +String getDescription()
    }

    class Bike {
        -String name
        -String bikeType
        -int numberOfGears
        +Bike(String name, String bikeType, int numberOfGears)
    }
    note for Bike "Invariants:
     * name is not null and not empty
     * bikeType is not null and not empty
     * numberOfGears > 0"

    class Helmet {
        -String name
        -String size
        +Helmet(String name, String size)
    }
    note for Helmet "Invariants:
     * name is not null and not empty
     * size is not null and not empty"

    class Shoe {
        -String name
        -String size
        -String shoeType
        +Shoe(String name, String size, String shoeType)
    }
    note for Shoe "Invariants:
     * name is not null and not empty
     * size is not null and not empty
     * shoeType is not null and not empty"

    class Activity {
        -Route route
        -Gear gearUsed
        -LocalDate date
        -double distance
        -int durationMinutes
        -String name
        +Activity(Route route, Gear gearUsed, LocalDate date, double distance, int durationMinutes, String name)
        +Route getRoute()
        +Gear gearUsed()
        +LocalDate getDate()
        +double getDistance()
        +int getDurationMinutes()
        +String getName()
    }
    note for Activity "Invariants:
     * route is not null with at least 1 point
     * gearUsed is not null
     * date is not null
     * distance > 0
     * durationMinutes > 0
     * name is not null and not empty"

    class Obstacle {
        -GridPoint position
        -Dimension size
        -String name
        +Obstacle(String name, GridPoint position, Dimension size)
        +GridPoint getPosition()
        +Dimension getSize()
        +String getName()
        +void setPosition(GridPoint position)
    }
    note for Obstacle "Invariants:
     * position is not null
     * size is not null
     * name is not null and not empty"

    class Map {
        -Dimension dimension
        -ArrayList<Obstacle> obstacles
        -String name
        +Map(Dimension dimension, String name)
        +Dimension getDimension()
        +ArrayList<Obstacle> getObstacles()
        +String getName()
        +void addObstacle(Obstacle obstacle)
        +void removeObstacle(Obstacle obstacle)
        +boolean isValidPosition(GridPoint point)
    }
    note for Map "Invariants:
     * dimension is not null
     * obstacles is not null
     * name is not null and not empty"

    class Stack~T~ {
        <<interface>>
        +void push(T item)
        +T pop()
        +int size()
        +boolean isEmpty()
        +T peek()
    }
    note for Stack "Methods throw EmptyStackException:
     * pop() - when stack is empty
     * peek() - when stack is empty
     Invariants:
     * LIFO ordering maintained
     * size >= 0"

    class LinkedStack~T~ {
        -Node~T~ top
        -int size
        +LinkedStack()
        +void push(T item)
        +T pop()
        +int size()
        +boolean isEmpty()
        +T peek()
    }
    note for LinkedStack "Invariants:
     * size >= 0
     * (size == 0) iff (top == null)
     * LIFO ordering maintained
     * Throws EmptyStackException on pop/peek when empty"

    class UserProfile {
        -String userId
        -ArrayList<Gear> gearInventory
        -ArrayList<Activity> activities
        -ArrayList<String> followedUserIds
        +UserProfile(String userId)
        +String getUserId()
        +ArrayList<Gear> getGearInventory()
        +ArrayList<Activity> getActivities()
        +ArrayList<String> getFollowedUserIds()
        +void addGear(Gear gear)
        +void removeGear(Gear gear)
        +void addActivity(Activity activity)
        +boolean isFollowing(String targetUserId)
        +void followUser(String targetUserId)
        +void unfollowUser(String targetUserId)
    }
    note for UserProfile "Invariants:
     * userId is not null and not empty
     * gearInventory is not null
     * activities is not null
     * followedUserIds is not null"

    class ActivityFeed {
        -String currentUserId
        -Map<String, UserProfile> userProfiles
        +ActivityFeed(String currentUserId, Map<String, UserProfile> userProfiles)
        +ArrayList<Activity> getAllActivities()
        +ArrayList<Activity> getOwnActivities()
        +ArrayList<Activity> getFollowedActivities()
    }
    note for ActivityFeed "Invariants:
     * currentUserId is not null and not empty
     * userProfiles is not null"

    Gear <|.. Bike
    Gear <|.. Helmet
    Gear <|.. Shoe
    Activity --> Route
    Activity --> Gear
    Route --> GridPoint
    Obstacle --> GridPoint
    Obstacle --> Dimension
    Map --> Dimension
    Map --> Obstacle
    Stack <|.. LinkedStack
    UserProfile --> Gear
    UserProfile --> Activity
    ActivityFeed --> UserProfile
```

## Layer Architecture

### Model Layer (ca.umanitoba.cs.abdullmm.model)
Represents the domain concepts. Validates class invariants.

* **GridPoint** (record): Represents a coordinate on the grid
* **Dimension**: Represents width and height
* **Route**: Collection of GridPoints forming a cycling route
* **Gear** (interface): Abstract cycling equipment
* **Bike, Helmet, Shoe**: Concrete gear implementations
* **Activity**: Records a completed cycling activity
* **Obstacle**: Represents an obstacle on the map
* **Map**: Grid-based world containing obstacles
* **UserProfile**: User's personal data, gear, and activities
* **ActivityFeed**: Aggregated view of activities from user and followed users
* **Stack<E>** (interface): Generic stack ADT
* **LinkedStack<E>**: Linked-list implementation of Stack

**Validation Strategy**: Class invariants checked at construction and after state changes using Guava preconditions.

### Logic Layer (ca.umanitoba.cs.abdullmm.logic)
Implements business rules and orchestrates model objects. Validates business preconditions and invariants.

* **UserManager**: Manages user profile creation, retrieval, and existence checks
* **ActivityManager**: Creates and retrieves activities, manages activity feed
* **PathFinder**: Implements pathfinding algorithm using Stack ADT with depth-first search

**Validation Strategy**: Business rule validation (duplicate users, invalid user references, activity constraints).

### UI Layer (ca.umanitoba.cs.abdullmm.ui)
Handles user interaction through REPL interface. Validates input format and type.

* **MainRepl**: Main menu system, user authentication flow, activity management flow, feed viewing, route finding interaction

**Validation Strategy**: Input format validation (string emptiness, numeric ranges, menu choices).

### Exceptions Layer (ca.umanitoba.cs.abdullmm.exceptions)
Custom exceptions for error handling across layers.

* **InvalidUserException**: User-related operation failures
* **DuplicateUserException**: Attempted creation of existing user
* **InvalidActivityException**: Activity creation or access failures
* **InvalidRouteException**: Route-related operation failures
* **PathNotFoundException**: Pathfinding algorithm failures

## Design Patterns and Principles

### SOLID Principles
* **Single Responsibility Principle**: Each class has one reason to change (UI handles I/O, Logic handles rules, Model handles state)
* **Open/Closed Principle**: Gear interface allows new gear types without modifying existing code
* **Liskov Substitution Principle**: Bike, Helmet, Shoe all properly implement Gear interface
* **Interface Segregation Principle**: Stack interface only includes required operations
* **Dependency Inversion**: MainRepl depends on manager abstractions, not direct model dependencies

### Design by Contract
* **Preconditions**: Input validation at layer boundaries (UI validates format, Logic validates business rules)
* **Postconditions**: State changes verified after operations
* **Invariants**: Class invariants checked using Guava Preconditions.check* methods

### Pathfinding Algorithm
Uses depth-first search (DFS) with a Stack ADT:
1. Initialize stack with start point
2. Track visited points to avoid cycles
3. For each point, explore unvisited adjacent points
4. When end point is reached, reconstruct path using parent pointers
5. If stack empty without finding end, no path exists

**Adjacency Definition**: Two grid points are adjacent if they differ by exactly 1 in either X or Y coordinate (not diagonally adjacent).

## Compilation and Testing

```bash
# Compile
mvn clean compile

# Run
mvn exec:java -Dexec.mainClass="ca.umanitoba.cs.abdullmm.ui.MainRepl"

# Or package and run
mvn clean package
java -cp target/classes ca.umanitoba.cs.abdullmm.ui.MainRepl
```

## Future Enhancements

* Persistent storage of user profiles and activities (database or file I/O)
* Visualization of routes and map on graphical interface
* Advanced pathfinding heuristics (Dijkstra's, A*)
* Activity statistics and analytics
* Achievements and badges system
* Export activities to external formats
