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
    <?PHP if($_SESSION['logged'] != 0) : ?>
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
    }
    $q = pg_query($conn, "select policz_zawodnikow as ile from policz_zawodnikow($x)");
    $val = pg_fetch_array($q);
    echo "Liczba zawodników: ";
    echo $val['ile'];
    echo "<br>";
    echo "<A HREF=\"wyniki.php?id=$x&opt=imie\" class=\"button\">Imie<A>";
    echo "<A HREF=\"wyniki.php?id=$x&opt=nazwisko\" class=\"button\">Nazwisko<A>";
    echo "<A HREF=\"wyniki.php?id=$x&opt=kraj\" class=\"button\">Kraj<A>";
    echo "<A HREF=\"wyniki.php?id=$x&opt=dlugosc\" class=\"button\">Długość<A>";
    echo "<A HREF=\"wyniki.php?id=$x&opt=ocena\" class=\"button\">Ocena<A>";
    $ord = 'ocena';
    if ($_GET['opt'] != null)
      $ord = $_GET['opt'];
    if($val['ile'] > 5){
      echo "<p>Seria kwalifikacyjna<p>";
      $q = pg_query($conn, "select row_number() over (order by ocena desc), * from Skok join zawodnik z on skok.id_zawodnika = z.id where skok.id_konkursu=$x and skok.seria=0 order by $ord desc");
      echo "
      <table border=\"5\" cellpadding=\"5\" cellspacing=\"0\" style=\"border-  collapse: collapse\" bordercolor=\"#808080\" width=\"50&#37;\"    id=\"AutoNumber2\" bgcolor=\"#C0C0C0\">
       <tr>
       <td width=100>Miejsce</td>
       <td width=100>ID</td> 
      <td width=100>Imię</td> 
      <td width=100>Nazwisko</td> 
      <td width=100>Kraj</td>  
      <td width=100>Długość</td> 
      <td width=100>Ocena</td>    
      </tr>"; 
        while ($val = pg_fetch_array($q)) {
            echo "<tr>";
            echo "<td>" .$val['row_number']. " </td>";
            echo "<td>" .$val['id_zawodnika']. " </td>";
            echo "<td>" .$val['imie']. " </td>";
            echo "<td>" .$val['nazwisko']. " </td>";
            echo "<td>" .$val['kraj']. " </td>";
        if ($val['dlugosc'] != -1)
          echo "<td>" . $val['dlugosc'] . " </td>";
          else echo "<td> DSQ </td>";
           if($val['ocena'] != -1) 
           echo "<td>" .$val['ocena']. " </td>";
           else
           echo "<td> DSQ </td>";
            echo "</tr>";
        }
        echo "</table><br>";
    }
    echo "<p>Seria pierwsza<p>";
    $q = pg_query($conn, "select row_number() over (order by ocena desc), * from Skok join zawodnik z on skok.id_zawodnika = z.id where skok.id_konkursu=$x and skok.seria=1 order by $ord desc");
    echo "
    <table border=\"5\" cellpadding=\"5\" cellspacing=\"0\" style=\"border-  collapse: collapse\" bordercolor=\"#808080\" width=\"50&#37;\"    id=\"AutoNumber2\" bgcolor=\"#C0C0C0\">
    <tr>
    <td width=100>Miejsce</td> 
    <td width=100>ID</td> 
    <td width=100>Imię</td> 
    <td width=100>Nazwisko</td> 
    <td width=100>Kraj</td>  
    <td width=100>Dlugosc</td> 
    <td width=100>Ocena</td>    
    </tr>"; 
    while ($val = pg_fetch_array($q)) {
        echo "<tr>";
        
        echo "<td>" .$val['row_number']. " </td>";
        echo "<td>" .$val['id_zawodnika']. " </td>";
        echo "<td>" .$val['imie']. " </td>";
        echo "<td>" .$val['nazwisko']. " </td>";
        echo "<td>" .$val['kraj']. " </td>";
        if ($val['dlugosc'] != -1)
        echo "<td>" . $val['dlugosc'] . " </td>";
        else echo "<td> DSQ </td>";
         if($val['ocena'] != -1) 
         echo "<td>" .$val['ocena']. " </td>";
         else
         echo "<td> DSQ </td>";
        echo "</tr>";
    }
    echo "</table><br>";
    echo "<p>Seria finałowa<p>";
    $q = pg_query($conn, "select row_number() over (order by ocena desc), * from Skok join zawodnik z on skok.id_zawodnika = z.id where skok.id_konkursu=$x and skok.seria=2 order by $ord desc");
    echo "
    <table border=\"5\" cellpadding=\"5\" cellspacing=\"0\" style=\"border-  collapse: collapse\" bordercolor=\"#808080\" width=\"50&#37;\"    id=\"AutoNumber2\" bgcolor=\"#C0C0C0\">
    <tr>
    <td width=100>Miejsce</td> 
    <td width=100>ID</td> 
    <td width=100>Imię</td> 
    <td width=100>Nazwisko</td> 
    <td width=100>Kraj</td>  
    <td width=100>Dlugosc</td> 
    <td width=100>Ocena</td>    
    </tr>"; 
    while ($val = pg_fetch_array($q)) {
        echo "<tr>";
        echo "<td>" .$val['row_number']. " </td>";
        echo "<td>" .$val['id_zawodnika']. " </td>";
        echo "<td>" .$val['imie']. " </td>";
        echo "<td>" .$val['nazwisko']. " </td>";
        echo "<td>" .$val['kraj']. " </td>";
        if ($val['dlugosc'] != -1)
        echo "<td>" . $val['dlugosc'] . " </td>";
        else echo "<td> DSQ </td>";
         if($val['ocena'] != -1) 
         echo "<td>" .$val['ocena']. " </td>";
         else
         echo "<td> DSQ </td>";
        echo "</tr>";
    }
    echo "</table><br>";
    echo "<A href=\"konkurs.php?id=$x\" class=\"button\" position:\"absolute\" bottom:\"0\">Powrót<A><br>";
    pg_close($conn);
    ?>
    <?PHP endif ; ?>
    </center>
  </BODY>

</HTML>