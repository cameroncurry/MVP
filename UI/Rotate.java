// Particle-Dynamics Visualizer by Greg Tumolo (greg.tumolo@ed.ac.uk)

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

class Rotate {	// -------------------------------------------------------------
	static int S = 780;	// (Display) Size [pixels]
	static int DP = 33;	// Draw Period [ms]
	static int UP = 1000;	// Update Period [ms]
	static int BG_RGB = Color.BLACK.getRGB();	// BackGround RGB
	static double B_MIN = .1;	// MINimum (Point) Brightness in [0, 1]

	static void init(Point[] Ps) {
		for (int i = 0; i < Ps.length; i++) Ps[i] = new Point(1.15470053837925*(Math.random() - .5), 1.15470053837925*(Math.random() - .5), 1.15470053837925*(Math.random() - .5), 255, 0, 0);
	}

	static void update(Point[] Ps) {
		Ps[(int)(Math.random()*Ps.length)].g = 255;
	}
	//----------------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		/*
		if (args.length != 5) throw new Exception("Arguments: #Points #Pixels(Radius_minimum) #Pixels(Radius_maximum) Perspective? Shading?");
		final Point[] Ps = new Point[Integer.parseInt(args[0])];
		final int r_min = Integer.parseInt(args[1]);
		final int r_range = Integer.parseInt(args[2]) - r_min;
		final boolean perspective = Boolean.parseBoolean(args[3]);
		final boolean shading = Boolean.parseBoolean(args[4]);
		 */
		final Point[] Ps = new Point[100000];
		final int r_min = 1;
		final int r_range = 2;
		final boolean perspective = true;
		final boolean shading = true;
		
		init(Ps);

		final Frame f = new Frame();
		f.setIgnoreRepaint(true);
		f.setVisible(true);
		top = f.getInsets().top;
		f.setSize(S, S + top);
		int hS = S/2;	// half Size [pixels]
		final RotationAdapter ra = new RotationAdapter(hS, top + hS);
		f.addMouseListener(ra);
		f.addMouseMotionListener(ra);
		f.addWindowListener(new WindowAdapter() {public void windowClosing(WindowEvent we) {System.exit(0);}});

		new Timer().scheduleAtFixedRate(new TimerTask() {public void run() {draw(ra, Ps, r_min, r_range, perspective, shading, f);}}, 0, DP);
		new Timer().scheduleAtFixedRate(new TimerTask() {public void run() {update(Ps);}}, 0, UP);
	}
	static int r_range;	// radius range [pixels]
	static int top;	// ...of display area (or bottom of menu bar) w/r/t origin of frame
	static double[][] Z = new double[S][S];	// Z-buffer
	static BufferedImage bi = new BufferedImage(S, S, BufferedImage.TYPE_INT_RGB);
	static float[] hsb = new float[3];	// {hue in [0, 1], saturation in [0, 1], brightness in [0, 1]}
	static int Sm1 = S - 1;	// [pixels]
	static void draw(RotationAdapter ra, Point[] Ps, int r_min, int r_range, boolean perspective, boolean shading, Frame f) {
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

		for (Point P : Ps) {
			double x = R_0_0*P.x + R_0_1*P.y + R_0_2*P.z, y = R_1_0*P.x + R_1_1*P.y + R_1_2*P.z, z = R_2_0*P.x + R_2_1*P.y + R_2_2*P.z;	// rotated coordinates

			int rgb;	// (packed) red-, green-, and blue-components of color
			if (shading) {
				Color.RGBtoHSB(P.r, P.g, P.b, hsb);
				rgb = Color.HSBtoRGB(hsb[0], hsb[1], (float)Math.max(B_MIN, .5*(z + 1.)*hsb[2]));
			} else rgb = P.r << 16 | P.g << 8 | P.b;

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
		f.getGraphics().drawImage(bi, 0, top, f.getWidth(), f.getHeight() - top, null);
	}
}

class RotationAdapter extends MouseAdapter {	// -----------------------------
	static double RPP = Math.PI/180.;	// Radians Per Pixel (dragged) [radians]
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

class Point {
	double x, y, z;	// coordinates
	int r, g, b;	// red-, green-, and blue-components of color in [0, 255]

	Point(double x, double y, double z, int r, int g, int b) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	Point() {
	}
}
