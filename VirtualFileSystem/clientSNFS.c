/*
Denny Sabu
John Sweeney

*/

#define FUSE_USE_VERSION 26

#include <fuse.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <netdb.h>
#include <math.h>
#include <stdio.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <string.h>
#include <netdb.h>

int startConnect();

int PORT;
struct hostent *hn;


static int hello_unlink(const char * path){

int bufsize = strlen(path) + 50;
char buf[bufsize];
bzero(buf, bufsize);

sprintf(buf, "%du%s", strlen(path) + 1, path);

int sok = startConnect();

int wrt = write(sok, buf, strlen(buf));



return 0;
}




static int hello_truncate(const char *path, off_t size){
	printf("Truncate(%s)\n", path);;
	char outbuf[5000];
	bzero(outbuf, 5000);

	char temp[1000];
	bzero(temp, 1000);

	sprintf(temp, "t%d,%s,", size, path);
	int len = strlen(temp);

	sprintf(outbuf, "%d%s", len, temp);

	int sok = startConnect();

	int wst = write(sok, outbuf, strlen(outbuf));

	bzero(outbuf, 5000);

	int rst = read(sok, outbuf, 5000);
	int res = atoi(outbuf);

	//printf("BUF: %s\nRES: %d\n", outbuf, res);

	if(res == -1){
		errno = res;
		res = -1;
	}
	else{
		return res;
	}
	
}


static int hello_release(const char *path, struct fuse_file_info *fi){

	printf("Release(%s)\n", path);;


	char temp[64];
	char temp1[90];

	bzero(temp1, 90);
	bzero(temp, 64);

	int fh = fi->fh;

	sprintf(temp, "l,%d,", fh);

	int len = strlen(temp);

	sprintf(temp1, "%d%s", len, temp);

	//printf("%s\n", temp1);

	int sok = startConnect();

	int swr = write(sok, temp1, strlen(temp1));

	return 0;

}


static int hello_mkdir(const char *path, mode_t mode){
	int socket = startConnect();
	printf("Mkdir(%s)\n", path);
	char buf[500] = {0};
	int len = strlen(path) + 2;
	sprintf(buf, "%dm%s,", len, path);
        int total = strlen(buf);
        int writen = 0;
       // printf("client befor sending: %s\t total: %d\n", msg, total);
        while(writen < total){
                int result = 0;
                result = write(socket, buf + writen, total - writen);
                writen += result;
        }
	char* buffer[50] = {0};
	int r = read(socket, buffer, 50);
	if(strcmp(buffer,"0") != 0)
		return -1;
	return 0;

	
}

static int hello_rmdir(const char* path){
	int socket = startConnect();
	printf("Rmdir(%s)\n", path);
	char msg[500] = {0};
	int len = strlen(path) + 2;
	sprintf(msg, "%dy%s,", len, path);
	int total = strlen(msg);
	int writen = 0;
	while(writen < total){
		int result = 0;
		result = write(socket, msg + writen, total - writen);
		writen += result;
	}
	
	char buffer[50] = {0};
	int j = read(socket, buffer, 50);
	int check = atoi(buffer);
	if(check != 0){
		check *= -1;
		return check;
	}
	return 0; 
}



static int hello_write(const char * path, const char * buf, size_t sizet, off_t offset, struct fuse_file_info *fi ){
	printf("Write(%s)\n", path);;
	
	char buf_out[5000];
	bzero(buf_out, 5000);
	
	char temp[4000];
	bzero(temp, 4000);

	int lenbuf = strlen(buf);

	sprintf(temp, "w%d,%s", fi->fh, buf);

	int tlen = strlen(temp);

	int totallen = tlen + floor(log10(abs(tlen))) + 2;

	sprintf(buf_out, "%d%s", tlen+1, temp);

	//printf("outgoing: \"%s\"\n", buf_out);

	int sok = startConnect();
	int wst = write(sok, buf_out, totallen);
	bzero(buf, 64);
	bzero(temp, 4000);

	int r = read(sok, temp, 4000);
	int b = atoi(temp);

	return b;


	//printf("----------------</WRITE>-----------------");

}


static int hello_getattr(const char *path, struct stat * st){


	printf("Get Attr(%s)\n", path);
	int pathLen = strlen(path);
	//Set up buffer
	// Need [msg len][char],[path]
	char buf[pathLen + 50];

	memset(buf, '\0', sizeof(buf));


	char temp[500];
	bzero(temp, 500);
	int x = strlen(path);
	sprintf(temp, ",%d,%s", x, path);
	int y = strlen(temp);
	sprintf(buf, "%d%c%s", y + 1, 'a', temp);

	//Send msg to server
	int sok = startConnect();
	int status = write(sok, buf, strlen(buf));

	if(status == -1){
		printf("here\n");
		return -1;
	}
	//Read msg back

	char *readbuf = malloc(5000);
	bzero(readbuf, 5000);
	int rstatus = read(sok, readbuf, 5000);

	//printf("BUFFER READ: %s\n", readbuf);
	if(rstatus == -1){
		return -1;
	}


	char * ser = strtok(readbuf, ",");
	char * ssize = strtok(NULL, ",");

	int err = atoi(ser);


	int flen = strlen(ser);
	int sslen = strlen(ssize);

	int of = flen + sslen + 2;

	struct stat * q;
	q = (struct stat *)(readbuf + of);

	st -> st_uid = getuid();
	st -> st_gid = getgid();

	//st ->  	st_uid	     = q->st_uid;
	//st -> 	st_gid       = q->st_gid;
	st -> 	st_atime     = q->st_atime;
	st -> 	st_mtime     = q->st_mtime;
	st ->   st_mode      = q->st_mode;
	st -> 	st_nlink     = q->st_nlink;
	st -> 	st_size      = q->st_size;


//printf("-------------GET ATTR ENDING----------------\n");

	if(err != 0)
		return err;
	else
		return 0;
}

static int hello_readdir(const char *path, void *buf, fuse_fill_dir_t filler,
			 off_t offset, struct fuse_file_info *fi)
{

	printf("Read Dir(%s)\n", path);;
	
	int socket = startConnect();
	char msg[500] = {0};
	int len = strlen(path) + 2;
	sprintf(msg, "%dd%s,", len, path);
	int total = strlen(msg);
	int writen = 0;
	//printf("client befor sending: %s\t total: %d\n", msg, total);
	while(writen < total){
		int result = 0;
		result = write(socket, msg + writen, total - writen);
		writen += result;
	}	
	//printf("we out dat wile lup\n");
	char info[5000]; 
	int red = read(socket, info, 5000);
//	printf("%s\n", info);
	
	int numfile  = atoi(strtok(info, ","));
	//printf("numfile:%d\n ",numfile); 
	int i = 0;
	while(i < numfile){
		char *name = strtok(NULL, ",");
		int offset = atoi(strtok(NULL,","));
		filler(buf, name, NULL, 0);	
		i++;
	}
	//printf("\n\n-------------------ENDING READDIR------------------\n");
	return 0;
}


static int hello_opendir(const char * path, struct fuse_file_info * fi){
	printf("Open Dir(%s)\n", path);;

	//printf("\n\n-------------------ENDING OPENDIR---------------------\n");
	return 0;
}


static int hello_create(const char * path, mode_t mode, struct fuse_file_info * fi){

	printf("Create(%s)\n", path);;

	int socket = startConnect();
	char msg[1024];
	bzero(msg, 1024);
	char bu[1000] = {'\0'};

	sprintf(bu,"c%s,%d,", path, fi->flags);
	int tlen = strlen(bu);

	//printf("FLAGS: %d\n", fi->flags);

	memcpy(bu + tlen, &mode, sizeof(mode_t));


	//mode_t * jim = bu + tlen;
	//printf("Mode Given: %d\n JIM: %d\n", mode, *jim);

	int l1 = tlen + sizeof(mode_t);

	sprintf(msg, "%d", l1);
	int msglenlen = strlen(msg);

	memcpy(msg + strlen(msg), bu, l1);



	int written = 0;
	int x = l1 + msglenlen;


	while(written < x){
		int result = 0;
		result = write(socket, msg + written, x - written);
		written += result;
	}

	char filehandler[500] = {'\0'};
	int red = read(socket, filehandler, 500);
	//uint64_t * a = (uint64_t *)malloc(sizeof(uint64_t));
	//memcpy(a, filehandler, sizeof(uint64_t));
	int a = atoi(filehandler);
	//printf("file handle: %d\n", a);
	fi->fh = a;
	//printf("Stored in di->fh: %d\n", fi->fh);
	//printf("--------CREATE ENDED---------\n\n")	;

	return 0;
}


static int hello_open(const char *path, struct fuse_file_info *fi){
	printf("open(%s)\n", path);;
	int flags = fi->flags;

	int socket = startConnect();
	int len = strlen(path);
	int nDigits = floor(log10(abs(flags))) + 1;
	char msg[1024];
	int sz = len + nDigits + 2;
	sprintf(msg, "%do%d,%s", sz, flags, path);
	//printf("%s\n", msg);
	int written = 0;
	int x = strlen(msg);
	while(written < x){
		int result = 0;
		result = write(socket, msg + written, x - written);
		written += result;
	}
	char buf[500];
	int red = read(socket, buf, 500);
	if(red < 0)
		printf("bad read\n");

	//printf("Buffer read: %s\n",buf);

	int err = atoi(strtok(buf, ","));
	int fh = atoi(strtok(NULL, "\0"));

	int * fhan = (int *)malloc(sizeof(int));
	memcpy(fhan, &fh, sizeof(sizeof(int)));


	//printf("File handller Passed: %d\n", fh);


	fi->fh = fh;
	//printf("Stored in di->fh: %d\n", fi->fh);
	//printf("-----------OPEN ENDING----------\n");
	return 0;
}


static int hello_read(const char *path, char *buf, size_t size, off_t offset, struct fuse_file_info *fi){
	printf("Read(%s)\n", path);;
	//printf("Stored in di->fh: %d\n", fi->fh);

	char buf_out[5000];
	bzero(buf_out, 5000);
	
	char temp[4000];
	bzero(temp, 4000);

	sprintf(temp, "r%d,", fi->fh);
	int tlen = strlen(temp);

	sprintf(buf_out, "%d%s", tlen , temp);
	//printf("CLIENT MSG: %s\n", buf_out);

	int sk = startConnect();
	int wrst = write(sk, buf_out, strlen(buf_out));

////////////////////////////////////////////////////////////////////////////////////////////////////////

	char buf_in[6000];
	bzero(buf_in, 6000);

	int rst = read(sk, buf_in, 6000);

	//printf("BACK: %s\nread: %d\n", buf_in, rst);

	char * ml = strtok(buf_in, ",");
	int ret = atoi(ml);
	int offset1 = floor(log10(abs(ret))) + 2;
	char * mp = buf_in + offset1;
	

	if(ret != 0){
		//printf("MSG: %s %d\n", mp, offset1);
		strncpy(buf, mp, ret);
	}
	//Return is nuber of bytes read
	return ret;
}

int startConnect(){
	int sockfd = 0;
	struct sockaddr_in sock;

	if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) < 0){
		printf("\n Socket creation error \n");

	}

	char * id = inet_ntoa(*((struct in_addr*) hn->h_addr_list[0]));

	sock.sin_family = AF_INET;
	sock.sin_port = htons(PORT);
	sock.sin_addr.s_addr = inet_addr(id);

	int connfd = connect(sockfd, ((struct sockaddr*) &sock), sizeof(sock));
	if(connfd < 0)
		printf("messedup\n");

	return sockfd;
}


static struct fuse_operations hello_oper = {
	.getattr	= hello_getattr,
	.readdir	= hello_readdir,
	.opendir 	= hello_opendir,
	.open		= hello_open,
	.read		= hello_read,
	.mkdir 		= hello_mkdir,
	.rmdir		= hello_rmdir,
	.write		= hello_write,
	.create		= hello_create,
	.release 	= hello_release,
	.truncate	= hello_truncate,
	.unlink		= hello_unlink,
};



int main(int argc, char **argv){
	PORT = atoi(argv[1]);
	const char *name = argv[2];
	hn = gethostbyname(name);
	struct fuse_args args = FUSE_ARGS_INIT(0, NULL);
	fuse_opt_add_arg(&args, argv[0]);
	fuse_opt_add_arg(&args, argv[3]);
	fuse_opt_add_arg(&args, argv[4]);
	return fuse_main(args.argc, args.argv, &hello_oper, NULL);
}
