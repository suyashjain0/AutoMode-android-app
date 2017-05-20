/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;


/**
 *
 * @author Lenovo
 */
public class LoginUser extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
   
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
       PrintWriter printWriter= response.getWriter();
       Enumeration en =  request.getHeaderNames();
       LoginData data=new LoginData(); 
       while(en.hasMoreElements()){
           String key = (String) en.nextElement();
           String value = request.getHeader(key);
           System.out.println("Key: "+ key +" Value: "+value);
           if(key!=null && key.equalsIgnoreCase("userId")){
              data.setEmail(value);
           }
           else if(key!=null && key.equalsIgnoreCase("password")){
              data.setPassword(value);
            }
       }
            Boolean isLogin=isLogin(data);
            if(isLogin!=null && isLogin){
                printWriter.print("Login success");
            }
            else{
                printWriter.print("Login faliure");
            }

            
            
       
    }
    
    public Boolean isLogin(LoginData data)
    {  
        String username=data.getEmail();
        String password=data.getPassword();
        String tablePassword="";
        Connection con = null;
        Statement st = null;
         
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver Loaded");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/automode","root","root");
            System.out.println("Connected");
            st = con.createStatement();
            String query="select password from registrationdata where userId='"+username+"'";
            ResultSet rs=st.executeQuery(query);
            if(rs.next())
            {
            tablePassword=rs.getString(1);
            }
        }
        catch (Exception e) 
        {           
            System.out.println(e);
            return false;
        } 
        if(username!=null && password!=null && password.equals(tablePassword))
        {
           return true;
        }
        else
        {
           return false;
        }
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
