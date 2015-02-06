import sys 
import pylab as pl
import numpy as np

data = np.loadtxt('data.txt',unpack=True)




if(len(data) == 4):
	pl.subplot(211)
	pl.plot(data[0],data[1])
	if(sys.argv[1] == 'true'):
		pl.ylabel('| M |')
	else:
		pl.ylabel('Energy')
	
	pl.subplot(212)
	pl.errorbar(data[0],data[2],data[3])
	if(sys.argv[1] == 'true'):
		pl.ylabel('Susceptibility')
	else:
		pl.ylabel('Heat Capacity')
	pl.xlabel('Temperature')
	
else:
	pl.subplot(221)
	pl.plot(data[0],data[1])
	pl.ylabel('| M |')
	
	pl.subplot(222)
	pl.plot(data[0],data[4])
	pl.ylabel('Energy')
	
	pl.subplot(223)
	pl.errorbar(data[0],data[2],data[3])
	pl.ylabel('Susceptibility')
	pl.xlabel('Temperature')
	
	pl.subplot(224)
	pl.errorbar(data[0],data[5],data[6])
	pl.ylabel('Heat Capacity')
	pl.xlabel('Temperature')
	
pl.suptitle('Results')
pl.savefig('img.png')


