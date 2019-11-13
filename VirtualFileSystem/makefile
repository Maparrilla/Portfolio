all:
	make server
	make client
	
server: serverSNFS.cpp
	g++ -o serverSNFS serverSNFS.cpp -pthread -lm

client:  clientSNFS.c
	gcc  -w clientSNFS.c `pkg-config fuse --cflags --libs` -o clientSNFS -lm

runc: 
	./clientSNFS 51223 cd.cs.rutgers.edu /tmp/t2 -f 

runs:
	./serverSNFS 51223 /tmp/t2

clean:
	rm clientSNFS serverSNFS
