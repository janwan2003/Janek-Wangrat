<?PHP 
    session_start();
    if($_SESSION['logged'] == 1){
      $_SESSION['LOGIN'] = $_POST['LOGN'];
      $_SESSION['PASS'] = $_POST['PASW'];
    }
  if ($_SESSION['logged'] == 0) {
    session_destroy();
    header('Location: index.html');
    echo "Nie jesteś zalogowany!";
  }
?>

<html>
    <link rel="stylesheet" href="styles.css">
    <head>
    <TITLE> Obsługa konkursu skoków narciarskich </TITLE>
        <title>
           Strona na zadanie zaliczeniowe z baz danych
        </title>
    </head>
 
    <body>
        
    <center>
    <H2> Obsługa konkursu skoków narciarskich </H2>
    <div class="topnav">
            <a href="mainPage.php">Dom</a>
            <a class="active" href="modelUML.php">Model UML</a>
            <a href="konkursy.php">Konkursy</a>
            <a href="zawodnicy.php">Zawodnicy</a>
            <a href="kraje.php">Kraje</a>
            <a href="skryptPostgresql.php">Skrypt PostgreSQL</a>
    </div>
        <p>Mój model UML:</p>
	  <br><br>
	  <img src="diagram1.png" alt="diagram">
	  <br>
    </center>
    </body>
</html>
