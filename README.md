# Tracklin

[![build](https://github.com/jmp/tracklin/workflows/build/badge.svg)](https://github.com/jmp/tracklin/actions?query=workflow%3Abuild)

A small time tracker written in Kotlin, using JavaFX. It is currently a bit
limited, but can do basic time tracking of tasks and export the results into
a CSV file.

## Features

* Start and stop recording time spent on tasks
* Fast switch to a different task
* Detect idle time (5+ minutes) and based on user's choosing
  * assign the idle time to a different task, or
  * keep the idle time allocated to the current task
* Export recorded times to a CSV file

## Compiling

Build with Gradle:

    gradlew shadowJar

This creates an executable JAR at `build/libs/tracklin.jar`.

## Usage

Run the executable JAR:

    java -jar tracklin.jar

## History

Tracklin started as a hobby project written in [Python][1] and [Tkinter][2].
It was originally called "Snacker", a weird portmanteau of "snake" (because
Python is a snake) and "tracker" (because it's a time tracker). I eventually
[rewrote][3] it in Java to learn a bit about [JavaFX][4], but kept the name.

This repository contains the latest iteration of the time tracking tool,
written in [Kotlin][5]. I changed the name to "Tracklin".

[1]: https://python.org
[2]: https://wiki.python.org/moin/TkInter
[3]: https://github.com/jmp/snacker
[4]: https://github.com/openjdk/jfx
[5]: https://kotlinlang.org
