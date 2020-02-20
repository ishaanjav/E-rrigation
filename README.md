# E-rrigation


  <img src="https://github.com/ishaanjav/E-rrigation/blob/master/Neighborhood%20Water%20Wastage%201.JPG" width="530" alt="Water Wastage on a Farm" align="right">
  
 Every day, ***more than 9 billion gallons*** of water are used for irrigation in the U.S. of which approximately **50% is wasted** due to inefficient irrigation systems.
In fact, water wastage is even common in our neighborhoods and cities.

**E-rrigation is a mobile application that serves as a sprinkler system designer.** Users create designs by plotting land/sprinklers, and specifying additional info. Then, the app calculates crucial statistics about a given design plan in order to optimize water usage *before* the sprinklers are put into place.

It does this by **eliminating overflowing and repetitive watering** of the same regions.
By providing these statistics, E-rrigation reduces water usage even *before* sprinklers are put into place: cutting down on expenditures in the long run.

- [Demonstration / Features](https://github.com/ishaanjav/E-rrigation/blob/master/README.md#demonstration-and-features)
- [Code](https://github.com/ishaanjav/E-rrigation/blob/master/README.md#code)

<img src="https://github.com/ishaanjav/E-rrigation/blob/master/Demonstration%20GIF.gif" width="250" align="right">

- [Applications](https://github.com/ishaanjav/E-rrigation/blob/master/README.md#applications)
- [Water Waste Images](https://github.com/ishaanjav/E-rrigation/blob/master/README.md#water-waste-images)

-----



## Demonstration and Features

Using the app is easy: Users simply have to plot their land area and sprinklers by tapping on the screen. **However, the calculations that take place beneath the hood are a lot more complex.** 


 E-rrigation must be able to distinguish between whether the overlap of 2 sprinklers is outside the land or inside the land or **both**.
 
 It must also detect a special case of 3 circles overlapping vs the normal case *(both of which require lots of mathematical calculations)* 
 
It also has to determine which side of a sprinkler is overflowing.

<p align="center">
<img src="https://github.com/ishaanjav/E-rrigation/blob/master/Special%20Sprinkler%20Cases%20App%20Calculates.PNG"
width="96%"/>
  </p>

###### Above is a diagram illustrating just a few of the *irregular* cases that the app has to account for. 

-----
## Code
Below are links to access some of the important files containing code for the application.

- [**MainActivity.java -**](https://github.com/ishaanjav/E-rrigation/blob/master/app/src/main/java/com/example/anany/drawingrectangles/CleanMainActivity.java) Code for plotting land, sprinklers, adjusting angle/radius, and more. ***Also contains calculations** and displaying of results*.
- [**activity_main.xml -**](https://github.com/ishaanjav/E-rrigation/blob/master/app/src/main/res/layout/activity_main.xml)  Layout for app's main screen.
- [**additional_info.xml -**](https://github.com/ishaanjav/E-rrigation/blob/master/app/src/main/res/layout/result.xml) Dialog that pops up to ask users for additional information.
- [**result.xml -**](https://github.com/ishaanjav/E-rrigation/blob/master/app/src/main/res/layout/real_result.xml) Dialog box that displays statistics to users.

-----
## Applications
As shown by the [images below](https://github.com/ishaanjav/E-rrigation/blob/master/README.md#water-waste-images), water wastage through inefficient sprinkler placement is a global issue:

- **Agricultural Use:** Since farms use a large quantity of water, farmers can more efficiently plot sprinklers on a large-scale using E-rrigation.
- **Civic Use:** Water wastage is common near neighborhoods, cities, and even schools. Using E-rrigation, the city can cut down on its budget by effectively placing sprinklers.
- **General Calculations:** At its core, E-rrigation is an app that performs complex calculations about areas of multiple circles and lines. Because of the lack of software that calculates areas like this, E-rrigation can be used.


-----
## Water Waste Images

<img src="https://github.com/ishaanjav/E-rrigation/blob/master/Agricultural%20Water%20Wastage%201.jpg" width="46%" alt="Agricultural Water Wastage 1 (multiple close sprinklers)" align="left">
<img src="https://github.com/ishaanjav/E-rrigation/blob/master/Agricultural%20Wastage%202.png" width="47%" alt="Close Sprinklers on a Farm (Agricultural Wastage 2)" align="right">

<img src="https://github.com/ishaanjav/E-rrigation/blob/master/Agricultural%20Wastage%203.png" width="46%" alt="Agricultural Water Wastage 3" align="left">
<img src="https://github.com/ishaanjav/E-rrigation/blob/master/Neighborhood%20Water%20Wastage%201.JPG" width="47%" alt="Neighborhood Water Wastage 1 (LOTS of water on street)" align="right">

<img src="https://github.com/ishaanjav/E-rrigation/blob/master/Agricultural%20Wastage%202.png" width="46%" alt="Neighborhood Water Wastage 2" align="left">
<img src="https://github.com/ishaanjav/E-rrigation/blob/master/Neighborhood%20Wastage%203.jpg" width="47%" alt="Neighborhood Water Wastage 3" align="right">

<img src="https://github.com/ishaanjav/E-rrigation/blob/master/Neighborhood%20Wastage%204.jpg" width="46%" alt="Neighborhood Water Wastage 4" align="left">
<img src="https://github.com/ishaanjav/E-rrigation/blob/master/Neighborhood%20Wastage%205.JPG" width="47%" alt="Neighborhood Water Wastage 5" align="right">



