# Intelligent Elevator

This project implements the Intelligent Elevator System (IES) showed
in the specification.

## Project Structure

Configuration file
- `etc/configurations.cfg
Source codes
- `src/MyApp/Controller` This folder contains `ElevatorController`
  `MainController` that are responsible for controlling the overall
  system.`Control Panel`is for system general display.
- `src/MyApp/Kiosk`: Implements the Kiosk Object and GUI.
- `src/MyApp/Elevator`: Implements the Elevator Object and GUI.
- `src/MyApp/SecurityRoom`: Implements the Administrator Panel.
- `src/MyApp/Timer`: Simulation of clock.
- `src/MyApp/misc`: Include objects responsible for message transfer.
- `src/MyApp/test` Unit Testing test cases for Controllers.
External Library
- `lib/`


## Build

You can build the project using `ant`.

```
ant
```

The project should then handle building itself.

## Continuous Integration

Use your favourite CI tool ([Hydra](http://nixos.org/hydra/), for
example) to build `build.x86_64-linux` path in the `release.nix` file.

```
nix-build release.nix -A build.x86_64-linux
```

[Nix](https://nixos.org/nix/) will help you to fetch dependencies
(`ant` and `openjdk`) if you don't have them.

## Copyright

Sisi Rao, Ruixian Liang, Wei Tang, Billy Choi, Yu Nang Mo
(c) 2016. All rights reserved. Redistribution as course materials or
for other educational purpose is not permitted.
