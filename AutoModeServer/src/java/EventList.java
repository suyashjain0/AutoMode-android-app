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
import java.sql.Statement;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Lenovo
 */
public class EventList extends HttpServlet {

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
       EventData data=new EventData(); 
       while(en.hasMoreElements()){
           String key = (String) en.nextElement();
           String value = request.getHeader(key);
           System.out.println("Key: "+ key +" Value: "+value);
           if(key!=null && key.equalsIgnoreCase("eventNo")){
              data.setEventNo(value);
           }
       }
            String getUrl=getUrlFromEvent(data);
            if(getUrl!=null && !getUrl.equalsIgnoreCase("Fail"))
            {
                printWriter.print(""+getUrl);
            }
            else
            {
                printWriter.print("Failure");
            }

     }
    
    public String getUrlFromEvent(EventData data)
    {  
        String eventnumber=data.getEventNo();
        String tableUrl="";
        Connection con = null;
        Statement st = null;
         
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver Loaded");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/automode","root","root");
            System.out.println("Connected");
            st = con.createStatement();
            String query="select url from eventlist where eventName='"+eventnumber+"'";
            ResultSet rs=st.executeQuery(query);
            if(rs.next())
            {
            tableUrl=rs.getString(1);
            }
        }
        catch (Exception e) 
        {           
            System.out.println(e);
            return ("Fail");
        } 
        if(tableUrl!=null)
        {
           return tableUrl;
        }
        else
        {
           return ("Fail") ;
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
