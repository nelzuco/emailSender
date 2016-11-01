package htmlMail;

public class Suscriptor {
	String nombre;
	String email;
	public Suscriptor(String nom, String e) {
		this.nombre= nom;
		this.email= e;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
}
