package com.att.sli.blacknode;

/*import org.opendaylight.yang.gen.v1.brocade.sample.rev150115.FetchDataInput;
import org.opendaylight.yang.gen.v1.brocade.sample.rev150115.FetchDataOutput;
import org.opendaylight.yang.gen.v1.brocade.sample.rev150115.FetchDataOutputBuilder;
*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;

public class BlackNode {

    private final Logger log = LoggerFactory.getLogger( BlackNode.class );	
    //FetchDataOutputBuilder fetchDataOutputBuilder = new FetchDataOutputBuilder();
    Connection con = null;
    Statement statement = null;
    ResultSet resultSet = null;
    String email = null;
    public String getDescription(String uId) {
    
      log.info("Inside GetData Function"); 
       try {
            Driver driver;
            driver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(driver);
            con = DriverManager.getConnection("jdbc:mysql://135.25.120.115:3306/RefConnection", "rctro", "d1lb3rt");
            statement = con.createStatement();
	    resultSet = statement.executeQuery("Select * from users WHERE user_id=\""+uId+"\"");
           
	    log.info("Inside try");
            while (resultSet.next()) {
	        log.info("Inside while"+resultSet.toString());
                 email = resultSet.getString(4).toString();                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
      return email;
    }
/*    public FetchDataOutputBuilder getData(FetchDataInput input ) {

	log.info("Inside GetData Function");
        String uId = input.getUserId();

        try {
            Driver driver;
            driver = new com.mysql.jdbc.Driver();
            DriverManager.registerDriver(driver);
            con = DriverManager.getConnection("jdbc:mysql://135.25.120.115:3306/RefConnection", "rctro", "d1lb3rt");
            statement = con.createStatement();
	    resultSet = statement.executeQuery("Select * from users WHERE user_id=\""+uId+"\"");
           
	    log.info("Inside try");
            while (resultSet.next()) {
	        log.info("Inside while"+resultSet.toString());
                fetchDataOutputBuilder.
                    setUsername(resultSet.getString(2)).setPassword(resultSet.getString(3)).setEmail(resultSet.getString(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return fetchDataOutputBuilder;
    }
    
 */

}
