import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


public class MD3DView extends JPanel {

	private static final long serialVersionUID = 1L;
	
	static int S = 780;	// (Display) Size [pixels]
	static int DP = 33;	// Draw Period [ms]
	static int UP = 1000;	// Update Period [ms]
	static int BG_RGB = Color.black.getRGB();	// BackGround RGB
	static double B_MIN = .1;	// MINimum (Point) Brightness in [0, 1]

	
	RotationAdapter ra;
	double dimension;
	
	public MD3DView() {
		Ps = new Particle3D[20];
		dimension = 1.;
		init(Ps);
		ra = new RotationAdapter(0,0);
		this.addMouseListener(ra);
		this.addMouseMotionListener(ra);
		
	}
	
	public void set(Particle3D[] particles, double dimension){
		this.Ps = particles;
		this.dimension = dimension;
	}
	
	static void init(Particle3D[] Ps) {
		
		int N = Ps.length;
		double sigma = 1;
		double density = 1;
		
		double volume = N*Math.PI / (6*sigma*sigma*sigma*density);
		double sideLength = Math.cbrt(volume);
		double nPerD = Math.cbrt(N);
		
		double delta = sideLength / nPerD;
		
		int index = 0;
		for(int i=0;i<nPerD;i++){
			for(int j=0;j<nPerD;j++){
				for(int k=0;k<nPerD;k++){
					if(index < N){
						Vector3D pos = new Vector3D(i*delta,j*delta,k*delta);
						Ps[index] = new Particle3D(1,pos,new Vector3D());
						index++;
					}
				}
			}
			
		}
		
		
		
	}
	
	Particle3D[] Ps;
	final int r_min = 1;
	final int r_range = 10;
	final boolean perspective = true;
	final boolean shading = true;
	
	BufferedImage b;
	
	
	public void paintComponent(Graphics g){
		if(Ps != null){
			Dimension d = getSize();
			b = draw(ra, Ps, r_min, r_range, perspective, shading,Color.pink);
			BufferedImage img = resize(b,d.width,d.height);
			g.drawImage(img, 0, 0, null);
		}
	}
	
	private BufferedImage resize(BufferedImage img, int newW, int newH){
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();

	    return dimg;
	}
	
	//static int r_range;	// radius range [pixels]
	static int top;	// ...of display area (or bottom of menu bar) w/r/t origin of frame
	static double[][] Z = new double[S][S];	// Z-buffer
	static BufferedImage bi = new BufferedImage(S, S, BufferedImage.TYPE_INT_RGB);
	static float[] hsb = new float[3];	// {hue in [0, 1], saturation in [0, 1], brightness in [0, 1]}
	static int Sm1 = S - 1;	// [pixels]
	
	private BufferedImage draw(RotationAdapter ra, Particle3D[] Ps, int r_min, int r_range, boolean perspective, boolean shading,Color c) {
		for (int i = 0; i < S; i++) {
			double[] Z_i = Z[i];
			for (int j = 0; j < S; j++) {
				bi.setRGB(i, j, BG_RGB);
				Z_i[j] = Double.NEGATIVE_INFINITY;
			}
		}

		double[] q = ra.q;	// quaternion
		double q_0, q_1, q_2, q_3;	// q components
		synchronized(ra) {
			q_0 = q[0];
			q_1 = q[1];
			q_2 = q[2];
			q_3 = q[3];
		}
		double	R_0_0 = 1. - 2.*(q_1*q_1 + q_2*q_2),	R_0_1 = 2.*(q_0*q_1 - q_2*q_3),		R_0_2 = 2.*(q_0*q_2 + q_1*q_3),
			R_1_0 = 2.*(q_0*q_1 + q_2*q_3),		R_1_1 = 1. - 2.*(q_0*q_0 + q_2*q_2),	R_1_2 = 2.*(q_1*q_2 - q_0*q_3),
			R_2_0 = 2.*(q_0*q_2 - q_1*q_3),		R_2_1 = 2.*(q_0*q_3 + q_1*q_2),		R_2_2 = 1. - 2.*(q_0*q_0 + q_1*q_1);	// R(otation matrix) components

		for (Particle3D P : Ps) {
			Vector3D pos = P.position();
			double p_x = 1.15470053837925*((pos.x()/dimension) - .5);
			double p_y = 1.15470053837925*((pos.y()/dimension) - .5);
			double p_z = 1.15470053837925*((pos.z()/dimension) - .5);
			
			double x = R_0_0*p_x + R_0_1*p_y + R_0_2*p_z, y = R_1_0*p_x + R_1_1*p_y + R_1_2*p_z, z = R_2_0*p_x + R_2_1*p_y + R_2_2*p_z;	// rotated coordinates

			int rgb;	// (packed) red-, green-, and blue-components of color
			//Color pink = Color.pink;
			if (shading) {
				Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
				rgb = Color.HSBtoRGB(hsb[0], hsb[1], (float)Math.max(B_MIN, .5*(z + 1.)*hsb[2]));
			} else rgb = c.getRed() << 16 | c.getGreen() << 8 | c.getBlue();

			int i_0, j_0;	// indices corresponding to x, y, and, conditionally, z
			if (perspective) {
				double s = 1./(2.21034343104507 - z);	// scale
				i_0 = (int)Math.round(Sm1*(s*x + .5));
				j_0 = Sm1 - (int)Math.round(Sm1*(s*y + .5));
			} else {
				i_0 = (int)Math.round(Sm1*(.5*x + .5));
				j_0 = Sm1 - (int)Math.round(Sm1*(.5*y + .5));
			}

			int r = (int)Math.round(r_min + r_range*(z + 1.));	// apparent radius [pixels]

			int i_max = Math.min(i_0 + r, Sm1);
			for (int i = Math.max(0, i_0 - r); i <= i_max; i++) {
				double[] Z_i = Z[i];
				double delta_i = i - i_0;
				int d = (int)Math.sqrt(r*r - delta_i*delta_i);	// distance to lower/upper (-)/(+) limb of circle from its horizontal center line [pixels]
				int j_max = Math.min(j_0 + d, Sm1);
				for (int j = Math.max(0, j_0 - d); j <= j_max; j++) if (Z_i[j] < z) {
					bi.setRGB(i, j, rgb);
					Z_i[j] = z;
				}
			}
		}
		//f.getGraphics().drawImage(bi, 0, top, f.getWidth(), f.getHeight() - top, null);
		return bi;
	}
	
}

class RotationAdapter extends MouseAdapter {	// -----------------------------
	static double RPP = 0.5*Math.PI/180.;	// Radians Per Pixel (dragged) [radians]
	//----------------------------------------------------------------------

	double[] q = new double[] {0., 0., 0., 1.};	// quaternion
	int O_x, O_y;	// Origin coordinates (in system of listener(s) of this)
	int x_c, y_c;	// current mouse coordinates (in system of listener(s) of this)

	RotationAdapter(int O_x, int O_y) {
		this.O_x = O_x;
		this.O_y = O_y;
	}

	public void mousePressed(MouseEvent me) {
		x_c = me.getX();
		y_c = me.getY();
	}

	public void mouseDragged(MouseEvent me) {
		int x_p = x_c, y_p = y_c;	// previous mouse coordinates (in system of listener(s) of this)
		
		x_c = me.getX();
		y_c = me.getY();

		if (x_c - x_p == 0 && y_c - y_p == 0) return;

		double a_x, a_y, a_z;	// axis components
		double alpha;	// angle (= theta/2) [radians]
		if (me.isShiftDown()) {
			a_x = 0.;
			a_y = 0.;
			a_z = 1.;
			alpha = .5*(Math.atan2(O_y - y_c, x_c - O_x) - Math.atan2(O_y - y_p, x_p - O_x));
		} else {
			a_x = y_c - y_p;
			a_y = x_c - x_p;
			a_z = 0.;
			double l = Math.sqrt(a_x*a_x + a_y*a_y);	// length of axis [pixels]
			a_x /= l;
			a_y /= l;
			alpha = .5*l*RPP;
		}
		double sin = Math.sin(alpha), cos = Math.cos(alpha);	// (co)sine of alpha
		double q_0 = q[0], q_1 = q[1], q_2 = q[2], q_3 = q[3];	// q components
		synchronized(this) {
			q[0] = cos*q_0 + sin*(a_x*q_3 + a_y*q_2 - a_z*q_1);
			q[1] = cos*q_1 - sin*(a_x*q_2 - a_y*q_3 - a_z*q_0);
			q[2] = cos*q_2 + sin*(a_x*q_1 - a_y*q_0 + a_z*q_3);
			q[3] = cos*q_3 - sin*(a_x*q_0 + a_y*q_1 + a_z*q_2);
		}
	}
}


