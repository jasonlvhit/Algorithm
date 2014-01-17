#include "stdafx.h"
#include <cstdio>
#include <fstream>
#include <iostream>
#include <time.h>
#include <windows.h>

#define numberOfVertex  500
#define Max_Iteration_Number 10000
#define Alpha 0.85
#define END_WEIGHT 1e-7
#define InitPageRankValue 6

using namespace std;

//END condition: when the PR value stable
bool END(float a[], float b[])
{
    float sum = 0;
    for (int i = 0; i < numberOfVertex ; ++i)
    {
        sum += abs(a[i] - b[i]);
    }
    cout << sum <<endl;
    if (sum < END_WEIGHT)
    {
        return true;
    }

    return false;
}

void PageRank(float *Graph, float PR[])
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
    int iter = 0;  //迭代次数
    for (int m = 0; m < Max_Iteration_Number; ++m)
    {
        iter++;
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
            PR_Temp[i] = (1 - Alpha)  + Alpha*(sum);
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
    printf("Calculate %d iteration of PageRank value cost us:%d ms.\n",iter, end - begin);

}



int main()
{
    char ch;
    int source = 0;
    int dist = 0;
    int vertex = 500;

    fstream fp("Graph.txt",ios::in);
    if(!fp.is_open())
    {
        printf("Failed to open file.\n");
    }

    fstream prFile("PageRankValue.txt", ios::out);
    if (!prFile.is_open())
    {
        printf("Failed to open file PRV\n");
    }

    //printf("%d\n", vertex);

    /*  这里我们得到图的总结点数
     *  我们为这个图建立一个邻接矩阵：
     *  Graph[vertex][vertex]
     *  并将其中所有的值赋为0，代表没有指向。当存在一个链接：从source到dist，则将Graph[soures][dist]赋值为 1.
     *
     *  同时建立PR数组，代表每一个节点的PageRank值。
     *  并将其初始化为 1.
     */

    float Graph[numberOfVertex][numberOfVertex];
    float PR[numberOfVertex];

    for (int i = 0; i < numberOfVertex; ++i)
    {
        PR[i] = InitPageRankValue;
    }

    for (int i = 0; i < numberOfVertex; ++i)
    {
        for (int j = 0; j < numberOfVertex; ++j)
        {
            Graph[i][j] = 0;
        }
    }

    while (!fp.eof()){

        fp >> source >> dist;
        std::cout << source << ' '<< dist << std::endl;

        Graph[source][dist] = 1;
    }
    printf("Graph build complete.\n");

    //invoke PageRank.
    PageRank(*Graph, PR);

    //output to file PageRankValue.txt
    for (int i = 0; i < numberOfVertex; ++i)
    {
        prFile << i << "  :  " << PR[i] << endl;
    }
    //printf("%f\n", Graph[0][0]);

    prFile.close();
    fp.close();
    getchar();
}
