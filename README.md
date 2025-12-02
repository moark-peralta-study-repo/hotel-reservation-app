# Hotel Reservation GUI

A simple desktop application for managing hotel reservations, built in Java with a GUI and SQLite database.

## Prerequisites

- Java JDK 17+
- An IDE (IntelliJ IDEA recommended)
- **Git** (required for cloning the repository)

> **Windows users:** Download and install Git from [https://git-scm.com/download/win](https://git-scm.com/download/win)

## Setup

### 1. Clone the repository
Open a terminal (Linux/Mac) or PowerShell/Command Prompt (Windows) and run:

```bash
https://github.com/moark-peralta-study-repo/hotel-reservation-app.git
cd hotel-reservation-gui
```

### 2. Build the project using the Gradle wrapper

**Linux / Mac:**

```bash
./gradlew build
```

**Windows (Command Prompt or PowerShell):**

```cmd
gradlew.bat build
```

> The Gradle wrapper automatically downloads the required SQLite JDBC dependency.

### 3. Run the application

**Linux / Mac:**

```bash
./gradlew run
```

**Windows:**

```cmd
gradlew.bat run
```

### 4. Open the project in your IDE
- Open the project folder in IntelliJ IDEA (or your preferred IDE).
- Let Gradle sync automatically.

## Database

- Uses SQLite (`hotel.db`) located in the `resources` folder.
- No additional setup is needed; the database file is included.

## Updating the Project

To get the latest changes from the repository:

1. Open a terminal or PowerShell in the project folder.
2. Run:

```bash
git pull
```

3. Rebuild and run the project using the Gradle wrapper as shown above.
