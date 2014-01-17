#Jason Lyu 2013 .11 .30

import sys
import random

def generate_graph(vertex_number):
	"""Generate a Graph.
	   which will use in CUDA PageRank test.
	   the generate file of Graph will be like this :

	   0 1
	   0 2
	   0 4
	   1 0
	   1 2
	   1 3
	   2 0
	   ...
	   ..
	   .
	   9999 2
	   ...
	   ..
	   .

	   the first number of each line represent the vertex of source, and the second number represent the 
	   source's out-degree_vertex.

	"""

	
	with open("Graph.txt", mode = 'w', encoding = "utf-8") as graph_file:
		i = 0
		while(i < vertex_number):
			number_of_out_degree = random.randint(0, vertex_number/2)
			#print(number_of_out_degree)
			list_of_out_degree = []
			start = 0

			while(start < number_of_out_degree):
				out_degree = random.randint(0, vertex_number)
				if out_degree == i or out_degree in list_of_out_degree:
					continue
				else:
					start += 1
					list_of_out_degree.append(out_degree)

			list_of_out_degree.sort()
			
			for out_degree in list_of_out_degree:
				graph_file.write(str(i) + ' ' + str(out_degree) + '\n')
				print(str(i) + ' ' + str(out_degree))
						
			i += 1


if __name__ == '__main__':

	vertex_number = sys.argv[1]
	#print(int(vertex_number))
	generate_graph(int(vertex_number))
