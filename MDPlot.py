import pylab as plt
import numpy as np
import sys

data = np.loadtxt('MD.txt',unpack=True)


if(len(data) == 2):
    plt.plot(data[0],label='Kinetic')
    plt.plot(data[1],label='Potential')
    plt.plot(data[0]+data[1],label='Total')
    plt.ylabel('Energy')
    plt.xlabel('time')
    plt.legend()
elif(sys.argv[1] == 'true'):
    plt.plot(data)
    plt.ylabel('Temperature')
else:
    #print 'mean square'
    plt.plot(data)
    plt.ylabel('$<r^2>$')


plt.title('Molecular Dynamics')
plt.xlabel('time')
plt.savefig('MDfig.png')
#plt.show()
