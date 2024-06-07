#include <iostream>
#include <limits>
#include <math.h>
#include <stdlib.h>
#include <time.h>
#include <vector>
#include "gen.h"

double l;
bool drawing = true; // Do you want the result to be drawn?
const double SQUARE = 10.00;
const double K = 10000;
const double e = 0.1;
const double temperature = 0.999;
int n, m;
std::vector<Node> vertices;
std::vector<int> edges_from[2000];
std::vector<Edge> edges;

void draw_vertices_on_grid() {
    const int GRID_SIZE = 33;
    const char EMPTY_CELL = '.';
    char grid[GRID_SIZE][GRID_SIZE];

    // Initialize grid with empty cells
    for (int i = 0; i < GRID_SIZE; i++) {
        for (int j = 0; j < GRID_SIZE; j++) {
            grid[i][j] = EMPTY_CELL;
        }
    }

    // Place vertices on the grid
    for (size_t i = 0; i < vertices.size(); i++) {
        int row = (int)(vertices[i].x / SQUARE * GRID_SIZE);
        int col = (int)(vertices[i].y / SQUARE * GRID_SIZE);
        if (row < GRID_SIZE && col < GRID_SIZE) {
            grid[row][col] = i + '0';
        }
    }

    // Print the grid
    for (int i = 0; i < GRID_SIZE; i++) {
        for (int j = 0; j < GRID_SIZE; j++) {
            std::cout << grid[i][j] << " ";
        }
        std::cout << std::endl;
    }
}

double scalar(double x, double y) {
  double k = sqrt(x * x + y * y);
  return k;
}

// Repulsion
std::pair<double, double> Frep(int u, int v) {
  double a = vertices[u].x - vertices[v].x;
  double b = vertices[u].y - vertices[v].y;
  double k = (l * l / (scalar(a, b)));
  return {k * a, k * b};
}

// Attraction
std::pair<double, double> Fattr(int u, int v) {
  double a = vertices[v].x - vertices[u].x;
  double b = vertices[v].y - vertices[u].y;
  double k = scalar(a, b) * scalar(a, b) / l;
  return {k * a, k * b};
}

int parse_input(){
  std::cin >> n >> m;
  l = sqrt(SQUARE * SQUARE / n);
  srand(time(NULL));
  int type;
  for (int i = 0; i < n; i++) {
    Node newNode;
    std::cin >> type;
    switch(type){
      case 0: 
        newNode.state_type = Initial;
        break;
      case 1: 
        newNode.state_type = Accepting;
        break;
      case 2:
        newNode.state_type = Default;
        break;
      default:
        printf("Invalid state type\n");
        return -1; 
    }
    newNode.x = SQUARE * ((double)rand() / RAND_MAX);
    newNode.y = SQUARE * ((double)rand() / RAND_MAX);
    newNode.index = i;
    vertices.push_back(newNode);
  }
  int from, to;
  char label;
  for (int i = 0; i < m; i++) {
    std::cin >> from >> to >> label;
    edges_from[from].push_back(to);
    edges_from[to].push_back(from); // for our algorithm the direction doesn't matter
    Edge newEdge;
    newEdge.from = from;
    newEdge.to = to;
    newEdge.label = label;
    edges.push_back(newEdge);
  }
  return 0;
}

void count_positions(){
    double maxF;
  double curr_temp = 1;
  for (int i = 0; i < K; i++) {
    maxF = 0;
    std::vector<std::pair<double, double>> powers;
    for (int k = 0; k < n; k++) {
      curr_temp *= temperature;
      std::pair<double, double> p = {0, 0};
      for (int j = 0; j < n; j++) {
        if (j == k)
          continue;
        p.first += Frep(k, j).first;
        p.second += Frep(k, j).second;
      }
      for (int j : edges_from[k]) {
        p.first += Fattr(k, j).first;
        p.second += Fattr(k, j).second;
      }
      powers.push_back(p);
      double pom = scalar(p.first, p.second);
      maxF = std::max(maxF, pom);
    }
    for (int k = 0; k < n; k++) {
      vertices[k].x += powers[k].first * curr_temp /
                           scalar(powers[k].first, powers[k].second);
      vertices[k].y += powers[k].second * curr_temp /
                            scalar(powers[k].first, powers[k].second);
    }
    if (maxF < e)
      break;
  }
}

void scale(){
    double minx = 100, miny = 100, maxx = 0, maxy = 0;
    for (int i = 0; i < n; i++) {
      minx = std::min(minx, vertices[i].x);
      miny = std::min(miny, vertices[i].y);
      maxx = std::max(maxx, vertices[i].x);
      maxy = std::max(maxy, vertices[i].y);
    }
    double p = maxx - minx;
    double q = maxy - miny;
    double res = std::max(p, q);
    for (int i = 0; i < n; i++) {
      vertices[i].x -= minx;
      vertices[i].y -= miny;
      vertices[i].x *= (SQUARE - 1.0) / res;
      vertices[i].y *= (SQUARE - 1.0) / res;
    }
    for (int i = 0; i < n; i++) {
      std::cout << i << " " << vertices[i].x << " " << vertices[i].y
          << std::endl;
    }
}

int main() {
  int err = parse_input();
  if(err == -1)return -1; //wrong input
  count_positions();
  scale();
  if(drawing){
    draw_vertices_on_grid();
  }
  std::cout<<generate_latex(vertices, edges);
  return 0;
}