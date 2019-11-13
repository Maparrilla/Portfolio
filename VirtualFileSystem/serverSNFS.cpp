#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <cstdlib>
#include <iostream>
#include <cstdio>
#include <string.h>
#include <string>
#include <unistd.h>
#include <errno.h>
#include <pthread.h>
#include <math.h>
#include <sstream>
#include <dirent.h>
//SERVER

void *whatToDo(void*);
void serverStart();
void do_getAttr(int, char*);
void do_create(int, char*, int);
void do_open(int, char*);
void do_read(int, char *);
void do_write(int, char*);
void do_close(int, char *);
void do_readdir(int, char*);
void do_truncate(int, char*);
void do_mkdir(int, char*);
void do_unlink(int, char*);
void do_rmdir(int, char*);

using namespace std;

int PORT;
string mount;
pthread_mutex_t lock;

int main(int argc, char const *argv[]){
	PORT = atoi(argv[1]);
	mount = argv[2];
   	serverStart();
    	return 0;
}

void serverStart(){
    	int server_fd, new_socket;
    	struct sockaddr_in address;
	int addrlen = sizeof(address);

    	// Creating socket file descriptor
	if ((server_fd = socket(AF_INET, SOCK_STREAM, 0)) == 0){
		perror("socket failed");
		exit(EXIT_FAILURE);
	}

	address.sin_family = AF_INET;
	address.sin_addr.s_addr = INADDR_ANY;
	address.sin_port = htons( PORT );

	if (bind(server_fd, (struct sockaddr *)&address, sizeof(address))<0){
		perror("bind failed");

		exit(EXIT_FAILURE);
    	}

	if (listen(server_fd, 3) < 0){
		perror("listen");
		exit(EXIT_FAILURE);
        }

	while(1){
		//cout << "john sweeney" << endl;

		new_socket = accept(server_fd, (struct sockaddr *)&address, (socklen_t*)&addrlen);
		if (new_socket < 0){
    			perror("accept failed");
    			break;
		}
		pthread_mutex_init(&lock, NULL);
		pthread_t thread;
		int *newSocket = &new_socket;
		if(pthread_create(&thread, NULL, whatToDo, (void*) newSocket) < 0){
			perror("could not create thread");
			break;
		}
		//cout << "FUUUUUUUUCCCK" << endl;
	}
}


void *whatToDo(void *sock){
	int socket = *(int*)sock;
	char buffer[5000] = {0};

	int red = read(socket, buffer, 5000);
	char msglen[10] = {0};
	int i = 0;
	while(isdigit(buffer[i])){
		msglen[i] = buffer[i];
		i++;
	}
	int len = atoi(msglen);

	//printf("LEN: %d\nRED: %d\nRED COMP: %d\n\n", len, red, red - strlen(msglen));

	if(len != red - strlen(msglen)){

		printf("did not recive full msg; %s\n", buffer);
		return 0;
	}

	switch(buffer[strlen(msglen)]){
		//cases for all functions
		//todo
		//getattr 	
		case 'a':
			do_getAttr(socket, buffer);
			break;

		//create	
		case 'c':
			do_create(socket, buffer + strlen(msglen) + 1, len);
			break;
			
		//open		
		case 'o':
			do_open(socket, buffer + strlen(msglen) + 1);
			break;

		//read;
		case 'r':
			do_read(socket, buffer + strlen(msglen) + 1);
			break;
			
		//write
		case 'w':
			do_write(socket, buffer + strlen(msglen) + 1);
			break;

		//close
		case 'l':
			do_close(socket, buffer + strlen(msglen) + 1);
			break;
			
		//truncate
		case 't':
			do_truncate(socket, buffer + strlen(msglen) + 1);
			break;
		
		//readdir
		case 'd':
			do_readdir(socket, buffer + strlen(msglen) + 1);
			break;
			
		//rmdir
		case 'y':
			do_rmdir(socket, buffer + strlen(msglen) + 1);
			break;
			
		//mkdir
		case 'm':
			do_mkdir(socket, buffer + strlen(msglen) + 1);
			break;
			
		//Unlink
		case 'u':
			do_unlink(socket, buffer+strlen(msglen) + 1);
			break;
			
		default:
			cout << "unknown command from client" << endl;
	}

}

void do_getAttr(int socket, char * msg){
	cout << "----------GET ATTR RUNNING----------------" << endl;
	struct stat * st = (struct stat *)malloc(sizeof(struct stat));
	char * s = msg;
	strtok(s, ",");
	int mlen = atoi(strtok(NULL, ","));
	char * path = strtok(NULL, ",");

	//cout << "Len Path: " << mlen << "\nPath: " << path << endl;

	char bigPath[500];
	bzero(bigPath, 500);
	sprintf(bigPath, "%s%s", mount.c_str(), path);

	//cout << "New Path: " << bigPath << endl;

	int pStat = lstat(bigPath, st);
	int er = -errno;
	//printf("ERRNO: %d\n", errno);

	int structSize = sizeof(struct stat);
	int bufsize;
	if(er == 0){
		bufsize = structSize + floor(log10(abs(structSize)))+ 3;
	}
	else{
		bufsize = structSize + floor(log10(abs(er))) + floor(log10(abs(structSize)))+ 2;
	}
	char buf[bufsize];
	bzero(buf, bufsize);
	sprintf(buf, "%d,%d,", er, structSize);
	int tlen = strlen(buf);

	memcpy(buf + tlen, st ,sizeof(struct stat));
	//cout << "pointer check" << endl;

	struct stat * q;
	q = (struct stat *)(buf + tlen);


	int wSt = write(socket, buf, structSize + 4);
	//printf("%d\n", wSt);
	fsync(socket);
	close(socket);
	free(st);
	cout << "----------GET ATTR ENDING ----------------" << endl;
	pthread_exit(NULL);

}


void do_create(int socket, char *msg, int len){

	//cout << "MSG: " << msg << endl << endl;
	cout << "----------OCREATE RUNNING----------------" << endl;
	char fil[500];
	
	char * spath = strtok(msg, ",");
	char bigPath[500];
	bzero(bigPath, 500);
	sprintf(bigPath, "%s%s", mount.c_str(), spath);


	int s = open(bigPath,O_CREAT | O_RDWR, 0666);

	//cout << "This is s: " << s << endl << "And this is errno you stupid fuck: " << errno << endl;

	char y[1024] = {'\0'};
	sprintf(y, "%d", s);
	int x = write(socket, y, strlen(y));
	//cout << "DID LEAVE CREATE" << endl;
	cout << "----------CREATE ENDING----------------" << endl;
}

void do_readdir(int socket, char* msg){
	cout << "----------READIR RUNNING----------------" << endl;
	char *path = strtok(msg, ",");
	stringstream str;
	char direct[500];
	//cout << "server recived: " << msg << endl; 
	sprintf(direct, "%s%s", mount.c_str(), path);
	//cout << "server direct: " << direct << endl;
	struct dirent *de;
	DIR* dr = opendir(direct);
	if(dr == NULL)
		//cout << "opendir didnt work" << endl;
	de = readdir(dr);
	de = readdir(dr);
	int i = 0;
	while((de = readdir(dr)) != NULL){
		str <<  de->d_name <<  "," << ((24+strlen(de->d_name)+7)&~7) << ',';
		i++; 
	}
	//cout << "string stream: " << str << endl;
	string x = str.str();
	const char *buf =  x.c_str();
	char berf[600] = {0};
	sprintf(berf, "%d,%s", i, buf);
	int writen = 0;
        int total = strlen(berf);
	//cout << "c string: " << buf << endl;
        while(writen < total){
                int result = 0;
                result = write(socket, berf + writen, total - writen);
                writen += result;
        } 
	cout << "----------READDIR ENDING----------------" << endl;
}

void do_mkdir(int socket, char* msg){
	cout << "----------MKDIR RUNNING----------------" << endl;
	char* path = strtok(msg, ",");
	char buf[500] = {0};
	sprintf(buf, "%s%s",mount.c_str(),path);
	int res = mkdir(buf, 0777);
	if(res == -1){
		printf("make dir is brok");
	}
	char response[50];
	sprintf(response, "%d", res);
	int k = strlen(response);
	int r = write(socket, response, k);
	cout << "----------MKDIR ENDING----------------" << endl;
}

void do_rmdir(int socket, char* msg){
	cout << "----------RMDIR RUNNING----------------" << endl;
	char* path = strtok(msg, ",");
	char buf[500] = {0};
	sprintf(buf, "%s%s", mount.c_str(), path);
	int r = rmdir(buf);
	if(r == -1){
		char tmp[10] = {0};
		sprintf(tmp, "%d", errno);
		int p = write(socket, tmp, strlen(tmp));
	}
	else{
		char tmp[10] = {0};
		int z = 0;
		sprintf(tmp, "%d", z);
		int g = write(socket, tmp, strlen(tmp));
	}
	cout << "-------------RMDIR ENDING----------------" << endl;
}

void do_open(int socket, char *msg){
printf("-----------OPEN RUNNING----------\n");

/*
 *
 * ASSUMPTIONS:
 *
 * MSG IN FORMAT [FLAGS],[PATH];

*/

//Denny Revision
int tmlen = strlen(msg);


//Flags in char form
char * cflags = strtok(msg, ",");
//Lenght of flags
int lenflag = strlen(cflags);

int pathStartIndex = lenflag + 1;
//Make useable path;
char path[500];
bzero(path, 500);
strncpy(path, msg + pathStartIndex, tmlen - pathStartIndex);
//printf("Path: %s\n\n", path);

//Make bigpath
char bigPath[500];
bzero(bigPath, 500);
sprintf(bigPath, "%s%s", mount.c_str(), path);


//do open

int st = open(bigPath, atoi(cflags));
//printf("Open Status: %d\n", st);

//return [errno],[filehandler];


char rebuf[5000];
bzero(rebuf, 5000);
sprintf(rebuf, "%d,%d", errno, st);

int wst = write(socket, rebuf, strlen(rebuf) + 2);


		printf("-----------OPEN ENDING----------\n");
}


void do_read(int socket, char * msg){
	cout << "----------READ RUNNING----------------" << endl;
	char * sfp  = strtok(msg, ",");
	int fh = atoi(sfp);

	char c[5000];
	bzero(c, 5000);
	int rst = read(fh, c, 5000);

	char d[6000];
	bzero(d, 6000);
	sprintf(d,"%d,%s", rst, c);
//	printf("SENDING BACK: %s\n", d);

	int wst = write(socket, d, strlen(d));
	cout << "----------READ ENDING----------------" << endl;
}

void do_write(int socket, char * msg){
	cout << "----------WRITE RUNNING----------------" << endl;
	char * cfd = strtok(msg, ",");
	int fd = atoi(cfd);

	char * cmsgstart = (cfd + strlen(cfd) + 1);
	//printf("msg start: %s\n", cmsgstart);

	int st = write(fd, cmsgstart, strlen(cmsgstart));

	char returnf[64];
	bzero(returnf, 64);

	sprintf(returnf, "%d", st);

	int sokret = write(socket, returnf, strlen(returnf));
	cout << "------------WRITE ENDING----------------" << endl;
}


void do_close(int socket, char * msg){
printf("-----------CLOSE STARTING----------\n");



	//printf("msg: %s\n", msg);

	char buf[500];
	bzero(buf, 500);
	
	//int rst = read(socket, buf, 500);

	char * step = strtok(msg, ",");
	int fh = atoi(step);

	//printf("fh in close: %d\n", fh);

	close(fh);
	printf("-----------CLOSE ENDING----------\n");

}


void do_truncate(int socket, char * msg){
	cout << "----------TRUNCATE RUNNING----------------" << endl;
	char * csize = strtok(msg, ",");
	char * path = strtok(NULL, ",");

	int size = atoi(csize);

	char bigPath[500];
	bzero(bigPath, 500);
	sprintf(bigPath, "%s%s", mount.c_str(), path);

	int x = truncate(bigPath, size);
	bzero(bigPath, 500); 
	sprintf(bigPath, "%d", x);
	int y = write(socket, bigPath, strlen(bigPath));

	cout << "----------TRUNCATE ENDING----------------" << endl;

}


void do_unlink(int socket, char * msg){
cout << "----------UNLINK RUNNING----------------" << endl;
//printf("SERVER UNLINKING: %s\n", msg);

	char buf[500];
	bzero(buf, 500);

        char bigPath[500];
        bzero(bigPath, 500);
        sprintf(bigPath, "%s%s", mount.c_str(), msg);



int x = unlink(bigPath);
int err = errno;

sprintf(buf, "%d,%d", x, err);

//printf("*************RESPONSE: %s\n", buf);

int wst = write(socket, buf, sizeof(buf));
cout << "----------UNLINK ENDING----------------" << endl;
}







