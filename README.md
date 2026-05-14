# OOP_Game
Title: Run the Tale  

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge\&logo=java\&logoColor=white)
![Swing](https://img.shields.io/badge/GUI-Java%20Swing-blue?style=for-the-badge)
![OOP](https://img.shields.io/badge/Architecture-OOP-green?style=for-the-badge)
![Status](https://img.shields.io/badge/Project-Completed-brightgreen?style=for-the-badge)

A Java 2D endless-runner style game built using **Java Swing** with a fully object-oriented architecture, custom rendering system, and real-time game loop.

---

##  Overview

RunTheTale is a simple 2D obstacle-avoidance game where the player must jump over moving cars before the timer runs out. It demonstrates clean OOP design, game loop mechanics, and GUI-based rendering using Java Swing.

---

##  Game Flow

```
RunTheTale (Main)
      ↓
FirstUI (Login / Welcome)
      ↓
SecondUI (Gameplay)
      ↓
ThirdUI (Results Screen)
```

---

##  Project Structure

###  Core Architecture

* Abstract base class for screens
* Abstract base class for game objects
* Separation of UI, logic, and rendering

---

##  UI SYSTEM

### 1. FirstUI

* Welcome screen
* Username input
* Starts the game

```java
new secondUI(username).display();
```

---

### 2. SecondUI (Main Game)

Handles:

* Game loop
* Player movement
* Obstacles (cars)
* Collision detection
* Score & timer

---

### 3. ThirdUI (Results Screen)

Displays:

* Win/Lose result
* Score
* Username
* Retry / Exit options

---

##  CORE CLASSES

###  GameScreen (Abstract UI Base)

```java
abstract class GameScreen extends JFrame
```

Used as the base for all UI screens.

Requires:

```java
public abstract void display();
```

---

###  GameEntity (Abstract Game Object)

```java
abstract class GameEntity
```

Represents all objects in the game world:

* Player
* Cars

Contains:

* Position (x, y)
* Size (width, height)
* Update & render logic

---

##  Player System

```java
class Player extends GameEntity
```

### Features:

* Jump mechanics
* Gravity simulation
* Collision detection
* Stickman rendering

### Core Logic:

* velocity-based jumping
* gravity pull
* ground reset system

---

##  Car System

```java
class Car extends GameEntity
```

### Features:

* Moving obstacles
* Randomized types:

```
sedan
suv
sports
```

### Behavior:

* Moves left continuously
* Resets after leaving screen

---

##  RENDERING ENGINE

```java
class GamePanel extends JPanel
```

Responsible for all graphics rendering.

Draws:

* Background (sky, clouds)
* Road
* Lane markings
* Player
* Cars

Uses:

```java
paintComponent(Graphics g)
```

---

##  GAME LOOP

Runs at ~60 FPS:

```java
new Timer(16, e -> { ... });
```

Each frame:

* Update player
* Update cars
* Check collisions
* Repaint screen

---

##  COLLISION SYSTEM

```java
playerBox.intersects(car.getBounds())
```

If collision occurs:

* Game ends immediately

---

##  TIMER SYSTEM

Runs every second:

```java
new Timer(1000, e -> { ... });
```

If time reaches zero:

* Player wins

---

##  MAIN CLASS

```java
public class RunTheTale
```

### Entry Point:

```java
new firstUI().display();
```

---

##  GAMEPLAY SUMMARY

```
Start Game
   ↓
Enter Username
   ↓
Play Game
   ↓
Jump Over Cars
   ↓
Collision → Lose
   ↓
Timer Ends → Win
```

---

##  TECHNICAL HIGHLIGHTS

* ✔ Object-Oriented Design
* ✔ Inheritance & Abstraction
* ✔ Real-time Game Loop
* ✔ Custom Swing Rendering
* ✔ Event-driven Programming
* ✔ Collision Detection System

---

##  HOW TO RUN

1. Open project in **Apache NetBeans**
2. Run:

```
RunTheTale.java
```

3. Play the game 

##  NOTES

* Built with Java Swing (no external game engine)
* Lightweight 2D game architecture
* Designed using proper OOP principles
* Timer-based FPS system (~60 FPS)






