# Inverted Index
Course work for parallel computing course. 
This project is written in [Kotlin language](https://kotlinlang.org/).

## Prerequisites
Before you start, ensure you have the [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/) 
and [Kotlin command-line compiler](https://kotlinlang.org/docs/command-line.html) installed on your machine as it's required 
for compiling and running Kotlin applications.

## Cloning the repository
Open a terminal or command prompt and run the following command:<br>
```git clone https://github.com/ooxyygeen/InvertedIndex```

## Compiling the files
After cloning the project, you need to compile the Kotlin files.
1. **Navigate to the Project Directory:** Use the terminal or command prompt to navigate to the cloned 
project's directory.<br>
```cd your-repo-name```
2. **Compile the Kotlin Files:** Run the following command to compile your Kotlin files:  
   - Compile Client-side:<br>
   ```kotlinc Client.kt -include-runtime -d Client.jar```
   - Compile Server-side:<br>
   ```kotlinc Main.kt Indexer.kt InvertedIndex.kt FileUtils.kt ClientHandler.kt -include-runtime -d InvertedIndex.jar```

## Running the Server and Client
1. **Start the Server:**<br>
```java -jar InvertedIndex.jar <threads>```
Specify the desired number of threads for index construction by replacing `threads` with the 
appropriate number. If you prefer to construct the index sequentially without parallel 
processing, simply leave this field empty.
2. **Start the Client:**<br>
```java -jar Client.jar```
