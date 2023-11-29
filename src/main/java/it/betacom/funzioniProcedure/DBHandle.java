package it.betacom.funzioniProcedure;


public class DBHandle {
	private String connection;
	private String user;
	private String password;
	private String nomeDB;
	private static DBHandle instance;
	
	
	private DBHandle() {
		try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
        	System.out.println("attenzione non connesso il driver");
        }
		this.nomeDB = "libri";
		this.connection = "jdbc:mysql://localhost:3306/" + nomeDB;
		this.user = "root";
		this.password = "root";
	}
	
    public static DBHandle getInstance() {
        if (instance == null){
            instance = new DBHandle();
        }
        return instance;
    }

	public String getConnection() {
		return connection;
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
    
}