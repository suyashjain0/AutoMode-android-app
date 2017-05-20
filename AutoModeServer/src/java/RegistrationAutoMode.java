/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException; import java.sql.Statement;
import java.util.Enumeration;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Lenovo
 */
public class RegistrationAutoMode extends HttpServlet {

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
       RegistrationData registrationData= new RegistrationData();
       
       while(en.hasMoreElements()){
           String key = (String) en.nextElement();
           String value = request.getHeader(key);
           System.out.println("Key: "+ key +" Value: "+value);
           if(key!=null && key.equalsIgnoreCase("userId")){
              registrationData.setUserId(value);
           }
           else if(key!=null && key.equalsIgnoreCase("email")){
              registrationData.setEmail(value);
            }
           else if(key!=null && key.equalsIgnoreCase("password")){
              registrationData.setPassword(value);
            }
           else if(key!=null && key.equalsIgnoreCase("confirmPassword")){
              registrationData.setConfrimPassword(value);
            }
       }
        
        insertDataToRegisterTable(registrationData);
        printWriter.print("Success");

    }
    
    private void insertDataToRegisterTable(RegistrationData registrationData)
    {
        
    Connection con = null;
    Statement st = null;
        String id=registrationData.getUserId();
        String email=registrationData.getEmail();
        String password=registrationData.getPassword();
        String conpassword=registrationData.getConfrimPassword();
        try 
        {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver Loaded");

            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/automode","root","root");
            System.out.println("Connected");

            st = con.createStatement();

            //String query = "Insert into emp values(100,'Ram','987654')";
            //String query = "Insert into emp values("+id+",'"+name+"','"+password+"')";
            
            String query = "Insert into registrationdata values('"+id+"','"+email+"','"+password+"','"+conpassword+"')";
            System.out.println(query);
            
            int i = st.executeUpdate(query);

            if(i>0)
            {
                System.out.println(i+" Record Inserted");
            }
            else
            {
                System.out.println("Record Not Inserted");
            }
            
            con.close();
        } 
        catch (ClassNotFoundException e) 
        {
            System.out.println(e);
        } 
        catch (SQLException e) 
        {
            System.out.println(e);
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
