#include <Adafruit_NeoPixel.h>
#include <SoftwareSerial.h>
#define MATRIX_PIN 8
#define MOUTH_SIZE 6
#define EYE_SIZE 6

typedef struct Pixel {
  byte color;
  uint16_t pos;
} Pixel;

typedef struct Element {
  Pixel pixel;
  Element* next;
  void add(byte color, uint16_t pos) {
    Element* tmp = this;
    while (tmp->next) tmp = tmp->next;
    tmp->next = (Element*)malloc(sizeof(Element));
    tmp->next->pixel = { color, pos };
    tmp->next->next = NULL;
  }
} Element;

typedef struct Face {
  Element* mouthes[MOUTH_SIZE];
  Element* eyes[EYE_SIZE];
  uint8_t currentMouth;
  uint8_t currentEye;
} Face;

SoftwareSerial BTSerial(10, 9);
Adafruit_NeoPixel matrix(256, MATRIX_PIN, NEO_GRB + NEO_KHZ800);
Face face;
uint32_t blue = matrix.Color(0, 0, 205);

void dispalyFace();
void initMouthes();
void initEyes();

void setup() {
  // put your setup code here, to run once:
  BTSerial.begin(38400);
  Serial.begin(9600);
  matrix.begin();
  matrix.setBrightness(10);
  initMouthes();
  initEyes();
  displayFace();
}

void loop() {
  // put your main code here, to run repeatedly:
  if (BTSerial.available()) {
    byte state = BTSerial.read();
    Serial.write(state);
    if (state == 'e') face.currentEye = ++face.currentEye % EYE_SIZE;
    else if (state == 'f') face.currentMouth = ++face.currentMouth % MOUTH_SIZE;
    displayFace();
  }
  delay(20);
}

void displayFace() {
  matrix.clear();
  Element* mouth = face.mouthes[face.currentMouth]->next;
  Element* eyes = face.eyes[face.currentEye]->next;
  while (mouth != NULL) {
    matrix.setPixelColor(mouth->pixel.pos, mouth->pixel.color);
    mouth = mouth->next;
  }
  while (eyes != NULL) {
    matrix.setPixelColor(eyes->pixel.pos, eyes->pixel.color);
    eyes = eyes->next;
  }
  matrix.show();
}

void initMouthes() {
  face.currentMouth = 0;
  Element* m1 = (Element*)malloc(sizeof(Element));
  m1->next = NULL;
  m1->add(blue, 70);
  m1->add(blue, 71);
  m1->add(blue, 72);
  m1->add(blue, 73);
  m1->add(blue, 85);
  m1->add(blue, 90);
  face.mouthes[0] = m1;
  Element* m2 = (Element*)malloc(sizeof(Element));
  m2->next = NULL;
  m2->add(blue, 69);
  m2->add(blue, 74);
  m2->add(blue, 86);
  m2->add(blue, 87);
  m2->add(blue, 88);
  m2->add(blue, 89);
  face.mouthes[1] = m2;
  Element* m3 = (Element*)malloc(sizeof(Element));
  m3->next = NULL;
  m3->add('b', 69);
  m3->add('b', 70);
  m3->add('b', 71);
  m3->add('b', 72);
  m3->add('b', 73);
  m3->add('b', 74);
  face.mouthes[2] = m3;
  Element* m4 = (Element*)malloc(sizeof(Element));
  m4->next = NULL;
  m4->add('b', 56);
  m4->add('b', 57);
  m4->add('b', 58);
  m4->add('b', 69);
  m4->add('b', 71);
  m4->add('b', 85);
  m4->add('b', 86);
  m4->add('b', 87);
  m4->add('b', 88);
  m4->add('b', 89);
  m4->add('b', 90);
  face.mouthes[3] = m4;
  Element* m5 = (Element*)malloc(sizeof(Element));
  m5->next = NULL;
  m5->add('b', 53);
  m5->add('b', 69);
  m5->add('b', 70);
  m5->add('b', 71);
  m5->add('b', 72);
  m5->add('b', 73);
  m5->add('b', 74);
  m5->add('b', 85);
  face.mouthes[4] = m5;
  Element* m6 = (Element*)malloc(sizeof(Element));
  m6->next = NULL;
  m6->add('b', 39);
  m6->add('b', 40);
  m6->add('b', 54);
  m6->add('b', 57);
  m6->add('b', 70);
  m6->add('b', 73);
  m6->add('b', 86);
  m6->add('b', 87);
  m6->add('b', 88);
  m6->add('b', 89);
  face.mouthes[5] = m6;
}

void initEyes() {
  face.currentEye = 0;
  Element* e1 = (Element*)malloc(sizeof(Element));
  e1->next = NULL;
  e1->add(blue, 148);
  e1->add(blue, 155);
  e1->add(blue, 164);
  e1->add(blue, 171);
  e1->add(blue, 180);
  e1->add(blue, 187);
  face.eyes[0] = e1;
  Element* e2 = (Element*)malloc(sizeof(Element));
  e2->next = NULL;
  e2->add(blue, 148);
  e2->add(blue, 155);
  e2->add(blue, 164);
  e2->add(blue, 171);
  e2->add(blue, 196);
  e2->add(blue, 203);
  e2->add(blue, 211);
  e2->add(blue, 220);
  face.eyes[1] = e2;
  Element* e3 = (Element*)malloc(sizeof(Element));
  e3->next = NULL;
  e3->add('b', 148);
  e3->add('b', 155);
  e3->add('b', 164);
  e3->add('b', 171);
  e3->add('b', 195);
  e3->add('b', 204);
  e3->add('b', 212);
  e3->add('b', 219);
  face.eyes[2] = e3;
  Element* e4 = (Element*)malloc(sizeof(Element));
  e4->next = NULL;
  e4->add('b', 147);
  e4->add('b', 148);
  e4->add('b', 149);
  e4->add('b', 154);
  e4->add('b', 155);
  e4->add('b', 156);
  face.eyes[3] = e4;
  Element* e5 = (Element*)malloc(sizeof(Element));
  e5->next = NULL;
  e5->add('b', 147);
  e5->add('b', 149);
  e5->add('b', 154);
  e5->add('b', 156);
  e5->add('b', 164);
  e5->add('b', 171);
  face.eyes[4] = e5;
  Element* e6 = (Element*)malloc(sizeof(Element));
  e6->next = NULL;
  e6->add('b', 148);
  e6->add('b', 155);
  e6->add('b', 165);
  e6->add('b', 170);
  e6->add('b', 180);
  e6->add('b', 187);
  face.eyes[5] = e6;
}