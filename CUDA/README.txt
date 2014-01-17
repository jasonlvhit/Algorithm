Data文件夹中是程序的运行数据和结果。
Graph300：含300个顶点的有向图文件。依此类推。
PageRankValue是同目录中Graph.txt的运行结果。

generate_graph.py为生成数据脚本，运行方式为python generate_graph 500.
这样会生成一个含有500个顶点的有向图文件Graph.txt

kernel.cu是CUDA源程序

PageRank.cpp是C++版本的PageRank程序。
CUDA程序和PageRank程序会打开同目录下的Graph.txt文件，并在程序开始指定了vertex总数，PageRank初始值等信息。

最后是一个答辩PPT。