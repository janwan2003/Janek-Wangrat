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
    <?PHP
    $conn = pg_connect("host=lkdb dbname=bd user=jw440012 password=iks");
    $q = pg_query($conn, "select id from Konkurs order by id desc");
    $val = pg_fetch_array($q);
    $newID = $val['id'] + 1;
    $q = pg_query($conn, "INSERT INTO Konkurs VALUES ($newID, '" . $_POST['NAME'] . "','".$_POST['NATI']."', '" . $_POST['DAY'] . "', '" . $_POST['TERMIN'] . "')");
    pg_close($conn);
    header('Location: konkursy.php');
    ?>
    <?PHP endif ; ?>
    </center>
  </BODY>

</HTML>