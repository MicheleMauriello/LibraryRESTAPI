import com.google.gson.Gson;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
@Path("/book")
public class Library {
    private final String error = "Server error, contact administrators";
    private boolean checkParams(String isbn,String autore, String titolo){
        return (isbn == null || isbn.trim().length() == 0) || (titolo == null || titolo.trim().length() == 0) || (autore == null || autore.trim().length() == 0);
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response read(){
        final String QUERY = "SELECT * FROM Libri";
        final List<Book> books = new ArrayList<>();
        final String[] data = Database.getData();
        try(

                Connection conn = DriverManager.getConnection(data[0]);
                PreparedStatement pstmt = conn.prepareStatement( QUERY )
        ) {
            ResultSet results =  pstmt.executeQuery();
            while (results.next()){
                Book book = new Book();
                book.setTitolo(results.getString("Titolo"));
                book.setAutore(results.getString("Autore"));
                book.setISBN(results.getString("ISBN"));
                books.add(book);

            }
        }catch (SQLException e){
            e.printStackTrace();
            String obj = new Gson().toJson(error);
            return Response.serverError().entity(obj).build();
        }
        String obj = new Gson().toJson(books);
        return Response.status(200).entity(obj).build();
    }

    @PUT
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response update(@FormParam("ISBN") String isbn,
                           @FormParam("Titolo")String titolo,
                           @FormParam("Autore") String autore){
        if(checkParams(isbn, titolo, autore)) {
            String obj = new Gson().toJson("Parameters must be valid");
            return Response.serverError().entity(obj).build();
        }
        final String QUERY = "UPDATE Libri SET Titolo = ?, Autore = ? WHERE ISBN = ?";
        final String[] data = Database.getData();
        try(

                Connection conn = DriverManager.getConnection(data[0]);//, data[1], data[2]);
                PreparedStatement pstmt = conn.prepareStatement( QUERY )
        ) {
            pstmt.setString(1,titolo);
            pstmt.setString(2,autore);
            pstmt.setString(3,isbn);
            pstmt.execute();
        }catch (SQLException e){
            e.printStackTrace();
            String obj = new Gson().toJson(error);
            return Response.serverError().entity(obj).build();
        }
        String obj = new Gson().toJson("Libro con ISBN:" + isbn + " modificato con successo");
        return Response.ok(obj,MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response create(@FormParam("ISBN") String isbn,
                           @FormParam("Titolo")String titolo,
                           @FormParam("Autore") String autore){
        if(checkParams(isbn, titolo, autore)) {
            String obj = new Gson().toJson("Parameters must be valid");
            return Response.serverError().entity(obj).build();
        }
        final String QUERY = "INSERT INTO Libri(ISBN,Titolo,Autore) VALUES(?,?,?)";
        final String[] data = Database.getData();
        try(

                Connection conn = DriverManager.getConnection(data[0]);
                PreparedStatement pstmt = conn.prepareStatement( QUERY )
        ) {
            pstmt.setString(1,isbn);
            pstmt.setString(2,autore);
            pstmt.setString(3,titolo);
            pstmt.execute();
        }catch (SQLException e){
            e.printStackTrace();
            String obj = new Gson().toJson(error);
            return Response.serverError().entity(obj).build();
        }
        String obj = new Gson().toJson("Libro con ISBN:" + isbn + " aggiunto con successo");
        return Response.ok(obj,MediaType.APPLICATION_JSON).build();
    }

    @DELETE
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response update(@FormParam("ISBN") String isbn){
        if(isbn == null || isbn.trim().length() == 0){
            String obj = new Gson().toJson("ISBN must be valid");
            return Response.serverError().entity(obj).build();
        }
        final String QUERY = "DELETE FROM Libri WHERE ISBN = ?";
        final String[] data = Database.getData();
        try(

                Connection conn = DriverManager.getConnection(data[0]);
                PreparedStatement pstmt = conn.prepareStatement( QUERY )
        ) {
            pstmt.setString(1,isbn);
            pstmt.execute();
        }catch (SQLException e){
            e.printStackTrace();
            String obj = new Gson().toJson(error);
            return Response.serverError().entity(obj).build();
        }
        String obj = new Gson().toJson("Libro con ISBN:" + isbn + " eliminato con successo");
        return Response.ok(obj,MediaType.APPLICATION_JSON).build();
    }
}

    @PUT
    @Path("/Prestito")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response update2(@FormParam("idPrestiti") String idPrestiti,
                       @FormParam("IDLibro")   String IDLibro,
                       @FormParam("idUtente")   String idUtente,
                       @FormParam("dataInizio") String dataInizio,
                       @FormParam("dataFine") String dataFine,
                       @FormParam("ISBN") String ISBN){
        if(checkParams2(idPrestiti, IDLibro, idUtente, dataInizio, dataFine, ISBN))
        {
            String obj = new Gson().toJson("Parameters must be valid");
            return Response.serverError().entity(obj).build();
        }             
        int totale = 0;
        final String QUERY = "INSERT INTO Prestiti(idPrestiti, IDLibro, idUtente, dataInizio, dataFine, ISBN) VALUE(?,?,?,?)";
        final String querySelezionaTotale = "SELECT Totale FROM Libri WHERE ISBN = '"+ISBN+"'";
        
        final String[] data = Database.getData();
        try(

            Connection connection = DriverManager.getConnection(data[0]);
            PreparedStatement pstmt = connection.prepareStatement( QUERY );
            PreparedStatement pstmt1 = connection.prepareStatement( querySelezionaTotale );
    ) {
        pstmt.setString(1, idPrestiti);
        pstmt.setString(2, IDLibro);
        pstmt.setString(3, idUtente);
        pstmt.setString(4, dataInizio);
        pstmt.setString(5, dataFine);
        pstmt.setString(6, ISBN);
        pstmt.execute();

        ResultSet r = pstmt1.executeQuery();
        while(r.next()){
            totale = Integer.parseInt(r.getString("Totale libri"));
        }
        if(totale >0 ){
            final String QUERYModificaTotale = "UPDATE Libri SET Totale = totale - '"+totale+"'";
            PreparedStatement pstmt2 = connection.prepareStatement( QUERYModificaTotale );
        }
    }catch (SQLException e){
        e.printStackTrace();
        String obj = new Gson().toJson(error);
        return Response.serverError().entity(obj).build();
    }
    String obj = new Gson().toJson("Libro con ISBN:" + ISBN + " aggiunto con successo");
    return Response.ok(obj,MediaType.APPLICATION_JSON).build();
}

}