---
Title: Cycle Tracker
Author: Md Mahin Abdullah (abdullmm@myumanitoba.ca)(7999890)
Date: Fall 2025
---
---
# Overview

CycleTracker is a cycling activity tracker built for COMP 2450 in Fall 2025.
CycleTracker enables cyclists to track their rides on a grid-based map system
with the following key features:

* Track cycling activities with detailed route information on a 2D grid map.
* Manage cycling gear including bikes, helmets, and shoes.
* Map obstacles that cyclists encounter during their routes (buildings, trees, etc.).
* Visualize routes and obstacles on an ASCII-based map display.
* Log activity statistics including distance and duration.

CycleTracker provides a foundation for route tracking and gear management,
with future plans to support multiple user profiles, social activity feeds, and
intelligent route-finding algorithms.

# Domain Model
```mermaid
classDiagram
    class GridPoint{
        <<record>>
        -int x
        -int y
        +GridPoint (int x, int y)
        +int x()
        +int y()
        
    }
    note for GridPoint "Invariant Properties:
     * x >= 0
     * y >= 0 
     "
     
     class Dimension{
         <<record>>
         -int width
         -int height
         +Dimension(int width, int height)
         +int width()
         +int height()
         
     }
     note for Dimension "Invariant Properties:
     * width > 0
     * height > 0
     
     "
     class Obstacle{
         -GridPoint position
         -Dimension size
         -String name
         +Obstacle(GridPoint position, Dimension size, String name)
         +GridPoint getPosition()
         +Dimension getSize()
         +String getName()
         +void setPosition(GridPoint newPosition)
     }
     note for Obstacle "Invariant Properties:
     * position != null
     * size != null
     * must fit within map bounds
     "
     class Route{
         -List~GridPoint~ points
         -String name
         +Route(String name)
         +List~GridPoint~ getPoints()
         +String getName()
         +void addPOint(GridPoint)point
         +int getDistance()
         +boolean contains(GridPoint point)
     }
     note for Route "Invariant Properties:
     * points != null or empty"
     
     class Map{
         -Dimension dimension
         -List~Obstacle~ obstacles
         -String name
         +Map(Dimension dimension, String name)
         +Dimension getDimension()
         +List~Obstacle~ getObstacles()
         +String getName()
         +void addObstacles(Obstacle obstacle)
         +void removeObstacle(Obstacle obstacle)
         +boolean isValidPosition(GridPoint point)
     }
     note for Map "Invariant Properties:
     *dimension != null
     *obstacles != null
     *obstacles must fit within map bounds"
     
     class Activity{
         -Route route
         -Gear gearUsed
         -LocalDate date
         -double distance
         -int durationMinutes
         -String name
         +Activity(Route route, Hear gearUsed, LocalDate date, double distance, int durationMinutes, String name)
         +Route getRoute()
         +Gear getGearUsed()
         +LocalDate getDate()
         +double getDistance()
         +int getdurationMinutes
         +String getName()
     }
     
     note for Activity "Invariant Properties:
     Invariants: route != null 
     gearUsed != null 
     date != null 
     distance > 0 
     durationMinutes > 0"
     
     class Gear{
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
        +String getName()
        +String getType()
        +String getDescription()
        +String getBikeType()
        +int getNumberOfGears()
    }
    note for Bike "Invariants properties: 
    name != null 
    bikeType != null
    numberOfGears > 0"

    class Helmet {
        -String name
        -String size
        +Helmet(String name, String size)
        +String getName()
        +String getType()
        +String getDescription()
        +String getSize()
    }
    note for Helmet "Invariants properties: 
    name != null 
    size != null"

    class Shoes {
        -String name
        -int size
        -String shoeType
        +Shoes(String name, int size, String shoeType)
        +String getName()
        +String getType()
        +String getDescription()
        +int getSize()
        +String getShoeType()
    }
    note for Shoes "Invariants properties: 
    name != null 
    size > 0 
    shoeType != null"

%% Inheritance/Implementation
    Gear <|.. Bike : implements
    Gear <|.. Helmet : implements
    Gear <|.. Shoes : implements

%% Obstacle relationships
    Obstacle o-- GridPoint : has position
    Obstacle o-- Dimension : has size

%% Route relationships
    Route o-- GridPoint : contains many

%% Map relationships
    Map *-- Dimension : has size
    Map *-- Obstacle : contains many

%% Activity relationships
    Activity *-- Route : has
    Activity o-- Gear : uses


```