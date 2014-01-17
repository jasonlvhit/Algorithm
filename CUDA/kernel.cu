#include <cstdio>
#include <fstream>
#include <iostream>
#include <time.h>
#include <windows.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>
#include <float.h>

#include "cutil_inline.h"

#define numberOfVertex  500
#define Max_Iteration_Number 10000
#define Alpha 0.15
#define END_WEIGHT 1e-7
#define InitPageRankValue 6

using namespace std;
static int CPUiter = 0;


//PageRank value calculate function

__global__ void PRAdd(float *PR, const float* Graph, const float * sumOfOutDegree)
{
    int i = blockDim.x * blockIdx.x + threadIdx.x;

    if (i < numberOfVertex )
    {
        float sum = 0.0;
        int k = 0;
        for (int j = i; j < numberOfVertex *numberOfVertex  ; j += numberOfVertex )
        {
            if (*(Graph + j) && sumOfOutDegree[k])
            {
                sum += PR[k] / sumOfOutDegree[k];

            }
            k++;
            //printf("%f\n", sum);
        } 
        PR[i] = Alpha  + (1 - Alpha)*sum;
    }
}

//Calculate Sum of out_degree of each vertex.
__global__ void claculateSumOfOutDegree(float * sumOfOutDegree, const float* Graph)
{
    int i = blockDim.x * blockIdx.x + threadIdx.x;

    if (i < numberOfVertex )
    {
        sumOfOutDegree[i]  = 0;
        for (int j = 0; j < numberOfVertex ; ++j)
        {
            sumOfOutDegree[i] += *(Graph +i*numberOfVertex  +j);
        }
    }

}

//END condition: when the PR value stable

bool END(float a[], float b[])
{
    float sum = 0;
    for (int i = 0; i < numberOfVertex ; ++i)
    {
        sum += abs(a[i] - b[i]);
    }
    printf("The Deviation Between Two Iteration: %f \n",sum);
    if (sum < END_WEIGHT)
    {
        return true;
    }
    
    return false;
}

//CPU Routine of PageRank Calculation

int PageRank(float *Graph, float PR[])
{
    //Display the Graph:
/*
    for (int i = 0; i < vertex; ++i)
    {
        for (int j = 0; j < vertex; ++j)
        {
            printf("%f\t", *(Graph +i*vertex +j));
        }
        printf("\n");
    }
*/
    //Calculate the sum of out-degree of every vertex
    //eg. the sum of every line
    clock_t begin, end;
    float PR_Temp[numberOfVertex ];
    
    begin = clock();
    
    for (int m = 0; m < Max_Iteration_Number; ++m)
    {
        CPUiter++;
        float sumOfOutDegree[numberOfVertex ];
        for (int i = 0; i < numberOfVertex ; ++i)
        {
            sumOfOutDegree[i] = 0.0;
        }

        //Calculate the sum of degree of each vertex
        for (int i = 0; i < numberOfVertex ; ++i)
        {
            float sum = 0;
            for (int j = 0; j < numberOfVertex ; ++j)
            {
                sum += *(Graph +i*numberOfVertex  +j);
            }
            sumOfOutDegree[i] = sum;
        }

        //Calculate the PR value of every vertex.
        for (int i = 0; i < numberOfVertex ; ++i)
        {
            float sum = 0;
            int k = 0;
            for (int j = i; j < numberOfVertex *numberOfVertex  ; j += numberOfVertex )
            {
                if (*(Graph + j) == 1)
                {
                    if(sumOfOutDegree[k] != 0)
                        sum += PR[k] / sumOfOutDegree[k];
                }
                k++;
                //printf("%f\n", sum);
            }
            PR_Temp[i] = Alpha  + (1 - Alpha)*(sum);
        }

        if (END(PR_Temp, PR))
        {
            break;
        }
        else{
            for (int i = 0; i < numberOfVertex ; ++i)
            {
                PR[i] = PR_Temp[i];
            }
        }

    }
    end = clock();
    return end - begin;
}



int main()
{
    //char ch;
    int source = 0;
    int dest = 0;
    cudaError_t err = cudaSuccess;
    
    size_t size = numberOfVertex  * sizeof(float);

    float sumOfOutDegree[numberOfVertex ];

    //Allocate the device memory
    float *d_Sum_Of_Degree = NULL;
    cudaMalloc((void **)&d_Sum_Of_Degree, size);
    if(d_Sum_Of_Degree == NULL)
    {
        cout << "Failed"<<endl;
    }

    float *d_PR = NULL;
    cudaMalloc((void**)&d_PR,size);
    if (d_PR == NULL)
    {
        cout << "Failed" << endl;
    }

    float *d_Graph = NULL;
    
    cudaMalloc((void **)&d_Graph, size * numberOfVertex );
    if (d_Graph == NULL)
    {
        cout <<"Failed" << endl;
    }

    //thread number

    int threadsPerBlock = numberOfVertex ;
    int blocksPerGrid =(numberOfVertex  + threadsPerBlock - 1) / threadsPerBlock;

    //Read Graph file.

    fstream fp("Graph.txt",ios::in);
    if(!fp.is_open())
    {
        printf("Failed to open file.\n");
    }

    //output file
    fstream prFile("PageRankValue.txt", ios::out);
    if (!prFile.is_open())
    {
        printf("Failed to open file PRV\n");
    }

    //host memory allocate

    float Graph[numberOfVertex ][numberOfVertex ];
    float PR[numberOfVertex ];
    float PR_Temp[numberOfVertex];


    //init
    for (int i = 0; i < numberOfVertex ; ++i)
    {
        PR[i] = InitPageRankValue;
        PR_Temp[i] = InitPageRankValue;
    }

    for (int i = 0; i < numberOfVertex ; ++i)
    {
        for (int j = 0; j < numberOfVertex ; ++j)
        {
            Graph[i][j] = 0;
        }
    }
	int edge = 0;
    //read from Graph.txt
    while (!fp.eof()){

        fp >> source >> dest;
        std::cout << source << ' '<< dest << std::endl;

        Graph[source][dest] = 1;
		edge++;
    }
    printf("Graph build Done!\n");
    printf("----------------------------------------------------------\n");


    //copy
    err = cudaMemcpy(d_Graph, *Graph, numberOfVertex *size, cudaMemcpyHostToDevice);

    if (err != cudaSuccess)
    {
        fprintf(stderr, "Failed to copy vector B from host to device (error code %s)!\n", cudaGetErrorString(err));
        exit(EXIT_FAILURE);
    }

    //invoke PageRank.
	//CPU Routine
 
    int CPUTime  = 0;
    CPUTime = PageRank(*Graph, PR);


	for (int i = 0; i < numberOfVertex; ++i)
    {
        PR[i] = InitPageRankValue;
    }
    printf("--------------------------------------------------------\n");
    clock_t begin, end;
    int iter = 0;
    float SumOfGPUTime = 0;
    begin = clock();
    for (int m = 0; m < Max_Iteration_Number; ++m)
    {
        /*
        for (int i = 0; i < numberOfVertex ; ++i)
        {
            for (int j = 0; j < numberOfVertex ; ++j)
            {
                printf("%f\t", Graph[i][j]);
            }
            printf("\n");
        }
        */

        iter ++;

        //CUDA event timing
        
        cudaEvent_t start, stop;
        cudaEventCreate(&start);
        cudaEventCreate(&stop);
        cudaEventRecord(start, 0);

        
        //calculate sum of out degree

        claculateSumOfOutDegree<<<blocksPerGrid, threadsPerBlock>>>(d_Sum_Of_Degree, d_Graph);
        err = cudaGetLastError();

        if (err != cudaSuccess)
        {
            fprintf(stderr, "Failed to launch vectorAdd kernel (error code %s)!\n", cudaGetErrorString(err));
            exit(EXIT_FAILURE);
        }
        /*
        cudaMemcpy(sumOfOutDegree, d_Sum_Of_Degree, size, cudaMemcpyDeviceToHost);
        for (int i = 0; i < numberOfVertex ; ++i)
        {
            cout << sumOfOutDegree[i] <<'\t';
        }
        */

        //copy
        err = cudaMemcpy(d_PR, PR, size, cudaMemcpyHostToDevice);

        if (err != cudaSuccess)
        {
            fprintf(stderr, "Failed to copy vector B from host to device (error code %s)!\n", cudaGetErrorString(err));
            exit(EXIT_FAILURE);
        }

        PRAdd<<<blocksPerGrid, threadsPerBlock>>>(d_PR, d_Graph, d_Sum_Of_Degree);

        err = cudaGetLastError();

        if (err != cudaSuccess)
        {
            fprintf(stderr, "Failed to launch vectorAdd kernel (error code %s)!\n", cudaGetErrorString(err));
            exit(EXIT_FAILURE);
        }
        cudaEventRecord(stop, 0);
        cudaEventSynchronize(stop);
        float elapsedTime;
        cudaEventElapsedTime(&elapsedTime, start, stop);

        SumOfGPUTime += elapsedTime;

        cudaMemcpy(PR_Temp, d_PR, size, cudaMemcpyDeviceToHost);

        if (END(PR_Temp, PR))
        {
            break;
        }
        else{
            for (int i = 0; i < numberOfVertex ; ++i)
            {
                PR[i] = PR_Temp[i];
            }
        }
        

    }
    end = clock();
    


    //printf("%d\n", vertex);
    
    for (int i = 0; i < numberOfVertex; ++i)
    {
        prFile << i << "  :  " << PR[i] << endl;
    }
    

    


    printf("Matrix: %d * %d\n", numberOfVertex, numberOfVertex);
	printf("Edge: %d .\n", edge);
    printf("Number of thread : %d.\n", numberOfVertex);
    printf("Number of block : %d\n", blocksPerGrid);
    printf("--------------------------------------------------------\n");
    printf("CPU Routine of calculating %d iterations of PageRank value cost us:%d ms.\n",CPUiter,  CPUTime);
    printf("--------------------------------------------------------\n");

    printf("Calculation on GPU : %f ms.\n", SumOfGPUTime);
    printf("GPU Routine of calculating %d iterations of PageRank value cost us:%d ms.\n",iter,  end - begin);

    printf("--------------------------------\n");
    printf("Ratio of acceleration: %f \n", (float)SumOfGPUTime/CPUTime);
    printf("--------------------------------\n");

    fp.close();
    prFile.close();
    getchar();
}
