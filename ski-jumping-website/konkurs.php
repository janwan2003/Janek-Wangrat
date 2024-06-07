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
    echo "<A HREF=\"startujacy.php?id=$x&opt=zawodnik.id\" class=\"button\"> Startujący <A><br>";
    echo "<br><A HREF=\"wyniki.php?id=$x&opt=ocena\" class=\"button\"> Wyniki <A><br>";
    if($_SESSION['logged']==1)
    echo "<br><A HREF=\"rozegrajKonkurs.php?id=$x\" class=\"button\"> Rozegraj Konkurs <A><br>";
    if($_SESSION['logged']==1)
    echo "<br><A HREF=\"zarzadzajKwotami.php?id=$x\" class=\"button\"> Zarządzaj kwotami <A><br>";
    echo "<br><A HREF=\"konkursy.php\" class=\"button\"> Powrót <A><br>";
    pg_close($conn);
    ?>
    <?PHP endif ; ?>
    </center>
  </BODY>

</HTML>