# LanternServer [![Build Status](https://travis-ci.org/LanternPowered/LanternServer.svg?branch=master)](https://travis-ci.org/LanternPowered/LanternServer) [![Discord](https://img.shields.io/badge/chat-on%20discord-6E85CF.svg)](https://discord.gg/ArSrsuU)

A open source and compatible Minecraft server that implements the [SpongeAPI]. It is licensed under the [MIT License].

* [Source]
* [Issues]
* [Wiki]

## Prerequisites
* [Java 8]

## Clone
The following steps will ensure your project is cloned properly.

1. `git clone --recursive https://github.com/LanternPowered/LanternServer.git`
2. `cd LanternServer`

## Building
__Note:__ If you do not have [Gradle] installed then use ./gradlew for Unix systems or Git Bash and gradlew.bat for Windows systems in place of any 'gradle' command.

In order to build LanternServer you simply need to run the `gradle build` command. You can find the compiled JAR file in `./build/libs` labeled similarly to 'lanternserver-x.x.x-SNAPSHOT.jar'.

## IDE Setup
__Note:__ If you do not have [Gradle] installed then use ./gradlew for Unix systems or Git Bash and gradlew.bat for Windows systems in place of any 'gradle' command.

__For [Eclipse]__
  1. Run `gradle eclipse`
  2. Run `gradle genEclipseRunConfigurations`
  3. Import LanternServer as an existing project (File > Import > General)
  4. Select the root folder for LanternServer
  5. Check LanternServer when it finishes building and click **Finish**

__For [IntelliJ]__
  1. Make sure you have the Gradle plugin enabled (File > Settings > Plugins)
  2. Click File > New > Project from Existing Sources > Gradle and select the root folder for LanternServer
  3. Select Use customizable gradle wrapper if you do not have Gradle installed.
  4. Once the project is loaded, run `gradle genIntelliJRunConfigurations`
  5. IntelliJ will now ask to reload the project, click **Yes**

[Eclipse]: https://eclipse.org/
[Gradle]: https://www.gradle.org/
[IntelliJ]: http://www.jetbrains.com/idea/
[Source]: https://github.com/LanternPowered/LanternServer
[Java 8]: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
[Issues]: https://github.com/LanternPowered/LanternServer/issues
[Wiki]: https://github.com/LanternPowered/LanternServer/wiki
[MIT License]: https://www.tldrlegal.com/license/mit-license
[SpongeAPI]: https://github.com/SpongePowered/SpongeAPI