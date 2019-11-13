# Map-and-Reduce

Overview

For this assignment, you will implement a framework for multithreaded data processing on a single computer that follows the “MapReduce” paradigm.

MapReduce is a popular programming paradigm for data intensive computing in clustered environments such as enterprise data-centers and clouds. MapReduce is used as a framework for solving number of parallel tasks, using a large number of CPUs or computers (nodes), collectively referred to as a cluster.

The name MapReduce comes from the two-phase manner in which the data is processed:

In the map phase, the input is divided into smaller sub-problems, and distributed to individual worker nodes. The worker node then processes the smaller problem, and passes the answer back to the framework. 

In the reduce phase, the framework then takes the answers to all the sub-problems and combines them in a way to get the final output for the problem being solved. 

Assignment

1. You are required to design and implement a MapReduce framework using C or C++ on Linux.

2. Your framework must be configurable to provide parallelism in two distinct ways:
a. Using multiple Linux processes
b. Using POSIX threads (pthreads)

3. Your framework should be designed in a general manner. In particular, the problem to be solved (see item 5 below) should be encapsulated solely in the implementation of the map() and reduce() functions. Substitution of different map() and reduce() functions should enable your framework to solve a different problem without any further changes in the framework itself.

4. All intermediate data that is to be transferred between the map and reduce phases is to be stored in POSIX shared memory. Reading and writing to this shared memory may require synchronization (mutual exclusion).  You may NOT write intermediate output to the file system.  There also needs to be synchronization (barrier) to explicitly separate the map and reduce phases.

5. Once your framework is working, you must implement two sets of map/reduce functions to solve the following problems:
a. Word count: given an input file, you must count the number of times each individual word appears in the input, and output a file containing a list of words followed by their counts.
b. Integer sort: given an input file containing a list of integers, you must output a file containing the same integers in sorted, ascending order.

6. You will be provided with sample input and output files for each problem. Your implementation must be able to process the example input and must produce output in exactly the same format as the example output file. (These sample files will be attached to the assignment on Sakai).

7. Your framework must compile to a single executable file called mapred, that conforms to the following command line structure:

mapred –-app [wordcount, sort] –-impl [procs, threads]
--maps num_maps –-reduces num_reduces --input infile
–-output outfile


You should be able to execute this four different ways:
wordcount using processes
wordcount using threads
sort using processes
sort using threads
with any arbitrary number of maps and reduces.




--------------------------------------------------------------------------------------------------------------------------------


# Project Outline

### Initial Read In

Given a File Input Stream and a Vector, the *tokenizer()* function populates the Vector with the input from the file input stream. All inputs are read in and saved as lowercase to the vector.

### Delegator

After *tokenizer()* populates the vector, the *delegator()* function takes the vector, and preforms the operations to split it into *n* smaller vectors to be handed off to the threads/processes.


