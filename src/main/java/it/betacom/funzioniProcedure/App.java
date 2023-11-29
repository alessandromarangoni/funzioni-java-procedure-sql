package it.betacom.funzioniProcedure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;



/**
 * Hello world!
 *
 */
public class App {
	
	static Connection con = null;
	
	private static void connectDB(){
		DBHandle db = DBHandle.getInstance();
		try {
			con = DriverManager.getConnection(db.getConnection(), db.getUser(), db.getPassword()); 
		} catch (SQLException e1) {
			System.out.println("non connesso");
		}
	}
	
	private static void getAge() {
		try (Statement stm = con.createStatement()){
			 ResultSet rs = stm.executeQuery("select anno_morte, anno_nascita from autori");
			 while(rs.next()) {
				int annoMorte = rs.getInt("anno_morte");
				int annoNascita = rs.getInt("anno_nascita");
				int age = calculateAge(annoMorte,annoNascita);
				System.out.println(age);
			 }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static int calculateAge(int annoMorte, int annoNascita) {
		int age = 0;
		if(annoMorte != 0 ) {
			age = annoMorte - annoNascita;
		}else {
			age = LocalDate.now().getYear() - annoNascita;
		}
		return age;
	}
	
    public static void main( String[] args )
    {
    	connectDB();
    	getAge();
    }
}
