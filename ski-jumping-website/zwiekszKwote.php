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
    <?PHP
    $x = $_GET['id'];
    $y = $_GET['opt'];
    $conn = pg_connect("host=lkdb dbname=bd user=jw440012 password=iks");
    $q = pg_query($conn, "INSERT INTO Kwota VALUES ($x, '" . $_POST['NATI'] . "', " . $_POST['VALU'] . ")");
    pg_close($conn);
    header('Location: zarzadzajKwotami.php?id='.$x.'&opt='.$y.'');
    ?>
