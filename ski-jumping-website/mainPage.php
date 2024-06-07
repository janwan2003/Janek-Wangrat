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
    <?PHP if($_SESSION['logged'] != 0 ) : ?>
    <div class="topnav">
    <a class="active" href="mainPage.php">Dom</a>
    <a href="modelUML.php">Model UML</a>
    <a href="konkursy.php">Konkursy</a>
    <a href="zawodnicy.php">Zawodnicy</a>
    <a href="kraje.php">Kraje</a>
    <a href="skryptPostgresql.php">Skrypt PostgreSQL</a>
    </div>
    <p>Zadanie zaliczeniowe z baz danych</p>
    <br>
    Dla ułatwienia testowania funkcjonalności strony pozwoliłem sobie zmienić lekko zasady skoków - seria kwalifikacyjna odbywa się, jeżeli
    co najmniej sześciu zawodników się zgłosi, natomiast do serii finałowej przechodzi jedynie trzech zawodników. Zmiana na domyślne wartości to 
    oczywiście kwestia 10s, ale sądzę, że obecnie łatwiej będzie użytkownikowi zapoznać się z interfejsem. Strona posiada "kafelki":<br>
    Kraje - dodawanie i przeglądanie krajów<br>
    Zawodnicy - dodawanie i przeglądanie zawodników z dodanych krajów<br>
    Konkursy - dodawanie i przeglądanie konkursów, modyfikowanie kwot w konkursach, dodawanie zawodników z listy do konkursów, rozgrywanie konkursów, przeglądanie startujących, przeglądanie wyników<br>
    Konkursy rozgrywane są zgodnie z założeniami - najpierw losowo, potem przeciwnie do kolejności w poprzedniej serii. Aby została zachowana zachowana kolejność program
    mówi nam czyj skok musi się teraz odbyć i tylko tego skoku dane możemy wpisać. Są dodane triggery na wszystkie ewentualne złe inputy. W razie uwag lub bugów proszę
    o mailowy kontakt - przy moich testach wszytko działało jak powinno. <br><br>
    <img src="Adam_Malysz.jpg" alt="Adam_Malysz">
    <br>
    <?PHP endif ; ?>
    </center>
  </BODY>

</HTML>