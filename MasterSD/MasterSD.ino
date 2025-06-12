#include <Adafruit_NeoPixel.h>
#include <SD.h>
#define MATRIX_PIN 8
#define SD_PIN 9
#define MOUTH_SIZE 10
#define EYE_SIZE 10
#define MOUTH_CATEGORY 0
#define EYE_CATEGORY 1
#define MESSAGE_HEADER 5
#define BTRemote Serial1
#define BTSmartphone Serial2

//MEGA: 50 (MISO), 51 (MOSI), 52 (SCK)
//MEGA: Serial1 - 19 (RX) and 18 (TX);
//MEGA: Serial2 - 17 (RX) and 16 (TX);
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

Adafruit_NeoPixel matrix(256, MATRIX_PIN, NEO_GRB + NEO_KHZ800);
Face face;
File file;
File copyFile;

void sendCategory(uint8_t category) {
  uint8_t n = 0;
  char categoryString[10];
  memset(categoryString, '\0', sizeof(char) * 10);
  if (category == MOUTH_CATEGORY) {
    n = MOUTH_SIZE;
    sprintf(categoryString, "mouthes");
  }
  else if (category == EYE_CATEGORY) {
    n = EYE_SIZE;
    sprintf(categoryString, "eyes");
  }
  else return;

  for (uint8_t i = 1; i <= n; i++) {
    char filename[50];
    memset(filename, 0, 50 * sizeof(char));
    sprintf(filename, "imgs/main/%s/%d.bin", categoryString, i);
    file = SD.open(filename, FILE_READ);
    uint32_t size = file.size();
    byte* message = (byte*)(malloc(sizeof(Pixel) * size + sizeof(byte) * MESSAGE_HEADER));
    file.readBytes((message + MESSAGE_HEADER), size);
    file.close();
    
    message[0] = 'a';                   //command
    message[1] = category;              // category
    message[2] = i;                     // element
    message[3] = size & 0xFF;           // file size LSB (mask for first 8 bits)
    message[4] = (size >> 8) & 0xFF;    // file size MSB (remaining 8 bits)
    BTSmartphone.write(message, MESSAGE_HEADER + size);

    free(message);
  }
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  BTRemote.begin(38400);
  BTSmartphone.begin(9600);
  SD.begin(SD_PIN);
  matrix.begin();
  matrix.setBrightness(10);
  matrix.clear();
  matrix.show();
}

void loop() {
  // put your main code here, to run repeatedly:
  if (BTRemote.available()) {
    byte state = BTRemote.read();
    if (state == 'e') face.currentEye = ++face.currentEye % EYE_SIZE;
    else if (state == 'f') face.currentMouth = ++face.currentMouth % MOUTH_SIZE;
    displayFace();
  }
  if (BTSmartphone.available()) {
    byte state = BTSmartphone.read();
    if (state == 'a') {
      sendCategory(MOUTH_CATEGORY);
      sendCategory(EYE_CATEGORY);
    }
    else if (state == 'u') {
      // update
    }
  }
  delay(20);
}

void displayFace() {
  matrix.clear();
  Pixel pixel;
  char filename[50];
  memset(filename, 0, 50 * sizeof(char));
  sprintf(filename, "imgs/main/mouthes/%d.bin", face.currentMouth + 1);
  file = SD.open(filename, FILE_READ);
  while(file.available()) {
    file.read((uint8_t *)&pixel, sizeof(Pixel) / sizeof(uint8_t));
    //Serial.print(pixel.pos); Serial.print(' ');
    //Serial.print(pixel.r); Serial.print(' ');
    //Serial.print(pixel.g); Serial.print(' ');
    //Serial.print(pixel.b); Serial.println();
    matrix.setPixelColor(pixel.pos, pixel.r, pixel.g, pixel.b);
  }
  file.close();
  //Serial.println();
  memset(filename, 0, 50);
  sprintf(filename, "imgs/main/eyes/%d.bin", face.currentEye + 1);
  file = SD.open(filename, FILE_READ);
  while(file.available()) {
    file.read((uint8_t *)&pixel, sizeof(Pixel) / sizeof(uint8_t));
    //Serial.print(pixel.pos); Serial.print(' ');
    //Serial.print(pixel.r); Serial.print(' ');
    //Serial.print(pixel.g); Serial.print(' ');
    //Serial.print(pixel.b); Serial.println();
    matrix.setPixelColor(pixel.pos, pixel.r, pixel.g, pixel.b);
  }
  file.close();
  matrix.show();
}