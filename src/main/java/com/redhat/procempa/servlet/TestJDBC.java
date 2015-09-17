package com.redhat.procempa.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.redhat.procempa.domain.Customer;


@WebServlet("/TestJDBC")
public class TestJDBC extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String PROCEMPA_SELECT_CUSTOMER= "Select * from procempa.CUSTOMERS";
	
	private static final String PROCEMPA_CUSTOMERS_JNDI= "java:jboss/datasources/ProcempaDockerDS";
	
	private static InitialContext ctx = null;
    
	private static Connection conn = null;
	
	private PreparedStatement ps = null;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println("<h1>LISTA DE CLIENTES</h1>");
		out.println("<table border=\"1\" style=\"width:100%\">");
		out.println("<tr>");
		out.println("<th>ID</th>");
		out.println("<th>NOME</th>");
		out.println("<th>IDADE</th>");
		out.println("<th>ENDERECO</th>");
		out.println("</tr>");
		List<Customer> customerList = getCustomerList();
		for (Customer customer : customerList) {
			out.println("<tr>");
			out.println("<td>"+ customer.getId() +"</td>");
			out.println("<td>"+ customer.getNome() +"</td>");
			out.println("<td>"+ customer.getIdade() +"</td>");
			out.println("<td>"+ customer.getEndereco() +"</td>");
			out.println("</tr>");
		}
		out.println("</table>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private List<Customer> getCustomerList(){
		List<Customer> customerList = null;
		try{
			ps = getConnection().prepareStatement(PROCEMPA_SELECT_CUSTOMER);
			ResultSet rs =  ps.executeQuery();
			customerList = new ArrayList<Customer>(0);
			while (rs.next()){
				Customer customer = new Customer();
				customer.setId(rs.getInt("ID"));
				customer.setNome(rs.getString("NAME"));
				customer.setIdade(rs.getInt("AGE"));
				customer.setEndereco(rs.getString("ADDRESS"));
				customerList.add(customer);
			}
			closeAll();
		}catch(Exception e){
			e.printStackTrace();
		}
		return customerList;
	}
	
	private static Connection getConnection() {
        try {
            ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(PROCEMPA_CUSTOMERS_JNDI);
            conn = ds.getConnection();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return conn;
	}
	
	private void closeAll(){	
		if (ps != null){
			try{
				ps.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if (conn != null){
			try{
				conn.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if (ctx != null){
			try{
				ctx.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}	
	}

}