/* written by dimitris platis, jiaxin li, pedram shirinbolaghi, shanlin tong */

#include <Servo.h> 
#include <AFMotor.h>
AF_DCMotor motorLeft1(1);
AF_DCMotor motorLeft2(2);
AF_DCMotor motorRight1(3);
AF_DCMotor motorRight2(4);

/* input stream */
int incomingByte[2];
String inputString = "";         // a string to hold incoming data
boolean inputComplete = false;
boolean tableComplete = false;  // whether the speed packet is complete
int packageIndex = 0;
int motorSpeed = 1;     //variable to set stepper speed STABLE for now

int wheelDegrees = 90;

/* automation mode*/
boolean automationCommand = false;
boolean automationMode = false;
/* ping */
long previousPing = 0;
long pingInterval = 200;
int pingID = 0;
int pingpong[5];
int failedPingCount = 0;
int failLimit = 5;
//int connectionLED = 40;
boolean forcedPong = false;
/* ---- */

/* PTZ movement */
int centerY = 90;
int centerX = 90;
int verticalDegrees = 90;
int horizontalDegrees = 90;
int horizontalPin = 10;
int verticalPin = 9;
Servo verticalServo, horizontalServo;

/* ----- */

/* ----------- PARKING -------*/

/* ultra sound stuff */
int echoPin = 43; // Echo Pin
int trigPin = 42; // Trigger Pin
int maximumRange = 210; // Maximum range needed
int minimumRange = 5; // Minimum range needed
long duration, distance; // Duration used to calculate distance
int sonarInterval = 65;
long sonarPrevMillis = 0;
/* measurements and quality */
boolean scannerFirst = false;
int measuredDistances[5]; //array to include measured distances at a specific angle
int distancesIndex = 0;
int totalMeasurements[9][3]; //array to include the average of all measurements made in a cycle (3 sweeps)
int cycleId = 0;
boolean scanComplete = false;
boolean startScan = false;
/* obstacles and parking*/
#include <Math.h> 
int obstacles[9];
boolean parkingDetectionComplete = false;
int wallLimit = 115; //more than that, ultra sound sensor is relatively inaccurate when trying to detect distinct objects 
boolean parkingSpotFound = false;
int parkingSpot[2][2]; //array that includes the parking spot coordinates. first column includes the angles, second includes the distances
int wall[2][2]; //an array to contain the wall coordinates
boolean wallFound = false;
int parkingSpotX, parkingSpotY; //the exact coordinates for the parking spot
boolean triangulated = false;
float verticalDistance,offsetDistance; //vertical distance between the vehicle and the line of wall or gap and offset between the vehicle and the line
int degreesToParallelize, distanceToMiddle;
/* servo stuff */
Servo scanner;
int scannerPin = 41;
int scannerStep = 15;
int scannerPos = 0;
int scannerInterval = 325;
long scannerPrevMillis = 0;
int scannerAngles[9] = {
  30,45,60,75,90,105,120,135,150};

/* ----- */
/* GYROSCOPE & SPEED ENCODER
 * for automated processes */

#include <MPU6050.h> //gyroscope
#include <I2Cdev.h> //gyroscope
#include <Wire.h>//library for gyroscope

# define angle_tolerance 2 //tolerance to rotation error
# define distance_tolerance 2 //tolerance to distance error
#define turningSpeed 200 //speed to rotate
#define movingSpeed 180 //speed to move
//====================
MPU6050 accelgyro;

int16_t ax,ay,az;//raw data from accelerometer, not used directly
int16_t gx,gy,gz;//raw data from accelerometer, not used directly
float ggz = 0; //proccessed data for z-axis rotation
float Sx = 0.0; //distance moved (from encoder)

float Gz;//rotation in degrees per second on the z-axis (from gyro)
float Gz_offset =-1.79748;//-0.88 0.5672 0.3228 0.10712 (determined experimentally)
int angle=0; //orientation of the car at that moment
int angle_error=0;
int turning_angle = 0;
int distance_error=0;
int moving_distance=0;
int angle_factor = 180; // used for maping the angle (determined experimentally)
int distance_factor = 490;// used for maping the distance from pulseCount (determined experimentally)
int pulseCount = 0; //from the speed encoder
int goTimeout = 2000; //timeout for the "go" function
int turnTimeout = 2000; //timeout for the turn function
long LastTime,NowTime,TimeSpan;//for intergration of the gyroscope
/* ------- */

/* status RGB LED stuff */
int lowPin = 29; //instead of ground
int R_led = 27;
int G_led = 25;
int B_led = 23;
int automationBLUE = 1;
int notConnectedRED = 4;
int connectedGREEN = 2;
/* ----- */

/* infra red track following */
/* 4 IR sensors are placed on the back of the vehicle
 * and below are the pins they are connected to */
#define infraredAutoSpeed 70
#define infraredLeft0 49      // the leftmost IR sensor pin
#define infraredRight0 48 // the rightmost IR sensor pin
#define infraredLeft1 47 
#define infraredRight1 46
int ultraSoundDistance; //distance measured by the ultra sound sensor, used to stop the vehicle
int stopDistance = infraredAutoSpeed/2; //distance to stop the car
int autoLeftSpeed = 0; //speed of the left and right side while on following track mode
int autoRightSpeed = 0; 
int leftInfraredData[2]; //array to store left infrared sensor data
int rightInfraredData[2];//array to store right infrared sensor data
int turningDelay = 20; // delay for simple turning , adjust the parameter to control the turning
boolean followTrack = false;
/* ---- */
/* IR distance sensor */
int IRpin = A10;
/* ---- */

/* EXPLORE */
boolean startExplore = false; //the exploring function trigger
int ClearDistance = 50;
boolean isFrontClear;
boolean isRightClear;
boolean isLeftClear;
int xcoordinate=0;
int ycoordinate=0;
int sonarOrientation=0; //current scanner position
int carOrientation=0; //current car orientation. in our implementation 0 is facing upwards and clockwise is positive
int exscannerAngles[13] = {
  0,15,30,45,60,75,90,105,120,135,150,165,180 };
int exobstacles[13]; //array to store possible obstacles at the various scanner positions

void setup() {
  // initialize serial:
  Serial.begin(9600);
  Serial2.begin(9600);
  motorLeft1.run(RELEASE);
  motorLeft2.run(RELEASE);
  motorRight1.run(RELEASE);
  motorRight2.run(RELEASE);

  /* initialize pingpong array, everything to 0 */
  for (int i=0; i<5; i++){
    pingpong[i] = 0;
  }
  //  pinMode(connectionLED,OUTPUT);
  //  digitalWrite(connectionLED,LOW);

  /* ptz initialization */
  horizontalServo.attach(horizontalPin);
  verticalServo.attach(verticalPin);
  centerPTZ();

  /* parking */
  /* ultra sounds */
  Serial.begin (9600);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  /* servo stuff */
  scanner.attach(scannerPin);
  scanner.write(scannerStep);
  /*----*/

  /* gyroscope and speed encoder */
  Wire.begin();
  //     Serial2.println(accelgyro.testConnection() ? "MPU6050 connection successful":"MPU6050 connection failure");

  accelgyro.initialize();

  /* ----- */
  /* status LED initialization */
  pinMode(lowPin,OUTPUT);
  digitalWrite(lowPin,LOW); //we set it as low instead of using ground
  pinMode(R_led,OUTPUT);
  pinMode(G_led,OUTPUT);
  pinMode(B_led,OUTPUT);
  statusLight(notConnectedRED);
  /* ----- */

}

void loop() {
  if (tableComplete) {
    wheelMovement();    

    tableComplete = false;

  }

  if (inputComplete){
    processCommand();
    inputComplete = false; //go in here once per command 
  }

  //  ping();
  //  if (!connectionExists()){
  //    //if connection does not exist motors shouldn't move
  //    incomingByte[0] = 0;
  //    incomingByte[1] = 0;
  //    stopMotors();
  //    //and LED that indicates connection should be off
  ////    statusLight(notConnectedRED); 
  //    centerPTZ();
  //  }else if (automationMode){
  ////    statusLight(automationBLUE);
  //  }



  /* parking */
  if (startScan) scanForObjects();
  if (scanComplete && !parkingDetectionComplete){
    findParkingSpot();

  }
  //if detection is complete and a parking spot is found do:
  if (parkingDetectionComplete && parkingSpotFound && !triangulated){
    obstaclePositioning("parkingSpot");
    turn(180); //turn to the front
    Serial.println("turning to front");
    turn(degreesToParallelize);
    Serial.print("extra degrees: ");
    Serial.println(degreesToParallelize);
    go(distanceToMiddle);
    Serial.print("going towards the middle: ");
    Serial.println(distanceToMiddle);
    turn (-degreesToParallelize);
    go(verticalDistance);
    Serial.print("going forward: ");
    Serial.println(verticalDistance);
    automationMode = false;
  }

  else if(parkingDetectionComplete && wallFound && !triangulated){
    obstaclePositioning("wall");
  }

  //track following
  if (followTrack){
    getDistanceSimple( );
    isCloseStop( );
    irRead( );
    adjustMovement();
  }

  if(startExplore){
    explore();
  }
}

void ping(){
  //ping should happen only every pingInterval
  unsigned long currentMillis = millis();
  if(currentMillis - previousPing > pingInterval) {
    String id = String(pingID);
    String ping = "ping";
    String pingOut = ping + id;

    if (pingpong[pingID] == 0){ // if that ping is not sent (0), send it
      pingpong[pingID] = 1; //ping sent
      //send ping + timestamp through serial port
      Serial.println(pingOut);
    }
    else{ //if however that ping is already sent but has not received any answer
      //restore it, don't ping, but keep track of this failure
      forcedPong = true;
      pong(pingID);
      if (failedPingCount<failLimit){
        //no need to raise the failed attempts over the fail limit
        failedPingCount++;
      }

    }
    pingID = (pingID + 1) % 5;
    previousPing = currentMillis;
  }
}

void pong(int id){
  if (pingpong[id] != 0){
    pingpong[id] = 0;
    //    Serial.print("id: ");
    //    Serial.println(id);
    //if we received a pong that means that the connection is on
    if (!forcedPong && !followTrack){
      statusLight(connectedGREEN); 
      //reset failed ping count if ping sent
      failedPingCount = 0;
    }
  }
  else{
    //this shouldn't happen and we could throw an error here
  }
}

boolean connectionExists(){
  if (failedPingCount>=failLimit){
    //connection obviously doesn't exist after we passed the limit
    //to ping
    return false;
  } 
  else{
    return true;
  }
}

void stopMotors(){
  motorLeft1.run(RELEASE);
  motorLeft2.run(RELEASE);
  motorRight1.run(RELEASE);
  motorRight2.run(RELEASE);
}

void processCommand(){
  /* pong */
  if (inputString.startsWith("pong")){
    //the pongID should be at the end of the string
    String pongStamp = String(inputString.charAt(inputString.length()-1));
    int pongID = pongStamp.toInt();
    forcedPong = false;
    pong(pongID);
  }
  else if (inputString.startsWith("ver")){
    //the degrees of how much to turn should be right after
    //the "ver" prefix, so starting at the 4th char and onwards
    String degreesStamp = inputString.substring(3);
    int movementDegrees = degreesStamp.toInt();
    ptzVerticalMove(movementDegrees);
  }
  else if (inputString.startsWith("hor")){
    //same as above
    String degreesStamp = inputString.substring(3);
    int movementDegrees = degreesStamp.toInt();
    //  Serial.println(movementDegrees);
    ptzHorizontalMove(movementDegrees);
  }
  else if (inputString.equals("centercamera")){
    centerPTZ();
  }
  else if (inputString.equals("park")){
    startScan = true;
    scanComplete = false;
    parkingDetectionComplete = false;
    parkingSpotFound = false;
    triangulated = false;
    wallFound = false;
    automationMode = true;
    statusLight(automationBLUE);
  }
  else if (inputString.equalsIgnoreCase("followTrack")){
    followTrack = true;
    scanner.write(90);
    statusLight(automationBLUE);
    automationMode = true;
  }
  else if (inputString.startsWith("go")){
    //same as above
    String distanceStamp = inputString.substring(2);
    int movementDis = distanceStamp.toInt();
    //  Serial.println(movementDegrees);
    go(movementDis);
  }
  else if (inputString.startsWith("turn")){
    //same as above
    String turnStamp = inputString.substring(4);
    int rotateDegrees = turnStamp.toInt();
    //  Serial.println(movementDegrees);
    turn(rotateDegrees);

  }
  else if (inputString.equalsIgnoreCase("explore")){
    startExplore = true; 
  }
  else if (inputString.equalsIgnoreCase("scan")){
    scan();
  }
  else if (inputString.equalsIgnoreCase("Stop")){
    startExplore = false;
  }
}

void ptzVerticalMove(int vDegrees){
  verticalDegrees += (-vDegrees);
  verticalDegrees = constrain(verticalDegrees, 20, 170);
  verticalServo.write(verticalDegrees);
}

void ptzHorizontalMove(int hDegrees){
  horizontalDegrees += (-hDegrees);
  horizontalDegrees = constrain(horizontalDegrees, 10, 170);
  //using minus so to reflect the actual joystick movement
  horizontalServo.write(horizontalDegrees);
}

void centerPTZ(){
  horizontalServo.write(centerX);
  verticalServo.write(centerY); 
}

void wheelMovement(){

  //left wheels
  //scale it from 8. depending on your input
  int leftSpeed = map(abs(incomingByte[0]),0,8,0,255);       
  if (incomingByte[0]>0){
    motorLeft1.run(FORWARD);
    motorLeft2.run(FORWARD);

  }
  else{
    motorLeft1.run(BACKWARD);
    motorLeft2.run(BACKWARD);
  }
  motorLeft1.setSpeed(leftSpeed);
  motorLeft2.setSpeed(leftSpeed);

  //right wheels
  //scale it from 8. depending on your input
  int rightSpeed = map(abs(incomingByte[1]),0,8,0,255);
  if (incomingByte[1]>0){
    motorRight1.run(FORWARD);
    motorRight2.run(FORWARD);         
  }
  else{
    motorRight1.run(BACKWARD);
    motorRight2.run(BACKWARD);  
  }
  motorRight1.setSpeed(rightSpeed);
  motorRight2.setSpeed(rightSpeed);
}


void serialEvent() {
  // read the incoming byte:
  incomingByte[packageIndex] = Serial.read();
  char inputChar = (char) incomingByte[packageIndex];
  // if as a char it's an asterisk, toggle automation mode
  if (inputChar == '*'){
    automationCommand = !automationCommand;
  }

  if (!automationCommand){ //manual mode
    if (inputChar != '*'){ //if we are in manual mode but the input char is a package identifier
      // that means it's the end-package identifier so,
      //we don't want to consider it as an input of manual mode
      if (incomingByte[packageIndex] > 127 ){
        //transforming unsigned byte to signed int
        incomingByte[packageIndex] = incomingByte[packageIndex]-256;
      }
      if (packageIndex == 1){
        tableComplete = true;
      }
      packageIndex = (packageIndex + 1) % 2;
    }
    else{
      //when in manual mode but inputChar is a package identifier
      //that means the command string has just been completed
      inputComplete = true;
    }
  }
  else{ //auto mode or ping
    //add the incoming character to a string to form
    //a command. Don't add it if it's the start-package string identifier
    if (inputChar != '*'){
      inputString += inputChar;

    }
    else{
      //if we are in automation mode and the inputChar is a package identifier
      // that means a command string is about to begin.
      inputComplete = false;
      inputString = ""; //empty the inputString so to receive the new one
    }

  }
}

//rpi
void serialEvent2() {
  // read the incoming byte:
  incomingByte[packageIndex] = Serial2.read();
  char inputChar = (char) incomingByte[packageIndex];
  // if as a char it's an asterisk, toggle automation mode
  if (inputChar == '*'){
    automationCommand = !automationCommand;
  }

  if (!automationCommand){ //manual mode
    if (inputChar != '*'){ //if we are in manual mode but the input char is a package identifier
      // that means it's the end-package identifier so,
      //we don't want to consider it as an input of manual mode
      if (incomingByte[packageIndex] > 127 ){
        //transforming unsigned byte to signed int
        incomingByte[packageIndex] = incomingByte[packageIndex]-256;
      }
      if (packageIndex == 1){
        tableComplete = true;
      }
      packageIndex = (packageIndex + 1) % 2;
    }
    else{
      //when in manual mode but inputChar is a package identifier
      //that means the command string has just been completed
      inputComplete = true;
    }
  }
  else{ //auto mode or ping
    //add the incoming character to a string to form
    //a command. Don't add it if it's the start-package string identifier
    if (inputChar != '*'){
      inputString += inputChar;

    }
    else{
      //if we are in automation mode and the inputChar is a package identifier
      // that means a command string is about to begin.
      inputComplete = false;
      inputString = ""; //empty the inputString so to receive the new one
    }

  }
}

void serialEvent3() {
  // read the incoming byte:
  incomingByte[packageIndex] = Serial3.read();
  char inputChar = (char) incomingByte[packageIndex];
  // if as a char it's an asterisk, toggle automation mode
  if (inputChar == '*'){
    automationCommand = !automationCommand;
  }

  if (!automationCommand){ //manual mode
    if (inputChar != '*'){ //if we are in manual mode but the input char is a package identifier
      // that means it's the end-package identifier so,
      //we don't want to consider it as an input of manual mode
      if (incomingByte[packageIndex] > 127 ){
        //transforming unsigned byte to signed int
        incomingByte[packageIndex] = incomingByte[packageIndex]-256;
      }
      if (packageIndex == 1){
        tableComplete = true;
      }
      packageIndex = (packageIndex + 1) % 2;
    }
    else{
      //when in manual mode but inputChar is a package identifier
      //that means the command string has just been completed
      inputComplete = true;
    }
  }
  else{ //auto mode or ping
    //add the incoming character to a string to form
    //a command. Don't add it if it's the start-package string identifier
    if (inputChar != '*'){
      inputString += inputChar;

    }
    else{
      //if we are in automation mode and the inputChar is a package identifier
      // that means a command string is about to begin.
      inputComplete = false;
      inputString = ""; //empty the inputString so to receive the new one
    }

  }
}

/* ------------------------ parking -------------- */


/* determing whether there is or there could be
 a parking spot among the scan results */
void findParkingSpot(){
  boolean beginFound = false;
  boolean endFound = false;
  boolean gapFound = false;
  int gapStart,gapEnd;
  for (int i = 0; i<9; i++){
    //ignore blank spots if we haven't found the beginning or we already found where the gap starts
    if (obstacles[i]<0 && (!beginFound || gapFound)) continue;
    //if we find a non blankspot and a gap isn't found yet, that means that it's (still) the beginning
    if (obstacles[i]>0 &&!gapFound){
      beginFound = true;
      gapStart = i;
      continue;
    }
    //if we found the beginning and we just found a blank spot, then it's the beginning of a gap
    if (obstacles[i]<0 && beginFound){
      gapFound = true;
      continue;
    }
    //if we find a non blankspot AND a gap is found, then it's the end of a gap
    if (obstacles[i]>0 && gapFound){
      endFound = true;
      gapEnd = i;
      break;
    }
  }
  //if end is found, that means we have succesfully found a gap
  if (endFound){  
    Serial.print("gap beginning: ");
    Serial.print(scannerAngles[gapStart]);
    Serial.print(" end: ");
    Serial.print(scannerAngles[gapEnd]);
    Serial.println();
    //bluetooth
    Serial2.print("gap beginning: ");
    Serial2.print(scannerAngles[gapStart]);
    Serial2.print(" end: ");
    Serial2.print(scannerAngles[gapEnd]);
    Serial2.println();
    parkingDetectionComplete = true;
    /* creating the table with the coordinates of the parking spot */
    parkingSpot[0][0] = scannerAngles[gapStart];
    parkingSpot[0][1] = obstacles[gapStart];
    parkingSpot[1][0] = scannerAngles[gapEnd];
    parkingSpot[1][1] = obstacles[gapEnd];
    parkingSpotFound = true; //setting this to true so we know we've found a spot to park

  }
  else{ //if no gap is found try to check if there's a "wall" far away
    checkForWalls();
  }
}

/* if there's something that appears as a wall far away, there could still be a parking spot
 * but we can't detect it unless we go closer. Check to see if there's a wall */
void checkForWalls(){
  /* wall is three or more obstacles in a row
   * "far away is more than wall limit */
  //   if ((obstacles[i]> wallLimit) && (obstacles[i-1] > wallLimit) && (obstacles[i+1] > wallLimit)){
  //  }
  boolean wallStarted = false;
  boolean wallFinished = false;
  int wallLength = 0;
  int wallStart, wallEnd;
  for (int i=0; i<9;i++){
    //ignore any blankspots or walls less than the wall limit in the beginning
    if (obstacles[i]<wallLimit && !wallStarted) continue;
    //if you found your first obstacle, start the wall
    if (obstacles[i]> wallLimit && !wallStarted){
      wallStarted = true;
      wallStart = i;
      wallLength++;
      continue;
    }
    //if there's an obstacle far enough and wall has started, then increase the wall length and move the wallEnd forward
    if (obstacles[i]>wallLimit && wallStarted){
      wallLength++;
      wallEnd = i;
      continue;
    }
    //if we find something smaller than wallLimit (like a blankspot) AND the wall has started
    //OR we are at our final obstacle that means that the wall is complete
    if ((obstacles[i]<wallLimit) && (wallStarted || i==8)){
      wallEnd = true;
      break;
    }
  }
  //we consider a wall 3 or more obstacles in a row
  if (wallEnd && wallLength>=3){
    Serial.print("wall found from: ");
    Serial.print(scannerAngles[wallStart]);
    Serial.print(" untill: ");
    Serial.print(scannerAngles[wallEnd]);
    Serial.println();
    //bluetooth
    Serial2.print("wall found from: ");
    Serial2.print(scannerAngles[wallStart]);
    Serial2.print(" untill: ");
    Serial2.print(scannerAngles[wallEnd]);
    Serial2.println();
    /* save the wall dimensions */
    wall[0][0] = scannerAngles[wallStart];
    wall[0][1] = obstacles[wallStart];
    wall[1][0] = scannerAngles[wallEnd];
    wall[1][1] = obstacles[wallEnd];
    wallFound = true;    
  }
  else{
    Serial.println("Wall or gaps not found");
    Serial2.println("Wall or gaps not found");

    parkingSpotFound = false;
    wallFound = false;
  }
  parkingDetectionComplete = true;
}

/* scan the vacinity for objects */
void scanForObjects(){
  //scan once
  if (!scanComplete){
    moveToNextPosition();
    getDistance();
  }
}

/* servo to next position */
void moveToNextPosition(){
  unsigned long currentMillis = millis();
  //occurs every scanner interval
  if(currentMillis - scannerPrevMillis > scannerInterval) {
    //before moving on, measure the quality of the last 5 measurements.
    measureQuality();
    //if we have completed 3 cycles (3 sweeps) AND scanner just was at 150 degrees, recheck for consistency 
    if (cycleId == 2 && scannerPos == 8){
      cycleCheck();
    }
    //if scanner is at 150 degrees, it'll go back to scannerStep next turn, so initiate a new cycle
    if (scannerPos == 8){
      cycleId = (cycleId + 1) % 3;
    }
    scannerFirst = true;
    scannerPrevMillis = currentMillis;   
    scannerPos = (scannerPos + 1) % 9;
    scanner.write(scannerAngles[scannerPos]);
  }
}

/* determine if the sonar readings are reliable */
void measureQuality(){
  //if measurements have occured first (ignore measurements prior to first move of the servo, while arduino is still booting up
  if (scannerFirst){
    sort(measuredDistances,sizeof(measuredDistances)/sizeof(int));
    int reliableCount = 0;
    //    for (int i=0;i<5;i++)      Serial.println(measuredDistances[i]);
    int prevDif = -1;
    int sum = 0; //so to calculate the average
    int avgCount = 0; //count how many elements we summed so to calculate the average
    for (int i = 0; i< 4; i++){
      //  Serial.print(measuredDistances[i]);
      //  Serial.print(" ");
      if ((measuredDistances[i] != -1) && (measuredDistances[i+1] != -1)){
        //calculate the difference between the next element and the present one
        int dif = measuredDistances[i] - measuredDistances[i+1];
        //if we previously had two similar elements
        if (prevDif != -1){
          //calculate their differences
          int totalDif = abs(dif) + prevDif;
          //if the sum of their differences is small, it means we have a chain of similar elements
          if (totalDif <= 15){
            //increase the reliability count by 1
            reliableCount++;
            avgCount+=3;
            sum += measuredDistances[i] + measuredDistances[i-1] +measuredDistances[i+1] ;
          }
        }
        //if difference is less than 15 while none of them is -1 count it and log them as reliable
        if (abs(dif)<=15){
          reliableCount++;
          prevDif = abs(dif);
        }
        else{
          //if chain is broken put previous different as -1 so we know
          prevDif = -1;
        }
      }
      else{
        prevDif = -1;
      }
    }
    //    Serial.println("++++");

    int angleIndex = scannerPos;
    //if there are 3 or more similar elements in a row, that means it's a reliable measurement
    if (reliableCount >=3){
      int average = sum/avgCount;
      //place succesful measurements in table
      totalMeasurements[angleIndex][cycleId] = average;
    }
    else{
      //unreliable measurements get represented with a small negative number
      totalMeasurements[angleIndex][cycleId] = -100;
    }
  }
}

void getDistance_old(){
  unsigned long currentMillis = millis();
  //should happen every sonar interval
  if(currentMillis - sonarPrevMillis > sonarInterval) {
    sonarPrevMillis = currentMillis;   

    digitalWrite(trigPin, LOW); 
    delayMicroseconds(2); 

    digitalWrite(trigPin, HIGH);
    delayMicroseconds(10); 

    digitalWrite(trigPin, LOW);
    duration = pulseIn(echoPin, HIGH, 65000);

    //Calculate the distance (in cm) based on the speed of sound.
    distance = duration/58.2;
    if (scannerFirst){ //ignore while booting up
      if (distance >= maximumRange || distance <= minimumRange){
        /* save a negative number to indicate "out of range" */
        measuredDistances[distancesIndex] = -1;
      }
      else {
        /* save the distance to indicate successful reading. */
        measuredDistances[distancesIndex] = distance;
      }
      distancesIndex = (distancesIndex + 1) % 5;
    }
  }
}

void getDistance(){
  unsigned long currentMillis = millis();
  //should happen every sonar interval
  if(currentMillis - sonarPrevMillis > sonarInterval) {
    sonarPrevMillis = currentMillis;
    if (scannerFirst){ //ignore while booting up
      //measure with the Sharp GP2Y0A02 series infrared distance sensor
      //code found in luckylarry.co.uk/arduino-projects/arduino-using-a-sharp-ir-sensor-for-distance-calculation/
      float volts = analogRead(IRpin)*0.0048828125;   // value from sensor * (5/1024) - if running 3.3.volts then change 5 to 3.3
      float distance = 65*pow(volts, -1.10);          // worked out from graph 65 = theretical distance / (1/Volts)S - luckylarry.co.uk
      if (distance<20 || distance>150){
        measuredDistances[distancesIndex] = -1;
      }
      else{
        measuredDistances[distancesIndex] = distance;
      }
      distancesIndex = (distancesIndex + 1) % 5;
    }
  }
}

/* check to see if the 3 last measurements at a specific angle
 * are consistent to each other. If they are, then we can finally use them */
void cycleCheck(){
  for (int i = 0; i< 9; i++){
    int sum = 0;
    int countedTotal = 0;
    for (int j = 0; j<3; j++){
      if (totalMeasurements[i][j]>0){
        sum += totalMeasurements[i][j];
        countedTotal++;  
      }
      //     Serial.print(totalMeasurements[i][j]);
      //    Serial.print(" ");
      Serial2.print(totalMeasurements[i][j]);
      Serial2.print(" ");
    }
    Serial2.println();
    //  Serial.println();
    int avDistance;
    if (countedTotal !=0){
      avDistance = sum/countedTotal;
    }
    else{
      avDistance = -100;
    }
    //compare each measuremt against the average
    int dif1,dif2,dif3;
    int minusTotal = 0;
    if (totalMeasurements[i][0] > 0){
      dif1 = totalMeasurements[i][0] - avDistance;
    }
    else{
      dif1 = 0;
      minusTotal++;
    }
    if (totalMeasurements[i][1] > 0){
      dif2 = totalMeasurements[i][1] - avDistance;
    }
    else{
      dif2 = 0;
      minusTotal++;
    }
    if (totalMeasurements[i][2] > 0){
      dif3 = totalMeasurements[i][2] - avDistance;
    }
    else{
      dif3 = 0;
      minusTotal++;
    } 
    int totalSpread = abs(dif1) + abs(dif2) + abs (dif3);
    //if the total spread is small and average distance is not -1 then we have a succesful reading
    if (abs(totalSpread)<=15 && avDistance>0 && minusTotal<=1){
      int currentAngle = scannerAngles[i];   
      //     obstaclePositioning(currentAngle, avDistance);
      //place the distances of the objects in an array
      obstacles[i] = avDistance;

    }
    else{
      //if invalid measurement, put -1 in that position
      obstacles[i] = -1;
    }
  }
  Serial.println("----");
  //  Serial2.println("----");

  scanComplete = true;
  startScan = false;
}

/* calculating the position relative to the car
 * on the X and Y axis */
void obstaclePositioning(String type){
  float a,b,c,f, rotateAngle;
  int startAngle, endAngle;
  if (type.equals("parkingSpot")){

    /*    parkingSpot[0][0] = scannerAngles[gapStart];
     parkingSpot[0][1] = obstacles[gapStart];
     parkingSpot[1][0] = scannerAngles[gapEnd];
     parkingSpot[1][1] = obstacles[gapEnd]; */
    b = parkingSpot[0][1];
    a = parkingSpot[1][1];
    startAngle = parkingSpot[0][0];
    endAngle = parkingSpot[1][0];
  }
  else if (type.equals("wall")){
    /* wall[0][0] = scannerAngles[wallStart];
     wall[0][1] = obstacles[wallStart];
     wall[1][0] = scannerAngles[wallEnd];
     wall[1][1] = obstacles[wallEnd]; */
    b = wall[0][1];
    a = wall[1][1];
    f = wall[0][0] - wall[1][0];
    startAngle = wall[0][0];
    endAngle = wall[1][0];
  }
  /* implementing law of cosines to find the parking space or wall length */
  f = startAngle - endAngle;
  f = abs(f);
  c = sq(a) + sq(b) - 2*a*b*cos(radianAngle(f));
  c = sqrt(c); // this is the parking gap in centimeters or the length of the wall
  Serial.print("The length of ");
  Serial.print(type);
  Serial.print(" is : ");
  Serial.print(c);
  Serial.println(); 
  //if the start angle is 90, turn 90 counterclockwise
  if (startAngle == 90){
    rotateAngle= -90;
    offsetDistance =0;
    //if the end angle is 90, turn 90 clockwise      
  }
  else if (endAngle ==90){
    rotateAngle = 90;
    offsetDistance = 0;
    //if we are between them find to which we are closer and rotate accordingly, also calculate the offset distance
  }
  else if (startAngle<90 && endAngle>90){
    rotateAngle=0;
    offsetDistance=0;
    distanceToMiddle = 0;
    /*  if (a<b){ //means we are closer to the left side
     rotateAngle = 90;
     offsetDistance = sq(b) - sq(verticalDistance);
     offsetDistance = -sqrt(offsetDistance);       //we turn it into negative since we are "between" the obstacles      
     }
     else if (a>b){ //means we are closer to the right side
     rotateAngle = -90;
     offsetDistance = sq(a) - sq(verticalDistance);
     offsetDistance = -sqrt(offsetDistance); //we turn it into negative since we are "between" the obstacles      
     }
     else{ //means we are in the middle
     rotateAngle = 0;
     offsetDistance = 0;
     }*/
  }
  else{
    //determing to which angle we are closer
    int diffStart = 90-startAngle;
    diffStart = abs(diffStart);
    int diffEnd = 90-endAngle;
    diffEnd = abs(diffEnd);
    if (diffStart < diffEnd){
      rotateAngle = -(90 + diffStart);
    }
    else{
      rotateAngle = 90 + diffStart;
    }
  }

  Serial.print("Angle to rotate is: ");
  Serial.print(rotateAngle);
  Serial.println(); 
  degreesToParallelize = rotateAngle;

  /* calculating vertical distance through law of sines */
  float sinA = sin(radianAngle(f)) * b / c; //we need this in radians
  // sinA = radians2Degrees(sinA);
  verticalDistance = sinA * a; //use asin(sinA) if you want to see the angle in radians

  /*determine offset distance */
  if (startAngle>90 && endAngle>90){ //we are on the right
    offsetDistance = sq(b) - sq(verticalDistance);
    offsetDistance = sqrt(offsetDistance);
  }
  else if(startAngle<90 && endAngle<90){ //we are on the left
    offsetDistance = sq(a) - sq(verticalDistance);
    offsetDistance = sqrt(offsetDistance);
  }
  if (rotateAngle != 0)
    distanceToMiddle = c/2  + offsetDistance; // we should travel to the middle of the gap or wall, which is offsetDistance plus half of the length of the gap
  triangulated = true;
}
/* scale the results in order to be represented in a graphical map */
void scaleResults(int x, int y){
  float scale = 15.0;
  int obstacleX = x / scale;
  int obstacleY = y / scale;
  String ob = "obst";
  String tempX = String(obstacleX);
  String hash = "#";
  String tempY = String(obstacleY);
  String dimentionsOut = ob + tempX + hash + tempY;
  Serial.println(dimentionsOut); 
}

/* converts degrees to radians */
float radianAngle(int angle){
  return angle / 57.3;
}
/* converts radians to degrees */
float radians2Degrees(float rads){
  return rads * 57.3;
}

/* a simple bubble sort, ascending */
void sort(int unsorted[], int arraySize){
  for (int i = 0; i < arraySize - 1; i++){
    for (int j = 0; j < arraySize -1 - i; j++){
      if (unsorted[j]>unsorted[j+1]){
        int temp = unsorted[j];
        unsorted[j] = unsorted[j+1];
        unsorted[j+1] = temp;
      }
    }
  }
}
/* ------------------- */

/* gyroscope and speed encoder methods */


/* this function re-initializes the data used by the gyroscope and the accelerometer and the speed encoder */
void cleardata( ){

  Gz=0;

  Sx=0; 
  moving_distance =0 ;
  pulseCount = 0; 
  angle_error=0;
  distance_error=0;
  turning_angle=0;  
}

/* this function gets the latest data from gyroscope, speed encoder and accelerometer
 * data from accelerometer are not used currently, but are neccessary to be fetched in order
 * for everything else to work and integrity */
void update(){
  accelgyro.getMotion6(&ax,&ay,&az,&gx,&gy,&gz);//fetch

  //==========Angular velocity==========

  ggz=gz/131.00;

  //===============Integration================
  NowTime=millis();//get current time
  TimeSpan=NowTime-LastTime;//
  //get the turning angle, the only value we care is Gz
  Gz=Gz+(ggz-Gz_offset)*TimeSpan/1000;  
  Sx = map (pulseCount,0,distance_factor,0,100 ); 
  NowTime=millis();//get current time
  LastTime=NowTime;

  //==============================

  turning_angle = map(Gz, -1 * angle_factor,angle_factor, -180 , 180); // very importance for maping the angle every time!!
  moving_distance = Sx;  
}

/* counts the pulses from the speed encoder. It's an interrupt function*/
void count(){
  pulseCount ++;
}

/* function to make the vehicle turn at the specified degrees.
 * Negative target value means counter-clockwise */
void turn(int goal_angle){

  // critical, time-sensitive code here, depeneds on the rest of the code

    cleardata( );
  update();
  //    Serial2.println(turning_angle);
  angle_error = goal_angle - turning_angle;

  //TO-DO add a timeout
  unsigned long startPoint = millis();
  unsigned long currentMillis = millis();
  while ( (abs(angle_error) > angle_tolerance) && (currentMillis-startPoint<turnTimeout)){
    /* feedback control */
    if (angle_error < 0){// when we drift more right, it's an error, so we should start turning a little to the left
      motorLeft1.run(BACKWARD);
      motorLeft2.run(BACKWARD);
      motorRight1.run(FORWARD);
      motorRight2.run(FORWARD);
      motorLeft1.setSpeed(turningSpeed);
      motorLeft2.setSpeed(turningSpeed);
      motorRight1.setSpeed(turningSpeed);
      motorRight2.setSpeed(turningSpeed);
    }
    else {// when we drift more left than we should, it's an error, so we should start turning a little to the right
      motorLeft1.run(FORWARD);
      motorLeft2.run(FORWARD);
      motorRight1.run(BACKWARD);
      motorRight2.run(BACKWARD);
      motorLeft1.setSpeed(turningSpeed);
      motorLeft2.setSpeed(turningSpeed);
      motorRight1.setSpeed(turningSpeed);
      motorRight2.setSpeed(turningSpeed);
    }
    update();
    angle_error = goal_angle - turning_angle;
    currentMillis = millis();
  }
  // stop engine
  motorLeft1.setSpeed(0);
  motorLeft2.setSpeed(0);
  motorRight1.setSpeed(0);
  motorRight2.setSpeed(0);
  angle += turning_angle;
  //updating car orientation for exploring mode
  carOrientation+= turning_angle;
  if(carOrientation < -180){
    carOrientation = 180-(carOrientation % 180);
  }
  if(carOrientation > 180){
    carOrientation = -180+(carOrientation % 180);
  }
  cleardata();
}

/* function to make the vehicle travel a specific distance in centimeters
 * negative values mean backwards */
void go(int distance){
  //we only attach the interrupt when it is necessary
  attachInterrupt(4, count, RISING);// pin 19 for counter at arduino mega
  if (distance > 0){
    motorLeft1.run(FORWARD);
    motorLeft2.run(FORWARD);
    motorRight1.run(FORWARD);
    motorRight2.run(FORWARD);
  }
  else {
    motorLeft1.run(BACKWARD);
    motorLeft2.run(BACKWARD);
    motorRight1.run(BACKWARD);
    motorRight2.run(BACKWARD);   
  }

  cleardata( );
  update();
  motorLeft1.setSpeed(movingSpeed);
  motorLeft2.setSpeed(movingSpeed);
  motorRight1.setSpeed(movingSpeed);
  motorRight2.setSpeed(movingSpeed);
  distance_error = abs(distance) - moving_distance;
  //TO-DO add a timeout
  long startPoint = millis();
  long currentMillis = millis();

  while (distance_error> 0 && (currentMillis-startPoint<goTimeout)){
    update();
    distance_error =abs(distance)- moving_distance;
    currentMillis = millis();
  }
  // stop engine
  motorLeft1.setSpeed(0);
  motorLeft2.setSpeed(0);
  motorRight1.setSpeed(0);
  motorRight2.setSpeed(0);
  //updating x,y coordinates needed for exploring mode
  xcoordinate += sin( radianAngle(carOrientation) )* distance ;
  ycoordinate += cos( radianAngle(carOrientation) )* distance ;
  detachInterrupt(4); //detach the interrupt after you have reached target distance
}

/* function to handle the status led light on the vehicle */
void statusLight(int colorValue) {
  int array [3];
  for(int i=0; i<3; i++) {
    array[i] = colorValue%2;
    colorValue /= 2;

  }
  digitalWrite(R_led,255* array[0] );
  digitalWrite(G_led,255* array[1]);
  digitalWrite(B_led,255* array[2]);

}

/* infra red track following */

/* function to measure distance in a simple way, used for infrared track following and autoexploring */
void getDistanceSimple(){
  digitalWrite(trigPin, LOW); 
  delayMicroseconds(2); 

  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10); 

  digitalWrite(trigPin, LOW);
  duration = pulseIn(echoPin, HIGH);

  //Calculate the distance (in cm) based on the speed of sound.
  ultraSoundDistance = duration/58.2;

}

/* method to decide whether to stop the motors or not
 * depending on the measured distance by the ultra sound sensor
 * used in infrared track following */
boolean isCloseStop(){
  if ( ultraSoundDistance < stopDistance ){
    motorLeft1.setSpeed(0);
    motorLeft2.setSpeed(0);
    motorRight1.setSpeed(0);
    motorRight2.setSpeed(0);
    followTrack = false;
    automationMode = false;
    return true; 
  }
  else return false;
}

/* method to fetch the data from the infrared sensors */
void irRead(){
  leftInfraredData[0] = digitalRead( infraredLeft0);
  leftInfraredData[1] = digitalRead( infraredLeft1);
  rightInfraredData[0] = digitalRead( infraredRight0);
  rightInfraredData[1] = digitalRead( infraredRight1);
}

/* method to decide whether to adjust the movement depending on infrared data */
void adjustMovement(){
  //    Serial2.print (leftRead[0] );
  //    Serial2.print ("," );
  //    Serial2.print (leftRead[1] );
  //    Serial2.print ("," );
  //     Serial2.print (rightRead[1] );
  //    Serial2.print ("," );
  //    Serial2.print (rightRead[0] );
  //    Serial2.println (" " );

  for ( int i =0 ; i < 2; i ++  ){
    if ( leftInfraredData[i]==0 ){// detect white on the left, should turn right
      autoRight();    
    }
    if ( rightInfraredData[i]==0 ){// detect white on the right , should turn left
      //rightSpeed += 50*i;
      //leftSpeed -=50*i; 
      autoLeft();
    }
  }
  /*
  motorLeft1.setSpeed(leftSpeed);
   motorLeft2.setSpeed(leftSpeed);
   motorRight1.setSpeed(rightSpeed);
   motorRight2.setSpeed(rightSpeed);
   */
}


void autoRight(){
  motorLeft1.setSpeed(0);
  motorLeft2.setSpeed(0);
  motorRight1.setSpeed(0);
  motorRight2.setSpeed(0); 
  motorLeft1.run(FORWARD);
  motorLeft2.run(FORWARD);
  motorLeft1.setSpeed(2*infraredAutoSpeed);
  motorLeft2.setSpeed(2*infraredAutoSpeed);
  motorRight1.setSpeed(2*infraredAutoSpeed);
  motorRight2.setSpeed(2*infraredAutoSpeed); 
  delay(turningDelay);
  motorLeft1.setSpeed(0);
  motorLeft2.setSpeed(0);
  motorRight1.setSpeed(0);
  motorRight2.setSpeed(0);
  motorLeft1.run(BACKWARD);
  motorLeft2.run(BACKWARD);
  motorLeft1.setSpeed(infraredAutoSpeed);
  motorLeft2.setSpeed(infraredAutoSpeed);
  motorRight1.setSpeed(infraredAutoSpeed);
  motorRight2.setSpeed(infraredAutoSpeed); 
}

void autoLeft(){
  motorLeft1.setSpeed(0);
  motorLeft2.setSpeed(0);
  motorRight1.setSpeed(0);
  motorRight2.setSpeed(0);
  motorRight1.run(FORWARD);
  motorRight2.run(FORWARD);
  motorLeft1.setSpeed(2*infraredAutoSpeed);
  motorLeft2.setSpeed(2*infraredAutoSpeed);
  motorRight1.setSpeed(2*infraredAutoSpeed);
  motorRight2.setSpeed(2*infraredAutoSpeed);
  delay(turningDelay);
  motorLeft1.setSpeed(0);
  motorLeft2.setSpeed(0);
  motorRight1.setSpeed(0);
  motorRight2.setSpeed(0);
  motorRight1.run(BACKWARD);
  motorRight2.run(BACKWARD);
  motorLeft1.setSpeed(infraredAutoSpeed);
  motorLeft2.setSpeed(infraredAutoSpeed);
  motorRight1.setSpeed(infraredAutoSpeed);
  motorRight2.setSpeed(infraredAutoSpeed); 
}

/*-----------explore-----------*/
/* when this method is called, the vehicle first goes towards the nearest
 wall or "border" (unless already there) and then follows the right hand rule
 in order to get information for the so far unknown area. 
 For example: first goes to the wall, then turns to the right if it's clear
 then scans once and sends the data through the serial port and continues doing
 that sequence untill it is constrained between two walls. then it turns 180 degrees */
void explore(){
  //explore algrithm: right hand rule
  //scan once
  moveToWall( );
  if ( isRightClear){
    turn (90);
    moveToWall();
  }
  else{  //right is not clear
    if(isFrontClear){ //right is NOT clear && front is clear
      go(-10);
      scan ();
    }
    else{ //right is NOT clear && front is NOT clear
      if (isLeftClear){

        turn(-90);
        scan ();
      }
      else {
        turn (180);
      }
    }
  }

  // go(-10);
}

/* very simple method to just get a measurement from the
 IR distance sensor */
int IRdistance(){  
  float volts = analogRead(IRpin)*0.0048828125;   // value from sensor * (5/1024) - if running 3.3.volts then change 5 to 3.3
  float distance = 65*pow(volts, -1.10);          // worked out from graph 65 = theretical distance / (1/Volts)S - luckylarry.co.uk
  if (distance >= maximumRange || distance < minimumRange ){
    /* save a negative number to indicate "out of range" */
    distance = maximumRange + 10;//+ClearDistance
  }
  return (int)distance;
}

/* this method scans once, from 0-180 degrees using the servo
 and the ir-sensor on it. it is used in order to determine 
 the border positions which are being sent over to serial port
 and whether the vehicle is free to move on each direction*/
void scan(){

  for ( scannerPos =0 ; scannerPos<13; scannerPos++ ){   
    int i = 0;
    exobstacles[ scannerPos] = IRdistance(); //getDistance();
    Serial2.print("#");
    Serial2.print(exobstacles[ scannerPos]);
    Serial2.print(",");
    Serial2.print(exscannerAngles[scannerPos]);
    Serial2.print(",");
    Serial2.print(xcoordinate);
    Serial2.print(",");
    Serial2.print(ycoordinate);
    Serial2.print(",");
    Serial2.print(carOrientation);
    Serial2.println( );

    Serial.print("#");
    Serial.print(exobstacles[ scannerPos]);
    Serial.print(",");
    Serial.print(exscannerAngles[scannerPos]);
    Serial.print(",");
    Serial.print(xcoordinate);
    Serial.print(",");
    Serial.print(ycoordinate);
    Serial.print(",");
    Serial.print(carOrientation);
    Serial.println( );

    scanner.write(exscannerAngles[scannerPos]);
    delay(500);
  }

  //update clear information
  int rightDistance = exobstacles[0] ;
  int frontDistance = exobstacles[6] ;
  int leftDistance = exobstacles[12] ; 
  if (rightDistance < ClearDistance ){ //digitalRead( RightIR) = 0 when detect   
    isRightClear = false;
  }
  else {
    isRightClear = true;
  }
  if (frontDistance < ClearDistance){
    isFrontClear = false;
  }
  else {
    isFrontClear = true;
  }
  if (leftDistance< ClearDistance ){
    isLeftClear = false;
  }
  else {
    isLeftClear = true;
  }
}

/* this function is used to make the vehicle
 move towards the nearest wall/border */
void moveToWall( ){
  scan ();
  while(isFrontClear){
    go(-10);
    scan ();
  }  
}

