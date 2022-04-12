<!DOCTYPE html>
<html>
    <head>
        <script>
            function loadDoc() {
            const xhttp = new XMLHttpRequest();
            //gestione risposta
            xhttp.onload = function() {
                document.getElementById("demo").innerHTML = this.responseText;
                //effettuo il parsing della risposta
                let dati=JSON.parse(this.responseText); 
                //inserisco i dati nell'interfaccia
                document.getElementById("num").value=dati[0].id;
                document.getElementById("nom").value=dati[0].info;
            }

            var select = document.getElementById('funzioni');
            var value = select.options[select.selectedIndex].value;

            switch (value){

                case "get":

                    break;
                
                case "put":

                    break;
                
                case "post":

                    break;

                case "delete":

                    break;

            }

            //preparo l'URL
            xhttp.open("GET", "resources/app");
            //popolo l'intestazione
            xhttp.setRequestHeader("accept","application/json");
            //richiamo l'URL
            xhttp.send();
            }
        </script>
    </head>
    <body>

        <h2>The XMLHttpRequest Object</h2>
        <button type="button" onclick="loadDoc()">Request data</button>

        <p id="demo"></p>

        <select name="funzioni" id="funzioni">
            <option value="get">get</option>
            <option value="put">update</option>
            <option value="post">add</option>
            <option value="delete">delete</option>
          </select> <br>
        <br>
        <form>
            id<input type="text" name="num" id="num">
            info<input type="text" name="nom" id="nom">    
        </form>
    </body>
</html>