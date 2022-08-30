package brs.seconddb;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Public {

  private static final String WL_URL;
  private static final String WL_USER_NAME;
  private static final String WL_PASSWORD;
  private static final String WL_TABLENAME;
  private static final String SIGNUM_URL;
  private static final String SIGNUM_USER_NAME;
  private static final String SIGNUM_PASSWORD;

  static {
    Properties properties = new Properties();
    try {
      properties.load(new FileInputStream("conf/seconddb"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    WL_URL = properties.getProperty("wl_url");
    WL_USER_NAME = properties.getProperty("wl_username");
    WL_PASSWORD = properties.getProperty("wl_password");
    WL_TABLENAME = properties.getProperty("wl_tablename");
    SIGNUM_URL = properties.getProperty("signum_url");
    SIGNUM_USER_NAME = properties.getProperty("signum_username");
    SIGNUM_PASSWORD = properties.getProperty("signum_password");
  }  
  
  public Boolean verifyWhiteList(String address) throws SQLException{

      boolean re;
      Connection conn = DriverManager.getConnection(WL_URL,WL_USER_NAME, WL_PASSWORD);
      Statement stmt = conn.createStatement();
      String sql = "select address from "+WL_TABLENAME+" where status = 0 and address = '"+address+"'";
      ResultSet rs = stmt.executeQuery(sql);
      if(rs.next()){
        re = true;
      }else{
        re = false;
      }
      conn.close();
      return re;
  }

  public int getAccountNumbers(int height) throws  SQLException{
    int numbers = 0;
    Connection conn = DriverManager.getConnection(SIGNUM_URL,SIGNUM_USER_NAME, SIGNUM_PASSWORD);
    Statement stmt = conn.createStatement();
    String sql = "select (select count(distinct id) from account  where a.height>=height) numbers from account a where a.height='"+height+"'";
    ResultSet rs = stmt.executeQuery(sql);
    if(rs.next()){
      numbers = rs.getInt("numbers");
    }
    conn.close();
    return numbers;
  }




//   public static void main(String[] args) {
//     Public test = new Public();
//     try {
// //      System.out.println(test.verifyWhiteList("TS-H9Z5-HYN5-LW8K-FZJLS"));
//       System.out.println(test.getAccountNumbers(14457));
//     } catch (SQLException e) {
//       e.printStackTrace();
//     }
//    try {
////      System.out.println(test.verifyWhiteList("TS-H9Z5-HYN5-LW8K-FZJLS"));
//      for (int i=1; i <= 97300; i++) {
//        System.out.println(test.getAccountCount(i));
//      }
//    } catch (SQLException e) {
//      e.printStackTrace();
//    }
//   }

}
