# Tracklin

[![CI](https://github.com/jmp/tracklin/workflows/CI/badge.svg)](https://github.com/jmp/tracklin/actions?query=workflow%3ACI)

A small time tracker written in Kotlin, using JavaFX. It is currently very
limited, but can do basic time tracking of tasks and export the results into
a CSV file.

## Compiling

Build with Gradle:

    gradlew shadowJar

This creates an executable JAR at `build/libs/tracklin-*.jar`.

## Usage

Run the executable JAR:

    java -jar tracklin-*.jar
