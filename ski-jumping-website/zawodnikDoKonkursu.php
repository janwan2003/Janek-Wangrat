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
    $x = $_GET['id'];
    $z = $_GET['opt'];
    $y = $_POST['taskOption'];
    $conn = pg_connect("host=lkdb dbname=bd user=jw440012 password=iks");
    $q = pg_query($conn, "select id from Zgloszenie order by id desc");
    if($val = pg_fetch_array($q))
        $newID = $val['id'] + 1;
    else
        $newID = 0;
    $q = pg_query($conn, "INSERT INTO Zgloszenie VALUES ($newID, CURRENT_DATE, $y, $x)");
    pg_close($conn);
   // echo "$x $y";
    header('Location: startujacy.php?id='.$x.'&opt='.$z.'');
    ?>
    <?PHP endif ; ?>
    </center>
  </BODY>

</HTML>