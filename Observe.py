import sys
import numpy as np
import pylab as plt

'''
requires command line arguments: python Observe.py filename.txt plotTitle xLabel yLabel dataisNamed? errors?

dataisNamed in boolean type 'true' or 'false' identifies that each column in filename.txt has a title
in the first row, titles will be put in plot legend

errors identifies that data should be plotted with error bars in the y dimension
'''


data = np.loadtxt(sys.argv[1],unpack=True, skiprows=1 if sys.argv[5] == 'true' else 0)

'''find labels for legend'''
file = open(sys.argv[1],'r')
line = file.readline()
labels = line.split()
file.close()

if(sys.argv[6] == 'true'):
    for i in range(0,len(data-1)/2):
        plt.errorbar(data[0],data[2*i+1],data[2*i +2],label=labels[2*i+1])
else:
    for i in range(1,len(data)):
        plt.plot(data[0],data[i],label=labels[i])
    

plt.title(sys.argv[2])
plt.xlabel(sys.argv[3])
plt.ylabel(sys.argv[4])

if sys.argv[5] == 'true':
    plt.legend()

plt.show()
