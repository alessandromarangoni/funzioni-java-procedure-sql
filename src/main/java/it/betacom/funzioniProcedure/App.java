package it.betacom.funzioniProcedure;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;


public class App {
	static ArrayList<Autori> autori = new ArrayList<Autori>();
	
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
				System.out.println("Età: " + age);
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
	
	
	public static void DropProcedure() {
		try (Statement stm = con.createStatement()){
			stm.executeUpdate("DROP PROCEDURE IF EXISTS get_age_autori_nazione");
		}catch(SQLException e){
			System.out.println("impossibile droppare procedura: get_age_autori_nazione");
		}
	}
	
	public static void createProcedure() {
	    try (Statement stm = con.createStatement()) {
	        String sql = "CREATE PROCEDURE get_age_autori_nazione(IN nazione VARCHAR(255)) "
	                   + "BEGIN "
	                   + "    DROP TABLE IF EXISTS autori_eta_temp; "
	                   + "    CREATE TABLE autori_eta_temp( "
	                   + "        nome VARCHAR(255), "
	                   + "        cognome VARCHAR(255), "
	                   + "        eta INT, "
	                   + "        data_odierna TIMESTAMP DEFAULT CURRENT_TIMESTAMP); "
	                   + "    INSERT INTO autori_eta_temp(nome, cognome, eta) "
	                   + "    SELECT "
	                   + "        nome, "
	                   + "        cognome, "
	                   + "        IF(anno_morte IS NOT NULL, anno_morte - anno_nascita, YEAR(CURRENT_DATE) - anno_nascita) AS eta "
	                   + "    FROM "
	                   + "        autori "
	                   + "    WHERE "
	                   + "        autori.nazione = nazione; "
	                   + "END";

	        stm.executeUpdate(sql);
	        System.out.println("Procedura creata: get_age_autori_nazione");
	    } catch (SQLException e) {
	        System.out.println("Impossibile creare la procedura: get_age_autori_nazione");
	        e.printStackTrace();
	    }
	}

	public static void callProcedure(String nazione) {
	    String callProcedure = "{call get_age_autori_nazione(?)}";
	    try {
	        CallableStatement cstm = con.prepareCall(callProcedure);
	        cstm.setString(1, nazione);
	        cstm.execute();
	        Statement stm = con.createStatement();
	        String select = "SELECT nome, cognome, eta FROM autori_eta_temp";
	        ResultSet rs = stm.executeQuery(select);
	        while (rs.next()) {
	            String nome = rs.getString("nome");
	            String cognome = rs.getString("cognome");
	            int eta = rs.getInt("eta");
	            autori.add(new Autori(nome, cognome, eta));
//	            System.out.println(nome + " " + cognome + " " + eta);
	        }
	        rs.close();
	        stm.close();
	        cstm.close();
	        autori.stream().sorted((a1, a2) -> Integer.compare(a2.getEta(), a1.getEta())).forEach(a -> System.out.println(a.getCognome()+ " " + a.getEta()));
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	
	
    public static void main( String[] args )
    {
    	//CONNETTO AL DB
    	connectDB();
    	//PRENDO ETA E DISPLAY IN CONSOLE 
    	getAge();
    	//DROPPO PROCEDURA get_age_autori_nazione
    	DropProcedure();
    	//CREO PROC get_age_autori_nazione
    	createProcedure();
    	
    	callProcedure("italia");
    }
}
