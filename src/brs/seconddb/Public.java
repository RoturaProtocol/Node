package brs.seconddb;

import java.sql.*;

public class Public {

  public Boolean verifyWhiteList(String address) throws SQLException{

      boolean re;
      Connection conn = DriverManager.getConnection(
        "jdbc:mariadb://43.135.44.240:3306/signum_business",
        "root", "z1050493759"
      );
      Statement stmt = conn.createStatement();
      String sql = "select address from white_list where status = 0 and address = '"+address+"'";
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
    Connection conn = DriverManager.getConnection(
      "jdbc:mariadb://43.138.104.154:3306/signum_testnet",
      "root", "z497688734"
    );
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
  }

}
