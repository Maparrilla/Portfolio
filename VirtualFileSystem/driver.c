#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>


int main(){

	//printf("%d\n", S_IRUSR | S_IRGRP | S_IROTH);

  //int f = open("/tmp/t2/test.txt", O_RDWR|O_CREAT, 0666);
  int x = truncate("/tmp/t2/test.txt", 5);
  printf("return: %d\n", x);
  //printf("open result: %d\nERRNO: %d\n\n\n\n", f, errno);
  //close(f);









return 0;
}
