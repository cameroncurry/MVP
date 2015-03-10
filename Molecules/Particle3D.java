
public class Particle3D {
	
	private final double mass;
	private Vector3D position;
	private Vector3D velocity;

	public Particle3D(double mass, Vector3D position, Vector3D velocity) {
		this.mass = mass;
		this.position = position;
		this.velocity = velocity;
	}
	
	public double mass(){return mass;}
	public Vector3D position(){return position;}
	public Vector3D velocity(){return velocity;}
	
	public void setPosition(Vector3D position){
		this.position = new Vector3D(position);//make deep copy just in case
	}
	public void setVelocity(Vector3D velocity){this.velocity = new Vector3D(velocity);}
	
	//the distance between particle a and particle b
	public static Vector3D separation(Particle3D a, Particle3D b){
		return Vector3D.subtract(a.position(), b.position());
	}

}
