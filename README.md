# TErdle Client - Wordle Game

Welcome to TErdle, a command-line driven client for playing the Wordle game. This Java application is developed for instructional purposes by the Java bootcamp TechElevator. The client interacts with a separate [TErdle server](https://github.com/tbeerbower/terdle_server) application by making HTTP requests to the server's RESTful API, utilizing the Spring REST framework.

## Table of Contents
1. [Introduction](#introduction)
2. [TErdle Rules](#wordle-rules)
3. [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
4. [Usage](#usage)
    - [Login](#login)
    - [Main Menu](#main-menu)
    - [Gameplay](#gameplay)
    - [Statistics](#statistics)
5. [Contributing](#contributing)
6. [License](#license)

## Introduction

TErdle is a Wordle game client designed for educational purposes. Players can log in, play daily or random games, view game statistics, and log out.

## TErdle Rules

TErdle is a word puzzle game where the objective is to guess a hidden five-letter word within six attempts. Here are the basic rules:

1. You have six attempts to guess a five-letter word.

2. After each guess, you'll receive feedback in the form of a colored background on the letters of the guess:
   * A green square indicates that a letter is in the correct position.
   * A yellow square indicates that a letter is in the word but in the wrong position.
   * A gray square means that the letter is not in the word at all.
3. You can use the feedback from previous guesses to refine your subsequent guesses and narrow down the possible word choices.

4. You win if you can guess the correct five-letter word within the six attempts.

The specific word to be guessed is typically randomly generated, and players use deductive reasoning and word knowledge to make educated guesses and solve the puzzle. 
## Getting Started

### Prerequisites
- Java (version 11)
- Maven (version 3.8.3)
- Connection to interact with the TErdle Server

### Installation
1. Clone this repository:
   ```bash
   git clone https://github.com/tbeerbower/terdle_client.git
   ```

2. Build the project using Maven:
   ```bash
   cd terdle_client
   mvn clean install
   ```

## Usage

### Login
1. Run the application:

2. Upon launch, you will be presented with the Login Menu. Enter `1` to log in and provide valid credentials.

### Main Menu
After successful login, the Main Menu provides various options:
- Play Daily Game
- Play Random Game
- Show Game Statistics
- Admin Menu
- Log Out

### Gameplay
During gameplay, users enter guesses based on Wordle rules. The application color-codes the letters of the guess according to the Wordle rules.

Example:
```bash
Enter guess number 1: train
                         
  T    R    A    I    N  
  
Enter guess number 2: sharp
                         
  T    R    A    I    N                     
  S    H    A    R    P  

Enter guess number 3: heard
                         
  T    R    A    I    N  
  S    H    A    R    P    
  H    E    A    R    D                        

Enter guess number 4: hoard
                         
  T    R    A    I    N       
  S    H    A    R    P       
  H    E    A    R    D                           
  H    O    A    R    D  
                         
***You got it in 4 tries!***      
```

### Statistics
Players can view their game statistics, including the date, word, last guess, number of guesses, and game type.

Example:
```bash
+------------+------------+------------+------------+------------+
|    Date    |    Word    | Last Guess |  Guesses   |    Type    |
+------------+------------+------------+------------+------------+
| 2023-10-19 |   filly    |   filly    |     5      |   RANDOM   |
+------------+------------+------------+------------+------------+
| 2023-10-19 |   hound    |   hound    |     2      |   RANDOM   |
+------------+------------+------------+------------+------------+
...

+-------------------+-------------------+
|   Games started   |        19         |
+-------------------+-------------------+
|  Games completed  |        18         |
+-------------------+-------------------+
|     Games won     |        15         |
+-------------------+-------------------+
|    Games won %    | 83.33             |
+-------------------+-------------------+
|  Average Guesses  | 4.56              |
+-------------------+-------------------+
```

## Contributing

Contributions are welcome! For major changes, please open an issue first to discuss what you would like to change.

## License

This project is licensed under the [MIT License](LICENSE).