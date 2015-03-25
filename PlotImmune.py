import pylab as plt



x = [0,0.1,0.2,0.3,0.35,0.4 ,0.45,0.5 ,0.55,0.6 ,0.65,0.7 ,0.75 ,0.8 ,0.85,0.9  ,0.95,1.0]
y = [0,0  ,0  ,0  ,0.1 ,0.17,0.22,0.27,0.31,0.34,0.37,0.39,0.395,0.4 ,0.44,0.455,0.46,0.47]
e = [0,0  ,0  ,0  ,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.015,0.02,0.03 ,0.03,0.03]


plt.errorbar(x,y,e)
plt.text(0.2,0.4,"Absorbing State")
plt.text(0.65,0.15,"Equilibrium State")
plt.xlabel("p1")
plt.ylabel("% Immune")
plt.title("% Immune Required for Absorbing State as a Function of p1")
plt.show()