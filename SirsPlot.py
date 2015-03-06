import sys
import numpy as np
import pylab as plt
import matplotlib.cm as cm
import matplotlib.mlab as mlab



#plot contour
if(sys.argv[1] == 'true'):
	
	averages = np.loadtxt('sirs.txt',unpack=True)
	variance = np.loadtxt('sirs2.txt',unpack=True)
	
	
	
	plt.subplot(121)
	im1 = plt.imshow(averages, interpolation='bilinear', cmap=cm.gnuplot,origin='lower', 
	extent=[0,1,0,1],vmax=np.max(averages), vmin=np.min(averages))
	plt.colorbar(im1)
	plt.title('Average I')
	plt.xlabel('p1')
	plt.ylabel('p3')
	#plt.tight_layout()
	
	
	plt.subplot(122)
	im2 = plt.imshow(variance, interpolation='bilinear', cmap=cm.gnuplot,origin='lower', 
	extent=[0,1,0,1],vmax=np.max(variance), vmin=np.min(variance))
	plt.colorbar(im2)
	plt.title('Variance of I')
	plt.xlabel('p1')
	plt.ylabel('p3')
	
	#plt.tight_layout()
	plt.subplots_adjust(wspace=0.3)
	
#plot time 
else:
	x,y = np.loadtxt('sirs.txt',usecols=(0,1),unpack=True)

	plt.title('SIRS Model')
	plt.plot(x,y)

	plt.xlabel('sweeps')
	plt.ylabel('% Infected')
	
	
plt.savefig('sirsfig.png')
#plt.show()
