#include <SD.h>

typedef struct Pixel {
    uint16_t pos;
    uint8_t r;
    uint8_t g;
    uint8_t b;
} Pixel;

File file;

void writePixel(uint16_t pos, uint8_t r, uint8_t g, uint8_t b) {
  Pixel pixel = {pos, r, g, b};
  file.write((uint8_t *)&pixel, sizeof(pixel)/sizeof(uint8_t));
  return;
}

void saveMouthes();
void saveEyes();

void setup() {
  pinMode(LED_BUILTIN, OUTPUT);
  if (!SD.begin(7)) {
    while (1);
  }
  if (!SD.exists("imgs")) SD.mkdir("imgs");
  if (!SD.exists("imgs/main")) SD.mkdir("imgs/main");
  if (!SD.exists("imgs/main/mouthes")) SD.mkdir("imgs/main/mouthes");
  if (!SD.exists("imgs/main/eyes")) SD.mkdir("imgs/main/eyes");
  digitalWrite(LED_BUILTIN, HIGH);
  saveMouthes();
  saveEyes();
  digitalWrite(LED_BUILTIN, LOW);
}

void loop() {
  // put your main code here, to run repeatedly:

}

void saveMouthes() {
  file = SD.open("imgs/main/mouthes/1", FILE_WRITE);
  file.seek(0);
  writePixel(70, 0, 0, 255);
  writePixel(71, 0, 0, 255);
  writePixel(72, 0, 0, 255);
  writePixel(73, 0, 0, 255);
  writePixel(85, 0, 0, 255);
  writePixel(90, 0, 0, 255);
  file.close();
  file = SD.open("imgs/main/mouthes/2", FILE_WRITE);
  file.seek(0);
  writePixel(69, 0, 0, 255);
  writePixel(74, 0, 0, 255);
  writePixel(86, 0, 0, 255);
  writePixel(87, 0, 0, 255);
  writePixel(88, 0, 0, 255);
  writePixel(89, 0, 0, 255);
  file.close();
  file = SD.open("imgs/main/mouthes/3", FILE_WRITE);
  file.seek(0);
  writePixel(69, 0, 0, 255);
  writePixel(70, 0, 0, 255);
  writePixel(71, 0, 0, 255);
  writePixel(72, 0, 0, 255);
  writePixel(73, 0, 0, 255);
  writePixel(74, 0, 0, 255);
  file.close();
  file = SD.open("imgs/main/mouthes/4", FILE_WRITE);
  file.seek(0);
  writePixel(56, 0, 0, 255);
  writePixel(57, 0, 0, 255);
  writePixel(58, 0, 0, 255);
  writePixel(69, 0, 0, 255);
  writePixel(71, 0, 0, 255);
  writePixel(85, 0, 0, 255);
  writePixel(86, 0, 0, 255);
  writePixel(87, 0, 0, 255);
  writePixel(88, 0, 0, 255);
  writePixel(89, 0, 0, 255);
  writePixel(90, 0, 0, 255);
  file.close();
  file = SD.open("imgs/main/mouthes/5", FILE_WRITE);
  file.seek(0);
  writePixel(53, 0, 0, 255);
  writePixel(69, 0, 0, 255);
  writePixel(70, 0, 0, 255);
  writePixel(71, 0, 0, 255);
  writePixel(72, 0, 0, 255);
  writePixel(73, 0, 0, 255);
  writePixel(74, 0, 0, 255);
  writePixel(85, 0, 0, 255);
  file.close();
  file = SD.open("imgs/main/mouthes/6", FILE_WRITE);
  file.seek(0);
  writePixel(39, 0, 0, 255);
  writePixel(40, 0, 0, 255);
  writePixel(54, 0, 0, 255);
  writePixel(70, 0, 0, 255);
  writePixel(73, 0, 0, 255);
  writePixel(86, 0, 0, 255);
  writePixel(87, 0, 0, 255);
  writePixel(88, 0, 0, 255);
  writePixel(89, 0, 0, 255);
  file.close();
}

void saveEyes() {
  file = SD.open("imgs/main/eyes/1", FILE_WRITE);
  file.seek(0);
  writePixel(148, 0, 0, 255);
  writePixel(155, 0, 0, 255);
  writePixel(164, 0, 0, 255);
  writePixel(171, 0, 0, 255);
  writePixel(180, 0, 0, 255);
  writePixel(187, 0, 0, 255);
  file.close();
  file = SD.open("imgs/main/eyes/2", FILE_WRITE);
  file.seek(0);
  writePixel(148, 0, 0, 255);
  writePixel(155, 0, 0, 255);
  writePixel(164, 0, 0, 255);
  writePixel(171, 0, 0, 255);
  writePixel(196, 0, 0, 255);
  writePixel(203, 0, 0, 255);
  writePixel(211, 0, 0, 255);
  writePixel(220, 0, 0, 255);
  file.close();
  file = SD.open("imgs/main/eyes/3", FILE_WRITE);
  file.seek(0);
  writePixel(148, 0, 0, 255);
  writePixel(155, 0, 0, 255);
  writePixel(164, 0, 0, 255);
  writePixel(171, 0, 0, 255);
  writePixel(195, 0, 0, 255);
  writePixel(204, 0, 0, 255);
  writePixel(212, 0, 0, 255);
  writePixel(219, 0, 0, 255);
  file.close();
  file = SD.open("imgs/main/eyes/4", FILE_WRITE);
  file.seek(0);
  writePixel(147, 0, 0, 255);
  writePixel(148, 0, 0, 255);
  writePixel(149, 0, 0, 255);
  writePixel(154, 0, 0, 255);
  writePixel(155, 0, 0, 255);
  writePixel(156, 0, 0, 255);
  file.close();
  file = SD.open("imgs/main/eyes/5", FILE_WRITE);
  file.seek(0);
  writePixel(147, 0, 0, 255);
  writePixel(149, 0, 0, 255);
  writePixel(154, 0, 0, 255);
  writePixel(156, 0, 0, 255);
  writePixel(164, 0, 0, 255);
  writePixel(171, 0, 0, 255);
  file.close();
  file = SD.open("imgs/main/eyes/6", FILE_WRITE);
  file.seek(0);
  writePixel(148, 0, 0, 255);
  writePixel(155, 0, 0, 255);
  writePixel(165, 0, 0, 255);
  writePixel(170, 0, 0, 255);
  writePixel(180, 0, 0, 255);
  writePixel(187, 0, 0, 255);
  file.close();
}