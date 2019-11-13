all: mapred.cpp shared_mutex_t.o mapfunctions.o
	g++ -g -o mapred mapred.cpp shared_mutex_t.o mapfunctions.o -lrt -pthread

shared_mutex_t.o:
	g++ -c shared_mutex_t.cpp -lrt -pthread

mapfunctions.o:
	g++ -c mapfunctions.cpp -lpthread

runp: mapred
	./mapred wordcount procs 500 50 geo.txt yer.txt

runt: mapred
	./mapred wordcount threads 500 50 geo.txt yer.txt
	
clean:
	rm mapred shared_mutex_t.o mapfunctions.o
