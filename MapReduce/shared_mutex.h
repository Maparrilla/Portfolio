#ifndef SHARED_MUTEX_H
#define SHARED_MUTEX_H

//#define _BSD_SOURCE 
#include <pthread.h> 
                     
typedef struct shared_mutex_t {
  pthread_mutex_t *ptr; 
                        
  int shm_fd; 
  
  char* name;           
                        
  int created;        
                       
} shared_mutex_t;

shared_mutex_t shared_mutex_init(const char *name);

int shared_mutex_close(shared_mutex_t mutex);

int shared_mutex_destroy(shared_mutex_t mutex);

#endif // SHARED_MUTEX_H
