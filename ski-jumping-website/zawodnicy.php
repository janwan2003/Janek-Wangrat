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

<HTML> 
  <HEAD>
    <TITLE> Skoki Narciarskie </TITLE>
    <link rel="stylesheet" href="styles.css">
    </HEAD>
    <BODY>
    <center>
    <H2> Obsługa konkursu skoków narciarskich </H2>
    <div class="topnav">
    <a href="mainPage.php">Dom</a>
    <a href="modelUML.php">Model UML</a>
    <a href="konkursy.php">Konkursy</a>
    <a class="active" href="zawodnicy.php">Zawodnicy</a>
    <a href="kraje.php">Kraje</a>
    <a href="skryptPostgresql.php">Skrypt PostgreSQL</a>
    </div>
    <?PHP
    $conn = pg_connect("host=lkdb dbname=bd user=jw440012 password=iks");
    echo "<A HREF=\"zawodnicy.php?opt=id\" class=\"button\">ID<A>";
    echo "<A HREF=\"zawodnicy.php?opt=imie\" class=\"button\">Imie<A>";
    echo "<A HREF=\"zawodnicy.php?opt=nazwisko\" class=\"button\">Nazwisko<A>";
    echo "<A HREF=\"zawodnicy.php?opt=kraj\" class=\"button\">Kraj<A>";
    $ord = 'ID';
    if ($_GET['opt'] != null)
      $ord = $_GET['opt'];
    $q = pg_query($conn, "select * from Zawodnik order by $ord");
    echo "
    <br>
  <table border=\"5\" cellpadding=\"5\" cellspacing=\"0\" style=\"border-  \" bordercolor=\"#808080\" width=\"50&#37;\"    id=\"AutoNumber2\" bgcolor=\"#C0C0C0\">
   <tr>
   <td width=100>ID</td> 
  <td width=100>Imię</td> 
  <td width=100>Nazwisko</td> 
  <td width=100>Kraj</td>  
  </tr>"; 
    while ($val = pg_fetch_array($q)) {
        echo "<tr>";
        echo "<td>" .$val['id']. " </td>";
        echo "<td>" .$val['imie']. " </td>";
        echo "<td>" .$val['nazwisko']. " </td>";
        echo "<td>" .$val['kraj']. " </td>";
        echo "</tr>";
    }
    echo "</table>";
    ?>
    <?PHP if($_SESSION['logged'] == 1) : ?>
    <div class="login">
    <H1> Dodaj Zawodnika </H1>
    <FORM ACTION="dodajZawodnika.php" METHOD="POST">  
      Imię:<INPUT TYPE="TEXT" NAME="NAME" required> <br>
      Nazwisko: <INPUT TYPE="TEXT" NAME="SURN" required> <br>
      Kraj: 
      <?php
    $q = pg_query($conn, "select * from Kraj");
    echo "<select name=\"NATI\">";
    while ($val = pg_fetch_array($q)) {
      echo "<option value = \"".$val['nazwa']."\">".$val['nazwa']."</option>";
    }
     echo "</select>";
     pg_close($conn);
?>
      <INPUT TYPE="SUBMIT" VALUE="Zgłoś">
    </FORM>
</div>
<?PHP endif ; ?>
    </center>
  </BODY>

</HTML>