<?PHP 
    session_start();
    if($_SESSION['logged'] == 1){
      $_SESSION['LOGIN'] = $_POST['LOGN'];
      $_SESSION['PASS'] = $_POST['PASW'];
    }
  if ($_SESSION['logged'] != 1) {
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
    }
    $q = pg_query($conn, "select * from Kraj left join (select * from Kwota where Kwota.id_konkursu = $x) A on Kraj.nazwa = A.kraj order by Kraj.nazwa");
    $w = pg_query($conn, "select Kraj.nazwa, count(A.id_zawodnika) as ile from Kraj left join (select * from Zgloszenie join Zawodnik on Zgloszenie.id_zawodnika 
    = Zawodnik.id where id_konkursu = $x)
     A on  Kraj.nazwa = A.kraj group by Kraj.nazwa order by Kraj.nazwa");
    echo "
    <br>
  <table border=\"5\" cellpadding=\"5\" cellspacing=\"0\" style=\"border-  \" bordercolor=\"#808080\" width=\"50&#37;\"    id=\"AutoNumber2\" bgcolor=\"#C0C0C0\">
   <tr> 
  <td width=100>Nazwa kraju</td> 
  <td width=100>Zgłoszonych</td> 
  <td width=100>Kwota</td>
  </tr>"; 
    while ($val = pg_fetch_array($q)) {
        $val2 = pg_fetch_array($w);
        echo "<tr>";
        $pom = $val['kwota_dodatkowa'] + 2;
        echo "<td>" .$val['nazwa']. " </td>";
        echo "<td>" .$val2['ile']. " </td>";
        if($val['kwota_dodatkowa'] != null)echo "<td> $pom </td>";
        else echo "<td> 2 </td>";
        echo "</tr>";
    }
    echo "</table>";
     ?>
     
    <div class="login">
    <H1> Zwiększ kwotę o(bazowo 2) </H1>
    <?php echo "<FORM ACTION=\"zwiekszKwote.php?id=$x\" METHOD=\"POST\">"; ?>
      Wartość:<INPUT TYPE="TEXT" NAME="VALU"> <br>
      Kraj: 
      <?php
    $q = pg_query($conn, "select * from Kraj");
    echo "<select name=\"NATI\">";
    while ($val = pg_fetch_array($q)) {
      echo "<option value = \"".$val['nazwa']."\">".$val['nazwa']."</option>";
    }
     echo "</select>";
    ?>
      <INPUT TYPE="SUBMIT" VALUE="Zwiększ">
    </FORM>
</div>
<?php echo "<A href=\"konkurs.php?id=$x\" class=\"button\" position:\"absolute\" bottom:\"0\">Powrót<A><br>"; ?>
    </center>
  </BODY>

</HTML>
