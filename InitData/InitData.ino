#include <SD.h>

typedef struct Pixel {
    uint16_t pos;
    uint8_t r;
    uint8_t g;
    uint8_t b;
} Pixel;

File file;
char* path = "imgs/main/";
char* backupPath = "imgs/backup/";
String mouthesPath = "imgs/main/mouthes/";
String eyesPath = "imgs/main/eyes/";
String mouthesBackupPath = "imgs/backup/mouthes/";
String eyesBackupPath = "imgs/backup/eyes/";

void saveMouthes();
void saveEyes();

void setup() {
  pinMode(LED_BUILTIN, OUTPUT);
  if (!SD.begin(7)) {
    while(1);
  };
  if (!SD.exists("imgs")) SD.mkdir("imgs");
  if (!SD.exists(path)) SD.mkdir(path);
  if (!SD.exists(backupPath)) SD.mkdir(backupPath);
  if (!SD.exists(mouthesPath)) SD.mkdir(mouthesPath);
  if (!SD.exists(eyesPath)) SD.mkdir(eyesPath);
  if (!SD.exists(mouthesBackupPath)) SD.mkdir(mouthesBackupPath);
  if (!SD.exists(eyesBackupPath)) SD.mkdir(eyesBackupPath);
  digitalWrite(LED_BUILTIN, HIGH);
  saveMouthes();
  saveEyes();
  digitalWrite(LED_BUILTIN, LOW);
  return;
}

void loop() {
  // put your main code here, to run repeatedly:

}

void writePixel(Pixel pixel) {
  file.write((uint8_t *)&pixel, sizeof(Pixel) / sizeof(uint8_t));
  return;
}

void writePixel(uint16_t pos, uint8_t r, uint8_t g, uint8_t b) {
  Pixel pixel = {pos, r, g, b};
  writePixel(pixel);
  return;
}

void writeFacePart(Pixel* pixels, int size, char* filename) {
  if (SD.exists(filename)) SD.remove(filename);
  file = SD.open(filename, FILE_WRITE);
  file.seek(0);
  for (int i = 0; i < size; i++) {
    writePixel(pixels[i]);
  }
  file.close();
  return;
}

void writeFacePart(Pixel* pixels, int size, String filename) {
  if (SD.exists(filename)) SD.remove(filename);
  file = SD.open(filename, FILE_WRITE);
  file.seek(0);
  for (int i = 0; i < size; i++) {
    writePixel(pixels[i]);
  }
  file.close();
  return;
}

void saveMouthes() {
  Pixel pixels[30];
  pixels[0] = { 70, 0, 0, 255 };
  pixels[1] = { 71, 0, 0, 255 };
  pixels[2] = { 72, 0, 0, 255 };
  pixels[3] = { 73, 0, 0, 255 };
  pixels[4] = { 85, 0, 0, 255 };
  pixels[5] = { 90, 0, 0, 255 };
  writeFacePart(pixels, 6, mouthesPath + "1.bin");
  writeFacePart(pixels, 6, mouthesBackupPath + "1.bin");
  pixels[0] = { 69, 0, 0, 255 };
  pixels[1] = { 74, 0, 0, 255 };
  pixels[2] = { 86, 0, 0, 255 };
  pixels[3] = { 87, 0, 0, 255 };
  pixels[4] = { 88, 0, 0, 255 };
  pixels[5] = { 89, 0, 0, 255 };
  writeFacePart(pixels, 6, mouthesPath + "2.bin");
  writeFacePart(pixels, 6, mouthesBackupPath + "2.bin");
  pixels[0] = { 69, 0, 0, 255 };
  pixels[1] = { 70, 0, 0, 255 };
  pixels[2] = { 71, 0, 0, 255 };
  pixels[3] = { 72, 0, 0, 255 };
  pixels[4] = { 73, 0, 0, 255 };
  pixels[5] = { 74, 0, 0, 255 };
  writeFacePart(pixels, 6, mouthesPath + "3.bin");
  writeFacePart(pixels, 6, mouthesBackupPath + "3.bin");
  pixels[0] = { 56, 0, 0, 255 };
  pixels[1] = { 57, 0, 0, 255 };
  pixels[2] = { 58, 0, 0, 255 };
  pixels[3] = { 69, 0, 0, 255 };
  pixels[4] = { 71, 0, 0, 255 };
  pixels[5] = { 85, 0, 0, 255 };
  pixels[6] = { 86, 0, 0, 255 };
  pixels[7] = { 87, 0, 0, 255 };
  pixels[8] = { 88, 0, 0, 255 };
  pixels[9] = { 89, 0, 0, 255 };
  pixels[10] = { 90, 0, 0, 255 };
  writeFacePart(pixels, 11, mouthesPath + "4.bin");
  writeFacePart(pixels, 11, mouthesBackupPath + "4.bin");
  pixels[0] = { 53, 0, 0, 255 };
  pixels[1] = { 69, 0, 0, 255 };
  pixels[2] = { 70, 0, 0, 255 };
  pixels[3] = { 71, 0, 0, 255 };
  pixels[4] = { 72, 0, 0, 255 };
  pixels[5] = { 73, 0, 0, 255 };
  pixels[6] = { 74, 0, 0, 255 };
  pixels[7] = { 85, 0, 0, 255 };
  writeFacePart(pixels, 8, mouthesPath + "5.bin");
  writeFacePart(pixels, 8, mouthesBackupPath + "5.bin");
  pixels[0] = { 39, 0, 0, 255 };
  pixels[1] = { 40, 0, 0, 255 };
  pixels[2] = { 54, 0, 0, 255 };
  pixels[3] = { 57, 0, 0, 255 };
  pixels[4] = { 70, 0, 0, 255 };
  pixels[5] = { 73, 0, 0, 255 };
  pixels[6] = { 86, 0, 0, 255 };
  pixels[7] = { 87, 0, 0, 255 };
  pixels[8] = { 88, 0, 0, 255 };
  pixels[9] = { 89, 0, 0, 255 };
  writeFacePart(pixels, 10, mouthesPath + "6.bin");
  writeFacePart(pixels, 10, mouthesBackupPath + "6.bin");
  pixels[0] = { 38, 0, 0, 255 };
  pixels[1] = { 39, 0, 0, 255 };
  pixels[2] = { 40, 0, 0, 255 };
  pixels[3] = { 41, 0, 0, 255 };
  pixels[4] = { 54, 0, 0, 255 };
  pixels[5] = { 57, 0, 0, 255 };
  pixels[6] = { 70, 0, 0, 255 };
  pixels[7] = { 73, 0, 0, 255 };
  pixels[8] = { 87, 0, 0, 255 };
  pixels[9] = { 88, 0, 0, 255 };
  writeFacePart(pixels, 10, mouthesPath + "7.bin");
  writeFacePart(pixels, 10, mouthesBackupPath + "7.bin");
  pixels[0] = { 56, 0, 0, 255 };
  pixels[1] = { 70, 0, 0, 255 };
  pixels[2] = { 88, 0, 0, 255 };
  writeFacePart(pixels, 3, mouthesPath + "8.bin");
  writeFacePart(pixels, 3, mouthesBackupPath + "8.bin");
  pixels[0] = { 69, 0, 0, 255 };
  pixels[1] = { 70, 0, 0, 255 };
  pixels[2] = { 73, 0, 0, 255 };
  pixels[3] = { 74, 0, 0, 255 };
  pixels[4] = { 84, 0, 0, 255 };
  pixels[5] = { 87, 0, 0, 255 };
  pixels[6] = { 88, 0, 0, 255 };
  pixels[7] = { 91, 0, 0, 255 };
  pixels[8] = { 100, 0, 0, 255 };
  pixels[9] = { 107, 0, 0, 255 };
  writeFacePart(pixels, 10, mouthesPath + "9.bin");
  writeFacePart(pixels, 10, mouthesBackupPath + "9.bin");
  pixels[0] = { 68, 0, 0, 255 };
  pixels[1] = { 70, 0, 0, 255 };
  pixels[2] = { 73, 0, 0, 255 };
  pixels[3] = { 75, 0, 0, 255 };
  pixels[4] = { 85, 0, 0, 255 };
  pixels[5] = { 87, 0, 0, 255 };
  pixels[6] = { 88, 0, 0, 255 };
  pixels[7] = { 90, 0, 0, 255 };
  writeFacePart(pixels, 8, mouthesPath + "10.bin");
  writeFacePart(pixels, 8, mouthesBackupPath + "10.bin");
  return;
}

void saveEyes() {
  Pixel pixels[30];
  pixels[0] = { 148, 0, 0, 255 };
  pixels[1] = { 155, 0, 0, 255 };
  pixels[2] = { 164, 0, 0, 255 };
  pixels[3] = { 171, 0, 0, 255 };
  pixels[4] = { 180, 0, 0, 255 };
  pixels[5] = { 187, 0, 0, 255 };
  writeFacePart(pixels, 6, eyesPath + "1.bin");
  writeFacePart(pixels, 6, eyesBackupPath + "1.bin");
  pixels[0] = { 148, 0, 0, 255 };
  pixels[1] = { 155, 0, 0, 255 };
  pixels[2] = { 164, 0, 0, 255 };
  pixels[3] = { 171, 0, 0, 255 };
  pixels[4] = { 196, 0, 0, 255 };
  pixels[5] = { 203, 0, 0, 255 };
  pixels[6] = { 211, 0, 0, 255 };
  pixels[7] = { 220, 0, 0, 255 };
  writeFacePart(pixels, 8, eyesPath + "2.bin");
  writeFacePart(pixels, 8, eyesBackupPath + "2.bin");
  pixels[0] = { 148, 0, 0, 255 };
  pixels[1] = { 155, 0, 0, 255 };
  pixels[2] = { 164, 0, 0, 255 };
  pixels[3] = { 171, 0, 0, 255 };
  pixels[4] = { 195, 0, 0, 255 };
  pixels[5] = { 204, 0, 0, 255 };
  pixels[6] = { 212, 0, 0, 255 };
  pixels[7] = { 219, 0, 0, 255 };
  writeFacePart(pixels, 8, eyesPath + "3.bin");
  writeFacePart(pixels, 8, eyesBackupPath + "3.bin");
  pixels[0] = { 147, 0, 0, 255 };
  pixels[1] = { 148, 0, 0, 255 };
  pixels[2] = { 149, 0, 0, 255 };
  pixels[3] = { 154, 0, 0, 255 };
  pixels[4] = { 155, 0, 0, 255 };
  pixels[5] = { 156, 0, 0, 255 };
  writeFacePart(pixels, 6, eyesPath + "4.bin");
  writeFacePart(pixels, 6, eyesBackupPath + "4.bin");
  pixels[0] = { 147, 0, 0, 255 };
  pixels[1] = { 149, 0, 0, 255 };
  pixels[2] = { 154, 0, 0, 255 };
  pixels[3] = { 156, 0, 0, 255 };
  pixels[4] = { 164, 0, 0, 255 };
  pixels[5] = { 171, 0, 0, 255 };
  writeFacePart(pixels, 6, eyesPath + "5.bin");
  writeFacePart(pixels, 6, eyesBackupPath + "5.bin");
  pixels[0] = { 148, 0, 0, 255 };
  pixels[1] = { 155, 0, 0, 255 };
  pixels[2] = { 165, 0, 0, 255 };
  pixels[3] = { 170, 0, 0, 255 };
  pixels[4] = { 180, 0, 0, 255 };
  pixels[5] = { 187, 0, 0, 255 };
  writeFacePart(pixels, 6, eyesPath + "6.bin");
  writeFacePart(pixels, 6, eyesBackupPath + "6.bin");
  pixels[0] = { 148, 0, 0, 255 };
  pixels[1] = { 155, 0, 0, 255 };
  pixels[2] = { 164, 0, 0, 255 };
  pixels[3] = { 170, 0, 0, 255 };
  pixels[4] = { 180, 0, 0, 255 };
  pixels[5] = { 187, 0, 0, 255 };
  writeFacePart(pixels, 6, eyesPath + "7.bin");
  writeFacePart(pixels, 6, eyesBackupPath + "7.bin");
  pixels[0] = { 148, 0, 0, 255 };
  pixels[1] = { 155, 0, 0, 255 };
  pixels[2] = { 165, 0, 0, 255 };
  pixels[3] = { 171, 0, 0, 255 };
  pixels[4] = { 180, 0, 0, 255 };
  pixels[5] = { 187, 0, 0, 255 };
  writeFacePart(pixels, 6, eyesPath + "8.bin");
  writeFacePart(pixels, 6, eyesBackupPath + "8.bin");
  pixels[0] = { 147, 0, 0, 255 };
  pixels[1] = { 149, 0, 0, 255 };
  pixels[2] = { 154, 0, 0, 255 };
  pixels[3] = { 156, 0, 0, 255 };
  pixels[4] = { 164, 0, 0, 255 };
  pixels[5] = { 171, 0, 0, 255 };
  pixels[6] = { 179, 0, 0, 255 };
  pixels[7] = { 181, 0, 0, 255 };
  pixels[8] = { 186, 0, 0, 255 };
  pixels[9] = { 188, 0, 0, 255 };
  writeFacePart(pixels, 10, eyesPath + "9.bin");
  writeFacePart(pixels, 10, eyesBackupPath + "9.bin");
  pixels[0] = { 148, 0, 0, 255 };
  pixels[1] = { 155, 0, 0, 255 };
  pixels[2] = { 163, 0, 0, 255 };
  pixels[3] = { 164, 0, 0, 255 };
  pixels[4] = { 165, 0, 0, 255 };
  pixels[5] = { 170, 0, 0, 255 };
  pixels[6] = { 171, 0, 0, 255 };
  pixels[7] = { 172, 0, 0, 255 };
  pixels[8] = { 178, 0, 0, 255 };
  pixels[9] = { 179, 0, 0, 255 };
  pixels[10] = { 180, 0, 0, 255 };
  pixels[11] = { 181, 0, 0, 255 };
  pixels[12] = { 182, 0, 0, 255 };
  pixels[13] = { 185, 0, 0, 255 };
  pixels[14] = { 186, 0, 0, 255 };
  pixels[15] = { 187, 0, 0, 255 };
  pixels[16] = { 188, 0, 0, 255 };
  pixels[17] = { 189, 0, 0, 255 };
  pixels[18] = { 194, 0, 0, 255 };
  pixels[19] = { 195, 0, 0, 255 };
  pixels[20] = { 197, 0, 0, 255 };
  pixels[21] = { 198, 0, 0, 255 };
  pixels[22] = { 201, 0, 0, 255 };
  pixels[23] = { 202, 0, 0, 255 };
  pixels[24] = { 204, 0, 0, 255 };
  pixels[25] = { 205, 0, 0, 255 };
  writeFacePart(pixels, 26, eyesPath + "10.bin");
  writeFacePart(pixels, 26, eyesBackupPath + "10.bin");
  return;
}