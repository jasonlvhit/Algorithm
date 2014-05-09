# -*- coding: cp936 -*-
#遗传算法求函数极值
#49位01,首位0为正，1为负
#-1 ~ 2

import random
import sys
import math

population = []
fitness = []
GrayMap = {0: "0000",
		   1: "0001",
		   2: "0011",
		   3: "0010",
		   4: "0110",
		   5: "0111",
		   6: "0101",
		   7: "0100",
		   8: "1100",
		   9: "1101"}

mutation_ratio = 0.0005
crossover_ratio = 1.0
alpha = 1.0
init_elite_ratio = 0
iter_number = 1000

def generate(init_amount):

	"""
		生成初始种群
	"""
	global population
	global GrayMap
	i = 0
	while i < init_amount:
		
		j = 0
		individual = ""
		while j < 11:
			temp = random.randint(0,9)
			individual = individual + GrayMap[temp]
			j = j + 1

		while 1:
			symbolbit = random.randint(0,1)
			intbit = random.randint(0,1)
			if symbolbit == 1 and intbit == 1:
				continue
			else:	
				individual = str(symbolbit) + GrayMap[intbit] + individual
				break

		population.append(individual)

		i = i + 1
			
def mutation():
	"""
		mutation
	"""
	global mutation_ratio
	global population

	mutation_gene_amount = 49 * len(population) * mutation_ratio
	while mutation_gene_amount > 0:
		mutation_bit = random.randint(1, 11)
		mutation_value = random.randint(0,9)
		mutation_individual = random.randint(0, len(population) - 1)
		population[mutation_individual] = population[mutation_individual][0: 5 + 4 * (mutation_bit - 1)]+ GrayMap[mutation_value] + population[mutation_individual][5 + 4 * (mutation_bit)  : 49]
		mutation_gene_amount = mutation_gene_amount - 1

def crossover():
	"""
	crossover
	"""
	global crossover_ratio
	global population

	crossover_amount = len(population) * crossover_ratio
	crossover_individual = random.sample(population, int(crossover_amount))

	for i in range(0, len(crossover_individual) - 2 ,2):
		makeCrossover(crossover_individual[i], crossover_individual[i+1])

	crossover_ratio = crossover_ratio * 0.7

def makeCrossover(a, b):
	crossover_start = random.randint(0, 7)
	temp = a[ 22 + crossover_start * 4 : 49]
	a = a[0:22] + b[ 22 + crossover_start * 4 : 49]
	b = b[0:22] + temp

def roulette(fitness):
	
	global population

	fitnesstemp = fitness
	accu = sum(fitnesstemp)
	print(len(population))
	#print(accu)	
	freq = []
	for i in fitness:
		freq.append(i / accu)
		
	temp =  elite()
	count = 0
	for i in freq:
		if i > 10 * alpha * 0.0000001:
			temp.append(population[count])
		count = count + 1

	population = temp


def elite():
	
	global init_elite_ratio
	global population
	global fitness
	global iter_number

	population_fitness_dict = dict(zip(population,fitness))

	d = sorted(population_fitness_dict.iteritems(), key=lambda d:d[1], reverse = False)

	for i,j in d:
		population_fitness_dict[i] = j

	i = 0
	elite_list = []
	while i < len(population_fitness_dict)*init_elite_ratio:
		elite_list.append(population_fitness_dict.keys()[i])
		i = i + 1
	
	init_elite_ratio = 1 - 1.0/1000 * iter_number
	iter_number = iter_number - 0.2
	return elite_list


def find_keys(d,v): 
    return [k1 for (k1,v1) in d.items() if v1 == v][0]
	
def GenetoNumber(string):
	global GrayMap
	number = ""
	number = number + str(find_keys(GrayMap, string[1:5])) + '.'
	for i in range(0,11):
		number = number + str(find_keys(GrayMap, string[5 + i * 4: 5 + (i + 1) * 4]))

	if string[0] == '1':
		return -float(number)
	else:
		return float(number)


def calfitness(i):
	global alpha
	numerical = GenetoNumber(i)
	k = abs(math.sin(10 * math.pi * numerical) + 10 * math.pi * numerical * math.cos(10 * math.pi * numerical))
	return alpha * math.exp(-k)


def GA(init_amount, iter):
	global alpha
	global fitness
	global population
	
	generate(init_amount)
	fitness = map(calfitness, population)
	while iter:
		roulette(fitness)
		if len(population) < 400:
			classify(population)
			while 1:
				1
		crossover()
		mutation()

		fitness = map(calfitness, population)
		alpha = alpha * 1.03
		iter = iter - 1


def classify(population):

	group = []
	group_amount = []
	for i in range(0,40):
		group.append(0)
		group_amount.append(0)

	i = 0
	while i < len(population):
		double_index = (int)(GenetoNumber(population[i]) / 0.05)
		if double_index > 0:
			group[double_index/2] = group[double_index/2] + GenetoNumber(population[i])
			group_amount[double_index/2] = group_amount[double_index/2] + 1
		else:
			group[20 - (double_index/2 - 1 )] = group[20 - (double_index/2 - 1)] + GenetoNumber(population[i])
			group_amount[20 - (double_index/2 - 1 )] = group_amount[20 - (double_index/2 - 1 )] + 1

		i = i + 1

	i = 0
	while i < 30:
		if group_amount[i] > 0:
			group[i] = group[i] /group_amount[i]
			print(group[i])	
		i = i + 1

	


if __name__ == '__main__':
	
	GA(40000, 1000)
	classify(population)


		

	
