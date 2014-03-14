/* LIBRARIES */

import codeanticode.syphon.*; //Import the Syphon library
import themidibus.*; //Import the library
// import processing.serial.*; // Import the serial library
PGraphics canvas;
SyphonServer server;


/* SCREEN AND CANVAS */

int xScreenSize = 2400;
int yScreenSize = 480;

/* MIDI */

MidiBus myBus; // The MidiBus

/*
int channel = 0;
int pitch = 0;
int velocity = 127;

int whistleSoundA = 1;
int whistleSoundB = 3;

int leftClicksStartNoteA = 10;
int leftClicksStartNoteB = 20;

int leftClicksRange = 7;

int rightClicksStartNoteA = 30;
int rightClicksStartNoteB = 40;

int rightClicksRange = 7;
*/

/* LINE GENERATION */

int lineLimit = 2000; // How many lines can be generated. Needs to be modified to either have the lines
                      // be 'recycled' after the limit is reached. Also the limit needs to be increased.
int lineCountLimit = 500;

PassingLines[] lines = new PassingLines[lineLimit]; // Declare the object
int linecount = 1; // Keeps track of how many line instances have been created



void setup() {

  size(xScreenSize, yScreenSize, P3D);
  background(0);

  // myBus = new MidiBus(this, 0, 1);
  myBus = new MidiBus(this, "VezŽr Midi Out ", "Bus 1");


  // String portName = Serial.list()[9]; // Currently uses direct serial communication. To be replaced with wifi
  // myPort = new Serial(this, portName, 9600);

  canvas = createGraphics(xScreenSize, yScreenSize, P3D); // Required for Syphon output
  server = new SyphonServer(this, "Processing Syphon"); // Syphon server

  for (int i = 0; i < lines.length; i++) { // Initialize objects for the lines
    lines[i] = new PassingLines(); // PassingLine object with no parameters declared
  }

}


/* THE LINE OBJECTS */

class PassingLines {

  float xTop = 0;   // Top horizontal position of the line
  float xBot = 0;   // Botom horizontal positon of the line
  // ... in this case xTop and xBot are the same value depending on whether they are moving from left or from right
  float yTop = 0;   // Top vertical position of the line
  float yBot = 0;   // Bottom vertical position of the line
  float speed = 0.5;  // Speed of the movement of the line
  int isMoving = 0; // Determines if the line is moving and what direction it is moving in

  void draw() {

      if ( isMoving == 1 ) {    // If the value of isMoving is 1 then...
        if ( xTop > width ) {   // If the line moves off the canvas then...
          isMoving = 0;         // ... disable its animation
          xTop = -5;            // ... and put it off the screen
          xBot = -5;
        } else {
          xTop = xTop + speed;  // ... move it from LEFT to RIGHT based on
          xBot = xBot + speed;  // ... its current position PLUS 1 pixel
        }
      }

      if ( isMoving == 2 ) {    // If the valude of isMoving is 2 then...
        if ( xTop > width ) {
            isMoving = 0;
            xTop = -5;
            xBot = -5;
        } else {
           xTop = xTop + speed;  // ... move the line from RIGHT to LEFT
           xBot = xBot + speed;  // based on its current position PLUS 1 pixel
         }
       }
       canvas.line(xTop,yTop,xBot,yBot); // Draws the line to the canvas
  }

  void startMovingRight() {  // Initiate the object to start moving from the LEFT edge of the canvas

    isMoving = 1;  // The line must start moving from LEFT side of the canvas
    xTop = 0;
    xBot = 0;
    yTop = 0;      // The line is drawn from the top of the canvas down to the half point
    yBot = 240;

  }

   void startMovingLeft() {   // Initiate the object to start moving from the RIGHT edge of the canvas

    isMoving = 2;   // The line must start moving from RIGHT side of the canvas
    xTop = 0;
    xBot = 0;
    yTop = 240;     // The line is line is drawn from the half point of the canvast all the way to the bottom
    yBot = 480;

  }
}


void draw() {

  canvas.beginDraw();
  canvas.background(0);
  canvas.strokeWeight(3); // Sets the stroke weight (could be random?)
  canvas.stroke(255);

  // Call the draw function within the line object
   for ( int i = 0; i < lineLimit; i++ ) {

     lines[i].draw();

   }

  canvas.endDraw();
  image(canvas, 0, 0);
  server.sendImage(canvas);

}

void noteOn(int channel, int pitch, int velocity) {

  // println( channel  );

   if ( channel == 3 ) { // Sent from channel 4 in Vezer
      if ( pitch > 0 ) {
        linecount = linecount + 1;
        lines[linecount].startMovingLeft();
      }
   }

   if ( channel == 4 ) {  // Sent from channel 5 in Vezer
     if ( pitch > 0 ) {
        linecount = linecount + 1;
        lines[linecount].startMovingRight();
     }
   }

   /*
   println("Channel "+ channel);
   println("Pitch "+ pitch);
   println("Velocity "+velocity);
   */

}

// Created in order to test line generation in case the sensors are not present

/*
void keyPressed() {
  if (key == 'z') {
    linecount = linecount + 1;
    lines[linecount].startMovingRight();
    myBus.sendNoteOn(channel, int(random( leftClicksStartNoteA, ( leftClicksStartNoteA + leftClicksRange ) ) ), velocity);
  }

  if (key == 'x') {
    linecount = linecount + 1;
    lines[linecount].startMovingLeft();
    myBus.sendNoteOn(channel, int(random( rightClicksStartNoteA, ( rightClicksStartNoteA + rightClicksRange ) ) ), velocity);
  }
}
*/