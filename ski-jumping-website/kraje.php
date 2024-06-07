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
    <a href="zawodnicy.php">Zawodnicy</a>
    <a class="active" href="kraje.php">Kraje</a>
    <a href="skryptPostgresql.php">Skrypt PostgreSQL</a>
    </div>
    <?PHP
    $conn = pg_connect("host=lkdb dbname=bd user=jw440012 password=iks");
    $q = pg_query($conn, "select * from Kraj");
    echo "
    <br>
  <table border=\"5\" cellpadding=\"5\" cellspacing=\"0\" style=\"border-  \" bordercolor=\"#808080\" width=\"50&#37;\"    id=\"AutoNumber2\" bgcolor=\"#C0C0C0\">
   <tr> 
  <td width=100>Nazwa kraju</td> 
  </tr>"; 
    while ($val = pg_fetch_array($q)) {
        echo "<tr>";
        echo "<td>" .$val['nazwa']. " </td>";
        echo "</tr>";
    }
    echo "</table>";
    pg_close($conn);
    ?>
    <?PHP if($_SESSION['logged'] == 1) : ?>
    <div class="login">

    <H1> Dodaj Kraj </H1>
    <FORM ACTION="dodajKraj.php" METHOD="POST">  
      Nazwa kraju: <INPUT TYPE="TEXT" NAME="NAME"> <br>
      <INPUT TYPE="SUBMIT" VALUE="Dodaj">
    </FORM>
    </div>
    <?PHP endif ; ?>
    </center>
  </BODY>

</HTML>