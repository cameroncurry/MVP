
public class Vector3D {

	private double x;
	private double y;
	private double z;
	
	public Vector3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	//vector of 0s
	public Vector3D(){
		this(0,0,0);
	}
	
	//deep copy constructor
	public Vector3D(Vector3D v){
		this(v.x(),v.y(),v.z());
	}
	
	public double x(){return x;}
	public double y(){return y;}
	public double z(){return z;}
	
	public void setX(double x){this.x = x;}
	public void setY(double y){this.y = y;}
	public void setZ(double z){this.z = z;}
	
	@Override
	public String toString(){
		return String.format("%.3f %.3f %.3f", x,y,z);
	}
	
	public double mag(){
		return Math.sqrt(x*x + y*y + z*z);
	}
	
	//add vector without returning new one
	public void add(Vector3D b){
		this.x += b.x();
		this.y += b.y();
		this.z += b.z();
	}
	
	public void subtract(Vector3D b){
		this.x -= b.x();
		this.y -= b.y();
		this.z -= b.z();
	}
	
	//multiply vector by a constant
	public Vector3D multiply(double c){
		return new Vector3D(this.x*c,
							this.y*c,
							this.z*c);
	}
	
	public static Vector3D add(Vector3D a, Vector3D b){
		return new Vector3D(a.x()+b.x(), 
							a.y()+b.y(), 
							a.z()+b.z());
	}
	
	// vector a - b
	public static Vector3D subtract(Vector3D a, Vector3D b){
		return new Vector3D(a.x()-b.x(),
							a.y()-b.y(),
							a.z()-b.z());
	}
	
	//dot product
	public static double dot(Vector3D a, Vector3D b){
		return a.x()*b.x() + a.y()*b.y() + a.z()*b.z();
	}

}
