import numpy as np
import math
import matplotlib.cm as cm
import matplotlib.mlab as mlab
#import matplotlib.pyplot as plt
import pylab as plt

'''
delta = 0.3
x = y = np.arange(-3.0, 3.0, delta)
X, Y = np.meshgrid(x, y)


Z1 = mlab.bivariate_normal(X, Y, 1.0, 1.0, 0.0, 0.0)
Z2 = mlab.bivariate_normal(X, Y, 1.5, 0.5, 1, 1)
Z = Z2-Z1  # difference of Gaussians

print len(Z)
print len(Z[0])
'''

Z = [[1,2,3], 
	 [4,5,6], 
	 [7,8,9]] 

fig, ax = plt.subplots()

im = ax.imshow(Z, interpolation='bilinear', cmap=cm.RdBu,
                origin='lower', extent=[0,1,0,1],
                vmax=10, vmin=0)

'''
im = ax.imshow(Z, interpolation='bilinear', cmap=cm.RdBu,
                origin='lower', extent=[-3,3,-3,3],
                vmax=abs(Z).max(), vmin=-abs(Z).max())
'''

cb = fig.colorbar(im, ax=ax)
plt.show()

