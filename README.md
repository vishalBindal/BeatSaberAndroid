# BeatSaberAndroid
An android app to detect the current orientation of device for Beat Saber 

## How to use this app
On opening the app, the screen shows headings "Orientation" and "Coordinates".\
Hold the phone by its bottom edge, and tilt it into landscape mode, with the screen facing towards your left hand side.\
Press the volume up or volume down button in this position (call it the mean position).\
Now the screen should show the orientation angles, as well as the (x,y) coordinates of the position of tip of the phone.

## What it shows
- **Orientation angles :**\
The azimuth, pitch and roll angles in degrees.( calculated from Rotation Vector Sensor) \
More about them here: https://google-developer-training.github.io/android-developer-advanced-course-concepts/unit-1-expand-the-user-experience/lesson-3-sensors/3-2-c-motion-and-position-sensors/3-2-c-motion-and-position-sensors.html#devicerotation \
The azimuth value displayed is the difference of the actual azimuth value and the value at the mean position.
- **Coordinates :**\
They represent the coordinates of mouse position if the orientation of the device were to be mapped onto a laptop screen. \
From the mean position ( where the volume up/down button was pressed), \
Rotation of the phone towards right : Positive x\
Rotation of the phone upwards : Positive y\
**NOTE** : The rotation is to be done keeping the bottom edge (from which the phone is to be held) fixed. \
Also, the calculation of (x,y) involves the variable length, which indicates something like the length of phone and distance from laptop screen. length , here , is taken 20 and changing this value will scale the values of x and y accordingly. 
- **SWING message and background colour :**\
When the net angular velocity ( calculated using gyroscope readings ) is greater than a particular value ( taken to be 3 rad/s2 ), the screen background changes to red and a SWING!! message is displayed at the bottom of the screen. 

