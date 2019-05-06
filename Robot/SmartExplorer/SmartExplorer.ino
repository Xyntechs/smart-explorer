//This is a WiFi Controlled Mapping robot
//we used arduino C Functions like Digital Read / Write and PinMode
//we used Servo Library to move ultrasonic by 180 degree to act as smart obstacle avoiding robot
//we used pulsin function to to get the time the frequecy wave travelled to get the distance from ultrasonic
//we used tone function to activate the buzzer in alert and as a horn with diffrent freqency
//we used serial library to get data through bluetooth serial
//we used 8 bit timer to get the time without blocking the code by delay function 
//we used pulse width modulation to control speed only when moving with angle 
//This was created by:
//Wael Ashraf
//Ahmed Ismaeel
//Abdelrahman Seif

//---------The Code----------// 

//1. Include Firebase ESP8266 library (this library)

#include "FirebaseESP8266.h"

//2. Include ESP8266WiFi.h and servo.h and must be included after FirebaseESP8266.h

#include <ESP8266WiFi.h>
#include <Servo.h>









// Set these to run example.

#define FIREBASE_HOST "smart-explorer-nodemcu.firebaseio.com"

#define FIREBASE_AUTH "1RsEob6doPqb6KwrYhsKMDYOZGGEJwcgh2OBax9S"

#define WIFI_SSID "Engineers"

#define WIFI_PASSWORD "1990199788wrr"


FirebaseData firebaseData;
//Servo object 
Servo myServo;
//DC Motor A Right
int MotorEnableA = HIGH;
int MotorBackwardA=D1;
int MotorForwardA=D2;

//DC Motor B Left
int MotorBackwardB=D3;
int MotorForwardB=D4;
int MotorEnableB=HIGH;

//Lasers
int LaserA=D5;
int LaserB=D5;

//Buzzer
int Buzzer=D5;

//Ultrasonic
int Trig =D6;
int Echo=D7;
long duration;
int distance;
int ThresDist;

//for timer 
volatile static int OverflowCount;



int robotup;
int robotdown;
int robotright;
int robotleft;
int sensorup;
int sensordown;
int sensorright;
int sensorleft;
int horn;

void setup() 
{
pinMode(MotorEnableA, OUTPUT);//make Motor pins act as output pins
pinMode(MotorEnableB, OUTPUT);//make Motor pins act as output pins
pinMode(MotorBackwardA, OUTPUT);//make Motor pins act as output pins
pinMode(MotorForwardA, OUTPUT);//make Motor pins act as output pins
pinMode(MotorBackwardB, OUTPUT);//make Motor pins act as output pins
pinMode(MotorForwardB, OUTPUT);//make Motor pins act as output pins
pinMode(LaserA, OUTPUT);//make Laser pin act as output pins
pinMode(LaserB, OUTPUT);//make Laser pin act as output pins
pinMode(Buzzer, OUTPUT);//make Buzzer pin act as output pins
pinMode(Trig, OUTPUT);//the output freqency from ultrasonic
pinMode(Echo, INPUT);//the input frequecny when it hit object
myServo.attach(D0);// attach servo to pin no 4
myServo.write(180);// intialize servo position to forward    
Serial.begin(9600);  //Set the baud rate to your wifi module.
WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print("connecting");
    while (WiFi.status() != WL_CONNECTED) 
      {
      Serial.print(".");
      delay(500);
      }
    Serial.println();
    Serial.print("connected: ");
    Serial.println(WiFi.localIP());
    Firebase.begin(FIREBASE_HOST,FIREBASE_AUTH);
}

void loop() 
{

  if(Firebase.getInt(firebaseData,"/item/robotup",robotup))
  {
    if(robotup)
    {
      forward();
    }
  }
  
  if(Firebase.getInt(firebaseData,"/item/robotdown",robotdown))
  {
    if(robotdown)
    {
      back();
    }
  }
  
  if(Firebase.getInt(firebaseData,"/item/robotright",robotright))
  {
       if(robotright)
    {
      right();
    }
  }
  
  if(Firebase.getInt(firebaseData,"/item/robotleft",robotleft))
  {
       if(robotleft)
    {
      left();
    }
  }
  
  if(Firebase.getInt(firebaseData,"/item/sensorup",sensorup))
  {
       if(sensorup)
    {
      Stop();
       myServo.write(180);//make ultrasonic points forward
    }
  }
  
  if(Firebase.getInt(firebaseData,"/item/sensordown",sensordown))
  {
       if(sensordown)
    {
      Stop();
       myServo.write(0);//make ultrasonic points forward
      
    }
  }
  
  if(Firebase.getInt(firebaseData,"/item/sensorright",sensorright))
  {
       if(sensorright)
    {
      Stop();
       myServo.write(270);//make ultrasonic points forward
     
    }
  }
  
  if(Firebase.getInt(firebaseData,"/item/sensorleft",sensorleft))
  {
       if(sensorleft)
    {
      Stop();
       myServo.write(90);//make ultrasonic points forward
     
    }
  }
    if(Firebase.getInt(firebaseData,"/item/horn",horn))
  {
       if(horn)
    {
      Stop();
       HornON();
      LasersON();
     
    }
       HornOFF();
      LasersOFF();
  }

}
void forward()
{
 
  //Move Forward
  
  myServo.write(180);//make ultrasonic points forward
  
  ThresDist=UltraSonic();//call ultrasonic function and get the distance read
  
if(ThresDist<15)//check on the distance thresohold 
{
  Alert(); // give alert if it about to hit
  Stop();// and stop motors
}
else
{ 
  do
  {
  //Motor A
  
  digitalWrite(MotorForwardA, LOW);
  digitalWrite(MotorForwardB, HIGH);
  digitalWrite(MotorBackwardA, HIGH);
  digitalWrite(MotorBackwardB, LOW);
  //Motor B


  ThresDist=UltraSonic();//call ultrasonic function and get the distance read contiously to avoid hitting something during movement

  }while(ThresDist>15);//check on the count or the distance or something came from user to break from the loop
}    
   Stop();//stop after getting out of the loop
   if(ThresDist<15)// if we got out because of distance then fire the alarm
   {
   Alert();
   }    
}
void back()
{
  //Move Backward
  myServo.write(0);//make ultrasonic points Backward

  ThresDist=UltraSonic();//call ultrasonic function and get the distance read
  
  if(ThresDist<20)
{
  Alert(); // give alert if it about to hit
  Stop();// and stop motors
}
else
{ 
  do
  {
    //Motor A
  
    digitalWrite(MotorBackwardA, HIGH);
    digitalWrite(MotorForwardA, LOW);
    //Motor B
   
    digitalWrite(MotorBackwardB, HIGH);
    digitalWrite(MotorForwardB, LOW);
    
    ThresDist=UltraSonic();//call ultrasonic function and get the distance read contiously to avoid hitting something during movement

  } while(ThresDist>20);//check on the count or the distance or something came from user to break from the loop
}
  
  Stop();//stop after getting out of the loop
    if(ThresDist<20)// if we got out because of distance then fire the alarm
   {
   Alert();
   }
}
  void left()
{
      
//Move to the Left
  myServo.write(180);//make ultrasonic points Forward
 ThresDist=UltraSonic();//call ultrasonic function and get the distance read

 if(ThresDist<15)
{
  Alert(); // give alert if it about to hit
  Stop();// and stop motors
}
else
{ 
 do 
  {
  //Motor A
  digitalWrite(MotorEnableA, HIGH);
   digitalWrite(MotorForwardA, HIGH);
  digitalWrite(MotorBackwardA, LOW);
 
  //Motor B
  digitalWrite(MotorEnableB, HIGH);
  digitalWrite(MotorBackwardB, LOW);
  digitalWrite(MotorForwardB, LOW);
  

    ThresDist=UltraSonic();//call ultrasonic function and get the distance read contiously to avoid hitting something during movement
  }while(ThresDist>15);//check on the count or the distance or something came from user to break from the loop
}
  
  Stop();//stop after getting out of the loop
     if(ThresDist<15)// if we got out because of distance then fire the alarm
   {
   Alert();
   }

}
void right()
{
 // Move to the Right 
   myServo.write(180);//make ultrasonic points Forward
 ThresDist=UltraSonic();//call ultrasonic function and get the distance read
  
   if(ThresDist<15)
{
  Alert(); // give alert if it about to hit
  Stop();// and stop motors
}
else
{ 
do  
  {
     //Motor A
  digitalWrite(MotorEnableA, HIGH);
  digitalWrite(MotorForwardA, LOW);
  digitalWrite(MotorBackwardA, LOW);
  //Motor B
  digitalWrite(MotorEnableB, HIGH);
   digitalWrite(MotorForwardB, HIGH);
  digitalWrite(MotorBackwardB, LOW);
 

    ThresDist=UltraSonic();//call ultrasonic function and get the distance read contiously to avoid hitting something during movement
  }while(ThresDist>15);//check on the count or the distance or something came from user to break from the loop
}

  Stop();//stop after getting out of the loop
     if(ThresDist<15)// if we got out because of distance then fire the alarm
   {
   Alert();
   }  
}
void Stop()
{
  //Stop Motors
    //Motor A
  digitalWrite(MotorEnableA, HIGH);
  digitalWrite(MotorForwardA, LOW);
  digitalWrite(MotorBackwardA, LOW);
  //Motor B
  digitalWrite(MotorEnableB, HIGH);
  digitalWrite(MotorForwardB, LOW);
  digitalWrite(MotorBackwardB, LOW);
   
}
void LasersON()
{

  digitalWrite(LaserA, HIGH);
  digitalWrite(LaserB, HIGH);
  
}
void LasersOFF()
{
      digitalWrite(LaserA, LOW);
      digitalWrite(LaserB, LOW);
}
void HornON()
{
  tone(Buzzer,1000);//Send 1KHZ Frequency
}
void HornOFF()
{
  noTone(Buzzer);     //Close Buzzer 
}
void Alert()
{
   tone(Buzzer,3000,500);//Send 3KHZ Frequency with duration to stop after half sec.
}
int UltraSonic()
{

digitalWrite(Trig, LOW);  // Clears the trigPin
delayMicroseconds(2);// Sets the trigPin on HIGH state for 10 micro seconds

digitalWrite(Trig, HIGH);
delayMicroseconds(10);
digitalWrite(Trig, LOW);

duration = pulseIn(Echo, HIGH);// Reads the echoPin, returns the sound wave travel time in microseconds

distance= duration*0.034/2;// Calculating the distance


return distance;
}
