#include <SoftwareSerial.h>
#define BUTTON1 2
#define BUTTON2 3
const unsigned char cat1 = 'f';
const unsigned char cat2 = 'e';
SoftwareSerial BTSerial(10, 9);

volatile byte button1Released = false;
void button1ReleasedInterrupt() {
  button1Released = true;
}

volatile byte button2Released = false;
void button2ReleasedInterrupt() {
  button2Released = true;
}

void setup() {
  // put your setup code here, to run once:
  BTSerial.begin(38400);
  Serial.begin(9600);
  pinMode(BUTTON1, INPUT);
  pinMode(BUTTON2, INPUT);
  attachInterrupt(digitalPinToInterrupt(BUTTON1), button1ReleasedInterrupt, FALLING);
  attachInterrupt(digitalPinToInterrupt(BUTTON2), button2ReleasedInterrupt, FALLING);
}

void loop() {
  // put your main code here, to run repeatedly:
  if (button1Released) {
    Serial.write('1');
    button1Released = false;
    BTSerial.write(cat1);
  }
  if (button2Released) {
    Serial.write('2');
    button2Released = false;
    BTSerial.write(cat2);
  }
  delay(20);
}