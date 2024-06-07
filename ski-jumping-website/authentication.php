<?PHP 
  session_start();
  $_SESSION['LOGIN'] = $_POST['LOGN'];
  $_SESSION['PASS'] = $_POST['PASW'];
  $_SESSION['logged'] = 0;
  $conn = pg_connect("host=lkdb dbname=bd user=jw440012 password=iks");
  $q = pg_query($conn, "select * from Loginy where login ='" . $_SESSION['LOGIN'] . "' and haslo = '" . $_SESSION['PASS'] . "'");
  if ($val = pg_fetch_array($q)) {
    $_SESSION['logged'] = 1;
    echo "dobre dane logowania!";
  }
  if ($_SESSION['logged'] == 0) {
    session_destroy();
    header('Location: index.html');
    echo "Złe dane logowania!";
  }
  header('Location: mainPage.php');
  pg_close($conn);

?>