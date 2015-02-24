import numpy as np
import pylab as plt


x,y = np.loadtxt('sirs.txt',usecols=(0,1),unpack=True)

plt.title('SIRS Model')
plt.plot(x,y)

plt.xlabel('sweeps')
plt.ylabel('% Infected')
plt.savefig('sirsfig.png')
#plt.show()