Moduł rysowania automatów. Moduł przyjmuje input w postaci:
<liczba wierzchołków> <liczba krawędzi>
0 1 2 1 ... <-- typy wierzchołków
0 1 a  <-- kolejne krawędzie wraz z etykietami
2 4 b
...
Przykładowe użycia możemy znaleźć w folderze Examples.
W celu debugowania rekomendowane jest ustawienie flagi drawing w main.cpp.

Typy:
Initial - 0
Accepting - 1
Default - 2

Przykładowe użycie wywołujemy komendą:
./main < Examples/test<nr_testu>