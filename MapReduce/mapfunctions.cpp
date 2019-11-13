#include "mapred.h"
#include <pthread.h>


void * threadWorker(void *);
void * threadWorkerRed(void *);
void finalReduce(std::vector< std::pair <std::string, int > > * sortMe);

void * threadWorker(void *arg){
	threadInfo * thisThread = (threadInfo*)arg;

	pthread_mutex_t *  mtx = thisThread->mutex;

	std::vector <std::string > tempRead = *thisThread->readFrom; 
	std::vector<std::pair<std::string, int> > hol;

	for (int i = 0; i < tempRead.size(); i++){
		hol.push_back(make_pair(tempRead[i], 1));
	}
	
	

	if(thisThread->ind == 1)	
		std::sort(hol.begin(), hol.end(), comparePairsInts);

	



	for(int i = 0; i < tempRead.size(); i++){
        	//std::cout << tempRead[i] <<  "\n";
        	pthread_mutex_lock(mtx);
        	glb_vec.push_back(hol[i]);
        	pthread_mutex_unlock(mtx);
	}

	

        
        pthread_exit(NULL);
}
        

void * threadWorkerRed(void *arg){
	threadInfo * thisThread = (threadInfo*)arg;

	pthread_mutex_t *  mtx = thisThread->mutex;
	std::vector< std::pair<std::string, int> >  tempRead = *thisThread->rd;
	std::vector <std::pair <std::string, int > > * writeTo = thisThread->wr;

	if(thisThread->ind ==1)
		std::sort(tempRead.begin(), tempRead.end(), comparePairsInts);


	wordCombiner(&tempRead);
	
	
		for(int i = 0; i < tempRead.size(); i++){
			//std::cout << tempRead[i].first << " " << tempRead[i].second << "\n";
			pthread_mutex_lock(mtx);
			writeTo->push_back(tempRead[i]);
			pthread_mutex_unlock(mtx);
	
		}
        pthread_exit(NULL);

}

void finalReduce(std::vector< std::pair <std::string, int > > * sortMe){
for(int i = 0; i < sortMe->size()-1; i++){
        //std::cout << sortMe->at(i).first <<  "\n";
        std::string a = sortMe->at(i).first;
        for(int j = i +1; a == sortMe->at(j).first && j < sortMe->size(); j++){
                sortMe->at(i).second += sortMe->at(j).second ;
                sortMe->erase(sortMe->begin() + j);
		if(i > 0)
			i--;
        }


}

}



