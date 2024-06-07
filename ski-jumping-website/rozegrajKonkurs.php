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
    <?PHP if($_SESSION['logged'] == 1) : ?>
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
    if ($val = pg_fetch_array($q)) {
        echo "<p>".$val['organizator']." / " .$val['nazwa']. " / ".$val['data_konkursu']."<p>";
    }
    $_SESSION['curID'] = $x;
    $q = pg_query($conn, "select policz_zawodnikow as ile from policz_zawodnikow($x)");
    $val = pg_fetch_array($q);
    echo "Liczba zawodników: ";
    echo $val['ile'];
    echo "<br>(w razie dyskwalifikacji wpisz -1 w obu polach)<br>";
    $pom = 0;
    $flag = true;
    if ($val['ile'] > 5) {
      $q = pg_query($conn, "
      select q,w,e,r from (select Zawodnik.id q, Zawodnik.imie w, Zawodnik.nazwisko e, Zawodnik.Kraj r, count(skok.id)
      from Zawodnik left join (select * from skok where skok.id_konkursu=$x) skok on skok.id_zawodnika = zawodnik.id where skok.id_konkursu=$x or skok.id_konkursu is null
      group by Zawodnik.id, Zawodnik.imie, Zawodnik.nazwisko, Zawodnik.Kraj having count(skok.id)=0) A join zgloszenie on q = zgloszenie.id_zawodnika where zgloszenie.id_konkursu=$x
  
    ");
      if ($val = pg_fetch_array($q)) {
        echo "<p>Seria kwalifikacyjna!<p>";
        $_SESSION['zaw'] = $val['q'];
        $_SESSION['curSer'] = 0;
        echo "<p>Zawodnik: " . $val['q'] . " - " . $val['w'] . " - " . $val['e'] . " - " . $val['r'] . "<p>";
        echo "<div class=\"login\">
        <H1> Podaj dane skoku: </H1>
        <FORM ACTION=\"dodajSkok.php\" METHOD=\"POST\">  
        Długość: <INPUT TYPE=\"TEXT\" NAME=\"DLUG\"> <br>
        Ocena: <INPUT TYPE=\"TEXT\" NAME=\"OCEN\"> <br>
        <INPUT TYPE=\"SUBMIT\" VALUE=\"Dodaj\">
        </FORM>
        </div>";
        $flag = false;
      } else
        $pom = 1;
    }
    if ($flag) {
      $_SESSION['curSer'] = 1;

      $q = pg_query($conn, "
      select count(*) as ile from (select q,w,e,r from (select Zawodnik.id q, Zawodnik.imie w, Zawodnik.nazwisko e, Zawodnik.Kraj r, count(skok.id), max(skok.ocena) t
      from Zawodnik left join (select * from skok where skok.id_konkursu=$x) skok on skok.id_zawodnika = zawodnik.id where skok.id_konkursu=$x or skok.id_konkursu is null
      group by Zawodnik.id, Zawodnik.imie, Zawodnik.nazwisko, Zawodnik.Kraj having count(skok.id)>0+$pom) A join zgloszenie on q = zgloszenie.id_zawodnika where zgloszenie.id_konkursu=$x
      order by t desc) A
     ");
     $val2 = pg_fetch_array($q);
      $w = pg_query($conn, "
      select q, w, e, r, t, row_number() over () u from (select q,w,e,r,t from (select Zawodnik.id q, Zawodnik.imie w, Zawodnik.nazwisko e, Zawodnik.Kraj r, count(skok.id), max(skok.ocena) t
      from Zawodnik left join (select * from skok where skok.id_konkursu=$x) skok on skok.id_zawodnika = zawodnik.id where skok.id_konkursu=$x or skok.id_konkursu is null
      group by Zawodnik.id, Zawodnik.imie, Zawodnik.nazwisko, Zawodnik.Kraj having count(skok.id)=0+$pom) A join zgloszenie on q = zgloszenie.id_zawodnika where zgloszenie.id_konkursu=$x
      order by t desc limit 5-".$val2['ile'].") A order by u desc");
      if (($val2['ile'] < 5) && $val = pg_fetch_array($w)) {
        echo "<p>Seria pierwsza!<p>";
        $_SESSION['zaw'] = $val['q'];
        echo "<p>Zawodnik: " . $val['q'] . " - " . $val['w'] . " - " . $val['e'] . " - " . $val['r'] . "<p>";
        echo "<div class=\"login\">
        <H1> Podaj dane skoku: </H1>
        <FORM ACTION=\"dodajSkok.php\" METHOD=\"POST\">  
        Długość: <INPUT TYPE=\"TEXT\" NAME=\"DLUG\" VALUE=\"\"> <br>
        Ocena: <INPUT TYPE=\"TEXT\" NAME=\"OCEN\" VALUE=\"\"> <br>
        <INPUT TYPE=\"SUBMIT\" VALUE=\"Dodaj\">
        </FORM>
        </div>";
      } else {
        $q = pg_query($conn, "
        select count(*) as ile from (select q,w,e,r from (select Zawodnik.id q, Zawodnik.imie w, Zawodnik.nazwisko e, Zawodnik.Kraj r, count(skok.id), max(skok.ocena) t
        from Zawodnik left join (select * from skok where skok.id_konkursu=$x) skok on skok.id_zawodnika = zawodnik.id where skok.id_konkursu=$x or skok.id_konkursu is null
        group by Zawodnik.id, Zawodnik.imie, Zawodnik.nazwisko, Zawodnik.Kraj having count(skok.id)>1+$pom) A join zgloszenie on q = zgloszenie.id_zawodnika where zgloszenie.id_konkursu=$x
        order by t desc) A
       ");
       $val2 = pg_fetch_array($q);
        $w = pg_query($conn, "
        select q, w, e, r, t, row_number() over () u from (select q,w,e,r,t from (select Zawodnik.id q, Zawodnik.imie w, Zawodnik.nazwisko e, Zawodnik.Kraj r, count(skok.id), max(skok.ocena) t
        from Zawodnik left join (select * from skok where skok.id_konkursu=$x) skok on skok.id_zawodnika = zawodnik.id where skok.id_konkursu=$x or skok.id_konkursu is null
        group by Zawodnik.id, Zawodnik.imie, Zawodnik.nazwisko, Zawodnik.Kraj having count(skok.id)=1+$pom) A join zgloszenie on q = zgloszenie.id_zawodnika where zgloszenie.id_konkursu=$x
        order by t desc limit 3-".$val2['ile'].") A order by u desc");
     if (($val2['ile'] < 3) && $val = pg_fetch_array($w)) {
          echo "<p>Seria finałowa!<p>";
          $_SESSION['zaw'] = $val['q'];
          $_SESSION['curSer'] = 2;
          echo "<p>Zawodnik: " . $val['q'] . " - " . $val['w'] . " - " . $val['e'] . " - " . $val['r'] . "<p>";
          echo "<div class=\"login\">
        <H1> Podaj dane skoku: </H1>
        <FORM ACTION=\"dodajSkok.php\" METHOD=\"POST\">  
        Długość: <INPUT TYPE=\"TEXT\" NAME=\"DLUG\" required> <br>
        Ocena: <INPUT TYPE=\"TEXT\" NAME=\"OCEN\" required> <br>
        <INPUT TYPE=\"SUBMIT\" VALUE=\"Dodaj\">
        </FORM>
        </div>";
        } else
          echo "<br>Konkurs zakończony!<br>";
      }
    }
    echo "<br><button onclick=\"history.back()\" class=\"button\" position:\"absolute\" bottom:\"0\">Powrót</button><br>";
    pg_close($conn);
    ?>
    <?PHP endif ; ?>
    </center>
  </BODY>

</HTML>