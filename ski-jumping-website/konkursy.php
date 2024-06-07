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
    $q = pg_query($conn, "select * from Konkurs");
    while ($val = pg_fetch_array($q)) {
        echo "<BR><A HREF=\"konkurs.php?id=" .$val['id']. "\" class=\"button\">".$val['organizator']." / " .$val['nazwa']. " / ".$val['data_konkursu']." <A><BR>\n";
    }
    ?>
    <?PHP if($_SESSION['logged'] == 1) : ?>
    <div class="login">
    <H1> Dodaj Konkurs </H1>
    <FORM ACTION="dodajKonkurs.php" METHOD="POST">  
      Nazwa: <INPUT TYPE="TEXT" NAME="NAME" required><BR><BR>
      Data: <INPUT TYPE="DATE" NAME="DAY" required><BR><BR>
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
      Termin Zgłoszeń: <INPUT TYPE="DATE" NAME="TERMIN" required><BR><BR>
      <INPUT TYPE="SUBMIT" VALUE="Zgłoś">
    </FORM>
  </div>
    <?PHP endif ; ?>
    </center>
  </BODY>

</HTML>