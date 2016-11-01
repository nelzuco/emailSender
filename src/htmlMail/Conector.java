package htmlMail;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.SingleSelectionModel;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class Conector {
	Connection con = null;
	ArrayList listaS= new ArrayList();
	
	
	public void conectar(){
	
	//...

	try {
	    this.con = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/randazzo", "root", "");
	    System.out.println("conectado");
	    // Otros y operaciones sobre la base de datos...

	} catch (SQLException ex) {

	    // Mantener el control sobre el tipo de error
	    System.out.println("SQLException: " + ex.getMessage());

		}
	}
	public void cerrar() throws SQLException{
		con.close();

	}
	
	public void consultar(){
		ResultSet rs = null;
		Statement cmd = null;

		// ...

		try {
		    cmd = (Statement) con.createStatement();
		    rs = cmd.executeQuery("SELECT nyap,email FROM suscriptor");
		    while (rs.next()) {
		    	String nombre = rs.getString("nyap");
		    	String email = rs.getString("email");	
		        //System.out.println(email);
		        Suscriptor s= new Suscriptor(nombre, email);
		        this.listaS.add(s);		        
		    }
		    rs.close();
		    // ...
		}catch (Exception e) {
			System.out.println("fallo la consulta");
		}
		
	}
	
	public ArrayList devolverLista(){
		
		
		return this.listaS;
	}
	
	
}	
