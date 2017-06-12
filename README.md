# STeamAPIdisplay

## Overview
Application uses Riot Games API and Nokia Display handled by STM32 Discovery. 
Main idea of this project is to let League of Legends players check divisions of their opponents 
and teammates faster and more convenient than by using traditional webservices.
You just type in your nickname and data is shown on display.

## Description
High-level part of this project is written in Java. Data is recived from server as JSON.
After it'p processed, it's send by VCP to stm32. 
Display handle is driven by SPI and based on libraries by Tilen Majerle (link below).

## Tools
  Eclipse
  CooCox IDE

## Connections
RES   <->	  PC15	</br>
SCE	  <->	  PC13	</br>
DC	  <->	  PC14	</br>
D/IN  <->	  PC3	</br>
CLK   <->	  PB10	</br>
+V    <->		3.3V		</br>
GND   <->		GND	</br>

## How to compile
  Open and build file stm/STM32DiscoveryVCP.coproj via CooCox IDE.
  Download program on stm board.
  
## How to run
  Plug in microUSB and miniUSB to your PC, open files from java/src/ in your IDE and build it.
  Select class Main and run it.

## Attributons
 https://github.com/xenovacivus/STM32DiscoveryVCP
 https://stm32f4-discovery.net/pcd8544-nokia-33105110-lcd-stm32f429-discovery-library/
 http://rxtx.qbang.org/wiki/index.php/Main_Page
 
 MIT License
 
## Author
Erwin Majewski

The project was conducted during the Microprocessor Lab course held by the Institute of Control and Information Engineering, Poznan University of Technology. 
Supervisor: Tomasz Mańkowski
