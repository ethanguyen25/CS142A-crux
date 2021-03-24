#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <inttypes.h>

void printInt(int64_t val) {
    printf("%" PRId64, val);
}

void printChar(int64_t val) {
  printf("%c", (unsigned char) val);
}

void println() {
    printf("\n");
}

void printBool(int64_t val) {
    if (val != 0)
        printf("true");
    else
        printf("false");
}

int64_t readInt() {
    int64_t val;
    printf("int?");
    scanf("%" PRId64, &val);
    return val;
}

int64_t readChar() {
    char val;
    if (scanf("%c", &val) == EOF) {
      printf("Read character past EOF\n");
      exit(-1);
    }
    return (int64_t) val;
}
