#include <SoftwareSerial.h>
#define BUTTON1 2
#define BUTTON2 3
const unsigned char cat1 = 'f';
const unsigned char cat2 = 'e';
int button1State;
int button2State;
int lastButton1State = LOW;
int lastButton2State = LOW;
unsigned long lastDebounceTime1 = 0;
unsigned long lastDebounceTime2 = 0;
unsigned long debounceDelay = 50;
SoftwareSerial BTSerial(10, 9);

void setup() {
  // put your setup code here, to run once:
  BTSerial.begin(38400);
  Serial.begin(9600);
  pinMode(BUTTON1, INPUT);
  pinMode(BUTTON2, INPUT);
}

void loop() {
  // put your main code here, to run repeatedly:
  int reading1 = digitalRead(BUTTON1);
  if (reading1 != lastButton1State) lastDebounceTime1 = millis();
  if ((millis() - lastDebounceTime1) > debounceDelay) {
    if (reading1 != button1State) {
      button1State = reading1;
      if (button1State == HIGH) {
        Serial.write('1');
        BTSerial.write(cat1);
      }
    }
  }
  lastButton1State = reading1;
  int reading2 = digitalRead(BUTTON2);
  if (reading2 != lastButton2State) lastDebounceTime2 = millis();
  if ((millis() - lastDebounceTime2) > debounceDelay) {
    if (reading2 != button2State) {
      button2State = reading2;
      if (button2State == HIGH) {
        Serial.write('2');
        BTSerial.write(cat2);
      }
    }
  }
  lastButton2State = reading2;
}