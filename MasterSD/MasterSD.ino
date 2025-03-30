#include <Adafruit_NeoPixel.h>
#include <SD.h>
#include <SoftwareSerial.h>
#define MATRIX_PIN 8
#define SD_PIN 7
#define MOUTH_SIZE 10
#define EYE_SIZE 10

//MEGA: 50 (MISO), 51 (MOSI), 52 (SCK)
typedef struct Pixel {
    uint16_t pos;
    uint8_t r;
    uint8_t g;
    uint8_t b;
} Pixel;

typedef struct Face {
  uint8_t currentMouth;
  uint8_t currentEye;
} Face;

//SoftwareSerial BTSerial(3, 4);
Adafruit_NeoPixel matrix(256, MATRIX_PIN, NEO_GRB + NEO_KHZ800);
Face face;
File file;
File copyFile;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  //BTSerial.begin(38400);
  SD.begin(SD_PIN);
  matrix.begin();
  matrix.setBrightness(10);
  matrix.clear();
  matrix.show();
}

void loop() {
  // put your main code here, to run repeatedly:
  /*
  if (BTSerial.available()) {
    byte state = BTSerial.read();
    Serial.write(state);
    if (state == 'e') face.currentEye = ++face.currentEye % EYE_SIZE;
    else if (state == 'f') face.currentMouth = ++face.currentMouth % MOUTH_SIZE;
    displayFace();
  }
  delay(20);
  */
  //displayFace();
  face.currentEye = ++face.currentEye % EYE_SIZE;
  face.currentMouth = ++face.currentMouth % MOUTH_SIZE;
  delay(3000);
}

void displayFace() {
  Serial.println(F("Xoxoxo"));
  matrix.clear();
  Pixel pixel;
  int counter = 0;
  char filename[50];
  memset(filename, 0, 50 * sizeof(char));
  sprintf(filename, "imgs/main/mouthes/%d.bin", face.currentMouth + 1);
  file = SD.open(filename, FILE_READ);
  while(file.available()) {
    file.read((uint8_t *)&pixel, sizeof(Pixel) / sizeof(uint8_t));
    Serial.print(pixel.pos); Serial.print(' ');
    Serial.print(pixel.r); Serial.print(' ');
    Serial.print(pixel.g); Serial.print(' ');
    Serial.print(pixel.b); Serial.println();
    matrix.setPixelColor(pixel.pos, pixel.r, pixel.g, pixel.b);
    counter++;
  }
  file.close();
  Serial.println();
  memset(filename, 0, 50);
  sprintf(filename, "imgs/main/eyes/%d.bin", face.currentEye + 1);
  file = SD.open(filename, FILE_READ);
  while(file.available()) {
    file.read((uint8_t *)&pixel, sizeof(Pixel) / sizeof(uint8_t));
    Serial.print(pixel.pos); Serial.print(' ');
    Serial.print(pixel.r); Serial.print(' ');
    Serial.print(pixel.g); Serial.print(' ');
    Serial.print(pixel.b); Serial.println();
    matrix.setPixelColor(pixel.pos, pixel.r, pixel.g, pixel.b);
    counter++;
  }
  file.close();
  matrix.show();
}