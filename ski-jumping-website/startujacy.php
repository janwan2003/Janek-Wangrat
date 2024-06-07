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
    <a class="active" href="konkursy.php">Konkursy</a>
    <a href="zawodnicy.php">Zawodnicy</a>
    <a href="kraje.php">Kraje</a>
    <a href="skryptPostgresql.php">Skrypt PostgreSQL</a>
    </div>
    <?PHP
    $conn = pg_connect("host=lkdb dbname=bd user=jw440012 password=iks");
    $x = $_GET['id'];
    $q = pg_query($conn, "select * from Konkurs where id = $x");
    while ($val = pg_fetch_array($q)) {
        echo "<p>".$val['organizator']." / " .$val['nazwa']. " / ".$val['data_konkursu']."<p>";
        echo "<p>Termin zgłoszeń: ".$val['termin_zgloszen']."<p>";
    }
    $q = pg_query($conn, "select policz_zawodnikow as ile from policz_zawodnikow($x)");
    $val = pg_fetch_array($q);
    echo "Liczba zawodników: ";
    echo $val['ile'];
    echo "<br>";
    echo "<A HREF=\"startujacy.php?id=$x&opt=zawodnik.id\" class=\"button\">ID<A>";
    echo "<A HREF=\"startujacy.php?id=$x&opt=imie\" class=\"button\">Imie<A>";
    echo "<A HREF=\"startujacy.php?id=$x&opt=nazwisko\" class=\"button\">Nazwisko<A>";
    echo "<A HREF=\"startujacy.php?id=$x&opt=kraj\" class=\"button\">Kraj<A>";
    $ord = 'ID';
    if ($_GET['opt'] != null)
      $ord = $_GET['opt'];
    $q = pg_query($conn, "select * from Zawodnik join Zgloszenie on Zawodnik.id = Zgloszenie.id_zawodnika where Zgloszenie.id_konkursu = $x order by $ord");
    echo "
  <table border=\"5\" cellpadding=\"5\" cellspacing=\"0\" style=\"border-  collapse: collapse\" bordercolor=\"#808080\" width=\"50&#37;\"    id=\"AutoNumber2\" bgcolor=\"#C0C0C0\">
   <tr>
   <td width=100>ID</td> 
  <td width=100>Imię</td> 
  <td width=100>Nazwisko</td> 
  <td width=100>Kraj</td>  
  </tr>"; 
    while ($val = pg_fetch_array($q)) {
        echo "<tr>";
        echo "<td>" .$val['id_zawodnika']. " </td>";
        echo "<td>" .$val['imie']. " </td>";
        echo "<td>" .$val['nazwisko']. " </td>";
        echo "<td>" .$val['kraj']. " </td>";
        echo "</tr>";
    }
    echo "</table>";
    if ($_SESSION['logged'] == 1) {
      echo "<br><p>Dodaj zawodnika";
      $q = pg_query($conn, "select * from Zawodnik");
      echo "<form method=\"post\" action=\"zawodnikDoKonkursu.php?id=$x&opt=$ord\">";
      echo "<select name=\"taskOption\">";
      while ($val = pg_fetch_array($q)) {
        echo "<option value = \"" . $val['id'] . "\">" . $val['id'] . " - " . $val['imie'] . " - " . $val['nazwisko'] . " - " . $val['kraj'] . "</option>";
      }
      echo "</select>";
      echo "<br><br>";
      echo "<input type=\"submit\" value=\"Dodaj\">";
      echo "</form>";
    }
    echo "<br><A href=\"konkurs.php?id=$x\" class=\"button\" position:\"absolute\" bottom:\"0\">Powrót<A><br>";
     pg_close($conn);
    ?>

    </center>
  </BODY>

</HTML>